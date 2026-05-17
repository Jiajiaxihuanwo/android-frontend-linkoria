package com.xinlei.frontend.linkoria.app.websocket.infrastructure.adapter

import com.google.gson.Gson
import com.xinlei.frontend.linkoria.app.core.util.Constants
import com.xinlei.frontend.linkoria.app.websocket.domain.model.StompConnectionState
import com.xinlei.frontend.linkoria.app.websocket.domain.model.WebSocketEvent
import com.xinlei.frontend.linkoria.app.websocket.domain.port.StompClient
import com.xinlei.frontend.linkoria.app.websocket.domain.port.TokenProvider
import com.xinlei.frontend.linkoria.app.websocket.infrastructure.util.StompReconnectionManager
import com.xinlei.frontend.linkoria.app.websocket.infrastructure.util.toFlow
import io.reactivex.CompletableObserver
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.BehaviorSubject
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.reactive.asFlow
import ua.naiksoftware.stomp.Stomp
import ua.naiksoftware.stomp.dto.LifecycleEvent
import ua.naiksoftware.stomp.dto.StompHeader
import ua.naiksoftware.stomp.dto.StompMessage
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject
import javax.inject.Singleton
import ua.naiksoftware.stomp.StompClient as NaikStompClient

@Singleton
class StompClientImpl @Inject constructor(
    private val tokenProvider: TokenProvider,
    private val reconnectionManager: StompReconnectionManager
) : StompClient {

    @Volatile
    private var naikClient: NaikStompClient? = null
    private val disposables = CompositeDisposable()

    // Fix #1: Scope propio para no crear scopes huérfanos
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    // Fix #2: AtomicBoolean para thread-safety
    private val isConnecting = AtomicBoolean(false)

    @Volatile
    private var intentionalDisconnect = false
    private var connectionDeferred: CompletableDeferred<Unit>? = null

    private val connectionStateSubject = BehaviorSubject.createDefault(
        StompConnectionState.DISCONNECTED
    )

    companion object {
        private const val STOMP_URL = Constants.WEBSOCKET_URL
    }

    override suspend fun connect() {
        // Fix #3: Reset intentionalDisconnect al conectar
        intentionalDisconnect = false

        // Fix #2: getAndSet es atómico, evita race condition
        if (isConnecting.getAndSet(true)) return

        try {
            reconnectionManager.executeWithRetry {
                updateConnectionState(StompConnectionState.CONNECTING)

                disposables.clear()
                naikClient?.disconnect()

                connectionDeferred = CompletableDeferred()

                val headers = tokenProvider.getHeaders()
                val stompHeaders = headers.map { StompHeader(it.key, it.value) }

                naikClient = Stomp.over(Stomp.ConnectionProvider.OKHTTP, STOMP_URL).apply {
                    withClientHeartbeat(10000)
                    withServerHeartbeat(10000)
                }

                val lifecycleDisposable = naikClient!!.lifecycle()
                    .subscribe(
                        { event -> handleLifecycleEvent(event) },
                        { error ->
                            updateConnectionState(StompConnectionState.ERROR, error)
                            connectionDeferred?.completeExceptionally(error)
                        }
                    )
                disposables.add(lifecycleDisposable)

                naikClient!!.connect(stompHeaders)
                connectionDeferred?.await()
            }.getOrThrow()
        } catch (e: Exception) {
            updateConnectionState(StompConnectionState.ERROR, e)
        } finally {
            isConnecting.set(false)
            connectionDeferred = null
        }
    }

    private fun handleLifecycleEvent(event: LifecycleEvent) {
        when (event.type) {
            LifecycleEvent.Type.OPENED -> {
                updateConnectionState(StompConnectionState.CONNECTED)
                connectionDeferred?.complete(Unit)
            }
            LifecycleEvent.Type.CLOSED -> {
                updateConnectionState(StompConnectionState.DISCONNECTED)
                connectionDeferred?.completeExceptionally(Exception("Socket closed"))

                if (!intentionalDisconnect && !isConnecting.get()) {
                    // Fix #1: Usar scope propio en lugar de crear uno nuevo cada vez
                    scope.launch { connect() }
                }
            }
            LifecycleEvent.Type.ERROR -> {
                updateConnectionState(StompConnectionState.ERROR, event.exception)
                connectionDeferred?.completeExceptionally(
                    event.exception ?: Exception("Unknown STOMP error")
                )

                if (!intentionalDisconnect && !isConnecting.get()) {
                    scope.launch { connect() }
                }
            }
            else -> {}
        }
    }

    override suspend fun disconnect() {
        intentionalDisconnect = true
        updateConnectionState(StompConnectionState.DISCONNECTING)

        try {
            // Fix #1: Cancelar coroutines de reconexión pendientes
            scope.coroutineContext.cancelChildren()
            naikClient?.disconnect()
            disposables.clear()
            reconnectionManager.reset()
            updateConnectionState(StompConnectionState.DISCONNECTED)
        } catch (e: Exception) {
            updateConnectionState(StompConnectionState.ERROR, e)
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun subscribe(topic: String): Flow<WebSocketEvent> {
        return getConnectionState()
            .filter { it == StompConnectionState.CONNECTED } // Solo cuando esté conectado
            .flatMapLatest {
                // Cada vez que el estado pase a CONNECTED, creamos una nueva suscripción
                // sobre el naikClient actual.
                naikClient?.topic(topic)?.asFlow()?.map<StompMessage, WebSocketEvent> { stompMessage ->
                    WebSocketEvent.Message(
                        payload = stompMessage.payload.toString(),
                        timestamp = java.time.Instant.now()
                    )
                }?.catch { e ->
                    emit(WebSocketEvent.Error(e))
                } ?: emptyFlow()
            }
    }

    override suspend fun send(destination: String, body: Any) {
        val currentClient = naikClient ?: throw IllegalStateException("STOMP client no conectado")
        val json = Gson().toJson(body)

        // Creamos un Observer anónimo para tener control total
        currentClient.send(destination, json).subscribe(object : CompletableObserver {
            private var d: Disposable? = null

            override fun onSubscribe(disposable: Disposable) {
                d = disposable
                disposables.add(disposable) // Se añade al CompositeDisposable global
            }

            override fun onComplete() {
                cleanup()
            }

            override fun onError(e: Throwable) {
                updateConnectionState(StompConnectionState.ERROR, e)
                cleanup()
            }

            private fun cleanup() {
                d?.let { disposables.remove(it) } // Se elimina de forma segura
            }
        })
    }

    override fun getConnectionState(): Flow<StompConnectionState> {
        return connectionStateSubject.toFlow()
    }

    private fun updateConnectionState(
        state: StompConnectionState,
        cause: Throwable? = null
    ) {
        connectionStateSubject.onNext(state)
    }
}
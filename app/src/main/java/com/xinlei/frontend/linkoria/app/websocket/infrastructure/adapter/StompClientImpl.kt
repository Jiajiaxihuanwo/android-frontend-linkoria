package com.xinlei.frontend.linkoria.app.websocket.infrastructure.adapter

import com.google.gson.Gson
import com.xinlei.frontend.linkoria.app.core.util.Constants
import com.xinlei.frontend.linkoria.app.websocket.domain.model.StompConnectionState
import com.xinlei.frontend.linkoria.app.websocket.domain.model.WebSocketEvent
import com.xinlei.frontend.linkoria.app.websocket.domain.port.StompClient
import com.xinlei.frontend.linkoria.app.websocket.domain.port.TokenProvider
import com.xinlei.frontend.linkoria.app.websocket.infrastructure.util.StompReconnectionManager
import com.xinlei.frontend.linkoria.app.websocket.infrastructure.util.toFlow
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.BehaviorSubject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.reactive.asFlow
import ua.naiksoftware.stomp.Stomp
import ua.naiksoftware.stomp.dto.LifecycleEvent
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import javax.inject.Singleton
import ua.naiksoftware.stomp.StompClient as NaikStompClient

@Singleton
class StompClientImpl @Inject constructor(
    private val tokenProvider: TokenProvider,
    private val reconnectionManager: StompReconnectionManager
) : StompClient {

    private var naikClient: NaikStompClient? = null
    private val disposables = CompositeDisposable()

    // Estado de conexión
    private val connectionStateSubject = BehaviorSubject.createDefault(
        StompConnectionState.DISCONNECTED
    )

    // Suscripciones activas por tópico
    private val subscriptions = ConcurrentHashMap<String, Any>()

    companion object {
        private const val STOMP_URL = Constants.WEBSOCKET_URL
        private const val HEARTBEAT_INTERVAL = 30000L
    }

    override suspend fun connect() {
        reconnectionManager.executeWithRetry {
            updateConnectionState(StompConnectionState.CONNECTING)

            // Obtener headers del provider
            val headers = tokenProvider.getHeaders()

            // Preparar headers STOMP
            val stompHeaders = headers.map { (key, value) ->
                ua.naiksoftware.stomp.dto.StompHeader(key, value)
            }

            // Crear cliente STOMP
            naikClient = Stomp.over(
                Stomp.ConnectionProvider.OKHTTP,
                STOMP_URL
            )

            // Escuchar cambios de conexión
            val lifecycleDisposable = naikClient!!.lifecycle()
                .subscribe(
                    { event ->
                        handleLifecycleEvent(event)
                    },
                    { error ->
                        updateConnectionState(StompConnectionState.ERROR, error)
                    }
                )

            disposables.add(lifecycleDisposable)

            // Conectar
            naikClient!!.connect(stompHeaders)
        }.getOrThrow()
    }

    private fun handleLifecycleEvent(event: LifecycleEvent) {
        when (event.type) {
            LifecycleEvent.Type.OPENED -> {
                updateConnectionState(StompConnectionState.CONNECTED)
            }
            LifecycleEvent.Type.CLOSED -> {
                updateConnectionState(StompConnectionState.DISCONNECTED)
            }
            LifecycleEvent.Type.ERROR -> {
                updateConnectionState(StompConnectionState.ERROR, event.exception)
            }

            else -> {}
        }
    }

    /**
     * Desconecta del servidor STOMP
     */
    override suspend fun disconnect() {
        updateConnectionState(StompConnectionState.DISCONNECTING)

        try {
            subscriptions.clear()
            naikClient?.disconnect()
            disposables.clear()
            reconnectionManager.reset()
            updateConnectionState(StompConnectionState.DISCONNECTED)
        } catch (e: Exception) {
            updateConnectionState(StompConnectionState.ERROR, e)
        }
    }

    /**
     * Suscribe a un tópico STOMP y emite eventos como Flow
     *
     * Retorna Flow<WebSocketEvent> (sealed class) con type-safety
     */
    override fun subscribe(topic: String): Flow<WebSocketEvent> {
        return naikClient?.topic(topic)
            ?.asFlow()
            ?.map { stompMessage ->
                WebSocketEvent.Message(
                    payload = stompMessage.payload.toString(),
                    timestamp = java.time.Instant.now()
                )
            }
            ?: throw IllegalStateException("STOMP client no conectado")
    }

    /**
     * Envía un comando STOMP
     */
    override suspend fun send(destination: String, body: Any) {
        if (naikClient == null) {
            throw IllegalStateException("STOMP client no conectado")
        }

        val json = Gson().toJson(body)
        naikClient!!.send(destination, json).subscribe()
    }

    /**
     * Estado de conexión como Flow
     */
    override fun getConnectionState(): Flow<StompConnectionState> {
        return connectionStateSubject.toFlow()
    }

    // ========== PRIVATE HELPERS ==========

    private fun updateConnectionState(
        state: StompConnectionState,
        cause: Throwable? = null
    ) {
        connectionStateSubject.onNext(state)
    }
}
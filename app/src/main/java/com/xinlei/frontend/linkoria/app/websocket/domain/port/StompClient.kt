package com.xinlei.frontend.linkoria.app.websocket.domain.port

import com.xinlei.frontend.linkoria.app.websocket.domain.model.StompConnectionState
import com.xinlei.frontend.linkoria.app.websocket.domain.model.WebSocketEvent
import kotlinx.coroutines.flow.Flow

interface StompClient {
    suspend fun connect()

    suspend fun disconnect()

    fun <T> subscribe(topic: String): Flow<WebSocketEvent<T>>

    suspend fun send(destination: String, body: Any)

    fun getConnectionState(): Flow<StompConnectionState>

    companion object {
        const val CONNECT_TIMEOUT_MS = 5000L
        const val HEARTBEAT_INTERVAL_MS = 30000L
    }
}
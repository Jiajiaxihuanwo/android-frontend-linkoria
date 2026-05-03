package com.xinlei.frontend.linkoria.app.websocket.domain.port

import com.xinlei.frontend.linkoria.app.websocket.domain.model.StompConnectionState
import com.xinlei.frontend.linkoria.app.websocket.domain.model.WebSocketEvent
import kotlinx.coroutines.flow.Flow

interface StompClient {

    /**
     * Conecta al servidor STOMP
     */
    suspend fun connect()

    /**
     * Desconecta del servidor STOMP
     */
    suspend fun disconnect()

    /**
     * Se suscribe a un tópico STOMP
     *
     * @param topic Ruta del tópico (ej: /topic/conversation/1)
     * @return Flow de WebSocketEvent (sealed class)
     */
    fun subscribe(topic: String): Flow<WebSocketEvent>

    /**
     * Envía un comando STOMP
     *
     * @param destination Destino (ej: /app/message/send/1)
     * @param body Payload a serializar
     */
    suspend fun send(destination: String, body: Any)

    /**
     * Obtiene el estado actual de la conexión
     */
    fun getConnectionState(): Flow<StompConnectionState>
}
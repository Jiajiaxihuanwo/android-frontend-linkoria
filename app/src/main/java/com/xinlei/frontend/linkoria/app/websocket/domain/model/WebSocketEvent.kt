package com.xinlei.frontend.linkoria.app.websocket.domain.model

import java.time.Instant

sealed class WebSocketEvent {
    abstract val timestamp: Instant

    /**
     * Evento cuando se recibe un mensaje
     */
    data class Message(
        val payload: String,
        override val timestamp: Instant = Instant.now()
    ) : WebSocketEvent()

    /**
     * Evento cuando se conecta exitosamente
     */
    data class Connected(
        override val timestamp: Instant = Instant.now()
    ) : WebSocketEvent()

    /**
     * Evento cuando se desconecta
     */
    data class Disconnected(
        override val timestamp: Instant = Instant.now()
    ) : WebSocketEvent()

    /**
     * Evento cuando ocurre un error
     */
    data class Error(
        val exception: Throwable,
        override val timestamp: Instant = Instant.now()
    ) : WebSocketEvent()
}

enum class StompConnectionState {
    CONNECTING,
    CONNECTED,
    DISCONNECTING,
    DISCONNECTED,
    ERROR
}

data class StompConnectionEvent(
    val state: StompConnectionState,
    val cause: Throwable? = null,
    val timestamp: Long = System.currentTimeMillis()
)
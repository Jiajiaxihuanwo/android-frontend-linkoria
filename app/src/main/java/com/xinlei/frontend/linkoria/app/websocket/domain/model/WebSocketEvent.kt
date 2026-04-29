package com.xinlei.frontend.linkoria.app.websocket.domain.model

data class WebSocketEvent<T> (
    val type: String,
    val payload: T,
    val timestamp: Long
)

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
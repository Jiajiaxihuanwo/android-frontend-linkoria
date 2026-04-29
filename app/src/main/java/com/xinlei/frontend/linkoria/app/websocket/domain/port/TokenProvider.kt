package com.xinlei.frontend.linkoria.app.websocket.domain.port

interface TokenProvider {
    suspend fun getHeaders(): Map<String, String>
}
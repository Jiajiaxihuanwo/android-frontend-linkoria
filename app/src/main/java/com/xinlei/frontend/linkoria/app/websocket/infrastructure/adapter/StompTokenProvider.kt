package com.xinlei.frontend.linkoria.app.websocket.infrastructure.adapter

import com.xinlei.frontend.linkoria.app.core.session.SessionManager
import com.xinlei.frontend.linkoria.app.websocket.domain.port.TokenProvider
import javax.inject.Inject

class StompTokenProvider @Inject constructor(
    private val sessionManager: SessionManager
) : TokenProvider {
    override suspend fun getHeaders(): Map<String, String> {
        val token = sessionManager.getAccessTokenOnce()
        return if (token != null) {
            mapOf("Authorization" to "Bearer $token")
        } else {
            emptyMap()
        }
    }
}
package com.xinlei.frontend.linkoria.app.websocket.infrastructure.adapter

import com.xinlei.frontend.linkoria.app.core.network.TokenRefreshService
import com.xinlei.frontend.linkoria.app.core.session.SessionManager
import com.xinlei.frontend.linkoria.app.websocket.domain.port.TokenProvider
import javax.inject.Inject

class StompTokenProvider @Inject constructor(
    private val tokenRefreshService: TokenRefreshService
) : TokenProvider {

    override suspend fun getHeaders(): Map<String, String> {
        // getValidToken() ya comprueba expiración y refresca si es necesario
        val token = tokenRefreshService.getValidToken()
            ?: return emptyMap()

        return mapOf("Authorization" to "Bearer $token")
    }
}
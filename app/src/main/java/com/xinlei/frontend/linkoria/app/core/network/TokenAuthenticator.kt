package com.xinlei.frontend.linkoria.app.core.network

import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import javax.inject.Inject

class TokenAuthenticator @Inject constructor(
    private val tokenRefreshService: TokenRefreshService
) : Authenticator {

    override fun authenticate(route: Route?, response: Response): Request? {
        // Ya reintentamos una vez y volvió a fallar → 401 por permisos, no por token
        if (response.priorResponse != null) {
            return null
        }

        // Intentar refresh
        val newToken = runBlocking { tokenRefreshService.refreshAccessToken() }
            ?: return null // Refresh falló, clearSession ya fue llamado

        // Reintentar request original con el nuevo token
        return response.request.newBuilder()
            .header("Authorization", "Bearer $newToken")
            .build()
    }
}
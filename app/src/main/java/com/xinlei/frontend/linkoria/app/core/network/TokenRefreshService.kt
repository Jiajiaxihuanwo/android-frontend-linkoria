package com.xinlei.frontend.linkoria.app.core.network

import com.xinlei.frontend.linkoria.app.auth.data.remote.TokenRefreshApi
import com.xinlei.frontend.linkoria.app.auth.data.remote.dto.RefreshRequest
import com.xinlei.frontend.linkoria.app.core.session.SessionManager
import com.yourcompany.discordclone.app.core.token.TokenManager
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Servicio centralizado de refresh de tokens.
 * Lo usan tanto TokenAuthenticator (REST) como StompTokenProvider (WebSocket).
 * Usa TokenRefreshApi que tiene su propio OkHttpClient limpio (sin interceptores).
 */
@Singleton
class TokenRefreshService @Inject constructor(
    private val refreshApi: TokenRefreshApi,
    private val sessionManager: SessionManager,
    private val tokenManager: TokenManager
) {

    /**
     * Intenta refrescar el accessToken.
     * Retorna el nuevo accessToken si tuvo éxito, null si falló.
     */
    suspend fun refreshAccessToken(): String? {
        // Si el refresh token también expiró, limpiar sesión
        if (tokenManager.isRefreshTokenExpired()) {
            sessionManager.clearSession()
            return null
        }

        val refreshToken = sessionManager.refreshToken.firstOrNull()
            ?: run {
                sessionManager.clearSession()
                return null
            }

        return try {
            val response = refreshApi.refresh(RefreshRequest(refreshToken))

            if (response.isSuccessful) {
                val newTokens = response.body()

                if (newTokens != null) {
                    sessionManager.saveSession(
                        accessToken = newTokens.accessToken,
                        refreshToken = newTokens.refreshToken,
                        refreshTokenExpiresAt = newTokens.refreshTokenExpiresAt.toEpochMilli(),
                        userId = newTokens.userId,
                        username = newTokens.username
                    )
                    newTokens.accessToken
                } else {
                    sessionManager.clearSession()
                    null
                }
            } else {
                sessionManager.clearSession()
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Obtiene un accessToken válido.
     * Si el actual expiró, hace refresh automáticamente.
     * Retorna null si no hay sesión válida.
     */
    suspend fun getValidToken(): String? {
        val currentToken = sessionManager.getAccessTokenOnce()
            ?: return null

        return if (tokenManager.isAccessTokenExpired()) {
            refreshAccessToken()
        } else {
            currentToken
        }
    }
}
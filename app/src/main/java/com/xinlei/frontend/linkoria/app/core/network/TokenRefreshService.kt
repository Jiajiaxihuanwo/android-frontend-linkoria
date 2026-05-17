package com.xinlei.frontend.linkoria.app.core.network

import android.util.Log
import com.xinlei.frontend.linkoria.app.auth.data.remote.TokenRefreshApi
import com.xinlei.frontend.linkoria.app.auth.data.remote.dto.RefreshRequest
import com.xinlei.frontend.linkoria.app.core.session.SessionManager
import com.yourcompany.discordclone.app.core.token.TokenManager
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
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
    private val mutex = Mutex()

    suspend fun refreshAccessToken(): String? = mutex.withLock {
        // Doble check: si otro thread ya refrescó mientras esperábamos el lock
        if (!tokenManager.isAccessTokenExpired()) {
            return@withLock sessionManager.getAccessTokenOnce()
        }

        if (tokenManager.isRefreshTokenExpired()) {
            sessionManager.clearSession()
            return@withLock null
        }

        val refreshToken = sessionManager.refreshToken.firstOrNull()
            ?: run {
                sessionManager.clearSession()
                return@withLock null
            }

        return@withLock try {
            val response = refreshApi.refresh(RefreshRequest(refreshToken))

            if (response.isSuccessful) {
                val newTokens = response.body()
                if (newTokens != null) {
                    sessionManager.saveSession(
                        accessToken = newTokens.accessToken,
                        refreshToken = newTokens.refreshToken,
                        refreshTokenExpiresAt = newTokens.refreshTokenExpiresAt.toEpochMilli(),
                        userId = sessionManager.getUserIdOnce(),
                        username = sessionManager.username.firstOrNull()
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
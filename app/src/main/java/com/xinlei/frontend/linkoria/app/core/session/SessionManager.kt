package com.xinlei.frontend.linkoria.app.core.session

import com.xinlei.frontend.linkoria.app.auth.data.local.TokenDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import javax.inject.Inject

//Fachada de sesión que expone los tokens del DataStore al resto de la app. Es el único punto de contacto para saber si hay sesión activa.
class SessionManager @Inject constructor(
    private val tokenDataStore: TokenDataStore
){
    val accessToken: Flow<String?> = tokenDataStore.accessToken
    val refreshToken: Flow<String?> = tokenDataStore.refreshToken
    val userId: Flow<String?> = tokenDataStore.userId
    val username: Flow<String?> = tokenDataStore.username

    val isLoggedIn: Flow<Boolean> = tokenDataStore.accessToken.map { it != null }

    suspend fun saveSession(
        accessToken: String,
        refreshToken: String,
        refreshTokenExpiresAt: Long? = null,
        userId: String? = null,
        username: String? = null
    ) {
        tokenDataStore.saveTokens(accessToken, refreshToken, refreshTokenExpiresAt, userId, username)
    }

    suspend fun getAccessTokenOnce(): String? = accessToken.firstOrNull()

    suspend fun getUserIdOnce(): String? = userId.firstOrNull()

    suspend fun clearSession() = tokenDataStore.clearTokens()

    suspend fun isRefreshTokenExpired(): Boolean {
        val expiresAt = tokenDataStore.refreshTokenExpiresAt.firstOrNull() ?: return true
        val currentTimeMs = System.currentTimeMillis()
        return currentTimeMs >= expiresAt
    }

    suspend fun isLoggedInOnce(): Boolean {
        return !accessToken.firstOrNull().isNullOrEmpty()
    }
}
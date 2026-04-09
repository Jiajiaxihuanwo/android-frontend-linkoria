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
        userId: String? = null,
        username: String? = null
    ) {
        tokenDataStore.saveTokens(accessToken, refreshToken, userId, username)
    }

    suspend fun getAccessTokenOnce(): String? = tokenDataStore.accessToken.firstOrNull()

    suspend fun getRefreshTokenOnce(): String? = tokenDataStore.accessToken.firstOrNull()

    suspend fun clearSession() = tokenDataStore.clearTokens()
}
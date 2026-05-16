package com.yourcompany.discordclone.app.core.token

import android.util.Base64
import android.util.Log
import com.google.gson.Gson
import com.xinlei.frontend.linkoria.app.auth.data.local.TokenDataStore
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TokenManager @Inject constructor(
    private val tokenDataStore: TokenDataStore,
    private val gson: Gson
) {

    /**
     * Decodifica un JWT y extrae los claims
     */
    private fun decodeToken(token: String): Map<String, Any>? {
        return try {
            val parts = token.split(".")
            if (parts.size != 3) return null

            val payload = parts[1]
            val decoded = Base64.decode(payload, Base64.URL_SAFE)
            val json = String(decoded, Charsets.UTF_8)

            @Suppress("UNCHECKED_CAST")
            gson.fromJson(json, Map::class.java) as? Map<String, Any>
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Obtiene la fecha de expiración del token JWT (exp está en segundos)
     */
    private fun getTokenExpirationTime(token: String): Long? {
        return try {
            val claims = decodeToken(token) ?: return null
            val exp = claims["exp"] as? Number
            exp?.toLong()?.times(1000) // convertir a milisegundos
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Verifica si el access token ha expirado
     * Retorna true si ha expirado, false si es válido
     */
    suspend fun isAccessTokenExpired(): Boolean {
        val token = tokenDataStore.accessToken.firstOrNull() ?: return true
        val expirationTime = getTokenExpirationTime(token) ?: return true

        val currentTimeMs = System.currentTimeMillis()
        // Añadir 60 segundos de buffer para hacer refresh antes de que expire
        return currentTimeMs >= (expirationTime - 60_000)
    }

    /**
     * Verifica si el refresh token ha expirado
     * El refresh token es un UUID opaco, su expiración está en DataStore
     */
    suspend fun isRefreshTokenExpired(): Boolean {
        val expiresAt = tokenDataStore.refreshTokenExpiresAt.firstOrNull() ?: return true
        val currentTimeMs = System.currentTimeMillis()
        return currentTimeMs >= expiresAt
    }

    /**
     * Obtiene el accessToken actual, retorna null si está expirado
     * El interceptor se encargará del refresh
     */
    suspend fun getValidAccessToken(): String? {
        // Si el access token es válido, devolverlo
        if (!isAccessTokenExpired()) {
            return tokenDataStore.accessToken.firstOrNull()
        }

        // Si el refresh token también expiró, limpiar sesión
        if (isRefreshTokenExpired()) {
            tokenDataStore.clearTokens()
            return null
        }

        // El access token expiró pero el refresh es válido
        // Esto debería ser manejado por el interceptor que hará el refresh
        return null
    }
}
package com.xinlei.frontend.linkoria.app.auth.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "auth")

@Singleton
class TokenDataStore @Inject constructor(
    @param:ApplicationContext private val context: Context
) {
    private object Keys {
        val ACCESS_TOKEN = stringPreferencesKey("access_token")
        val REFRESH_TOKEN = stringPreferencesKey("refresh_token")
        val REFRESH_TOKEN_EXPIRES_AT = longPreferencesKey("refresh_token_expires_at")
        val USER_ID = stringPreferencesKey("user_id")
        val USERNAME = stringPreferencesKey("username")
    }

    val accessToken: Flow<String?> = context.dataStore.data.map { it[Keys.ACCESS_TOKEN] }
    val refreshToken: Flow<String?> = context.dataStore.data.map { it[Keys.REFRESH_TOKEN] }
    val refreshTokenExpiresAt: Flow<Long?> = context.dataStore.data.map { it[Keys.REFRESH_TOKEN_EXPIRES_AT] }
    val userId: Flow<String?> = context.dataStore.data.map { it[Keys.USER_ID] }
    val username: Flow<String?> = context.dataStore.data.map { it[Keys.USERNAME] }

    suspend fun saveTokens(
        accessToken: String,
        refreshToken: String,
        refreshTokenExpiresAt: Long? = null,
        userId: String? = null,
        username: String? = null
    ) {
        context.dataStore.edit { preferences ->
            preferences[Keys.ACCESS_TOKEN] = accessToken
            preferences[Keys.REFRESH_TOKEN] = refreshToken
            refreshTokenExpiresAt?.let { preferences[Keys.REFRESH_TOKEN_EXPIRES_AT] = it }
            userId?.let { preferences[Keys.USER_ID] = it }
            username?.let { preferences[Keys.USERNAME] = it }
        }
    }

    suspend fun clearTokens() {
        context.dataStore.edit { it.clear() }
    }
}
package com.xinlei.frontend.linkoria.app.auth.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

//"para cualquier Context, existe una propiedad llamada dataStore que es un DataStore<Preferences> cuya instancia se crea con preferencesDataStore(name = "auth")"
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "auth")

//Capa de persistencia pura: escribe y lee los tokens de disco usando DataStore. No sabe nada del resto de la app.
@Singleton
class TokenDataStore @Inject constructor(
    @param:ApplicationContext private val context: Context
) {
    private object Keys {
        val ACCESS_TOKEN = stringPreferencesKey("access_token")
        val REFRESH_TOKEN = stringPreferencesKey("refresh_token")
        val USER_ID = stringPreferencesKey("user_id")
        val USERNAME = stringPreferencesKey("username")
    }

    val accessToken : Flow<String?> = context.dataStore.data.map { it[Keys.ACCESS_TOKEN] }
    val refreshToken: Flow<String?> = context.dataStore.data.map { it[Keys.REFRESH_TOKEN] }
    val userId: Flow<String?>       = context.dataStore.data.map { it[Keys.USER_ID] }
    val username: Flow<String?>     = context.dataStore.data.map { it[Keys.USERNAME] }

    suspend fun saveTokens(
        accessToken: String,
        refreshToken: String,
        userId: String? = null,
        username: String? = null
    ) {
        context.dataStore.edit { preferences ->
            preferences[Keys.ACCESS_TOKEN] = accessToken
            preferences[Keys.REFRESH_TOKEN] = refreshToken
            userId?.let { preferences[Keys.USER_ID] = it }
            username?.let { preferences[Keys.USERNAME] = it }
        }
    }

    suspend fun clearTokens() {
        context.dataStore.edit { it.clear() }
    }
}
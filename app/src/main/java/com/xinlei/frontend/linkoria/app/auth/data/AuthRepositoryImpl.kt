package com.xinlei.frontend.linkoria.app.auth.data

import com.google.gson.Gson
import com.xinlei.frontend.linkoria.app.auth.data.local.TokenDataStore
import com.xinlei.frontend.linkoria.app.auth.data.remote.AuthApiService
import com.xinlei.frontend.linkoria.app.auth.data.remote.dto.LoginRequest
import com.xinlei.frontend.linkoria.app.auth.data.remote.dto.RefreshRequest
import com.xinlei.frontend.linkoria.app.auth.data.remote.dto.RegisterRequest
import com.xinlei.frontend.linkoria.app.auth.domain.AuthRepository
import com.xinlei.frontend.linkoria.app.auth.domain.AuthUser
import com.xinlei.frontend.linkoria.app.core.network.ApiErrorResponse
import com.xinlei.frontend.linkoria.app.core.network.BaseRepository
import com.xinlei.frontend.linkoria.app.core.network.NetworkResult
import kotlinx.coroutines.flow.firstOrNull
import retrofit2.HttpException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val api : AuthApiService,
    private val tokenDataStore: TokenDataStore
) : AuthRepository, BaseRepository {

    override suspend fun login(email: String, password: String): NetworkResult<AuthUser> =
        safeApiCall {
            val response = api.login(LoginRequest(email, password))
            // Lógica de persistencia integrada en el bloque seguro
            tokenDataStore.saveTokens(
                accessToken = response.accessToken,
                refreshToken = response.refreshToken,
                userId = response.userId,
                username = response.username
            )
            AuthUser(
                userId = response.userId ?: "",
                username = response.username ?: ""
            )
        }

    override suspend fun register(username: String, email: String, password: String): NetworkResult<AuthUser> =
        safeApiCall {
            val response = api.register(RegisterRequest(username, email, password))
            tokenDataStore.saveTokens(
                accessToken = response.accessToken,
                refreshToken = response.refreshToken,
                userId = response.userId,
                username = response.username
            )
            AuthUser(
                userId = response.userId ?: "",
                username = response.username ?: ""
            )
        }

    override suspend fun refresh(): NetworkResult<Unit> = safeApiCall {
        val refreshToken = tokenDataStore.refreshToken.firstOrNull()
            ?: throw Exception("No refresh token")

        val response = api.refresh(RefreshRequest(refreshToken))
        tokenDataStore.saveTokens(
            accessToken = response.accessToken,
            refreshToken = response.refreshToken
        )
    }

    override suspend fun logout(): NetworkResult<Unit> = safeApiCall {
        val refreshToken = tokenDataStore.refreshToken.firstOrNull()
            ?: throw Exception("No refresh token")

        api.logout(RefreshRequest(refreshToken))
        tokenDataStore.clearTokens()
    }
}
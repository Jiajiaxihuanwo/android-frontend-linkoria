package com.xinlei.frontend.linkoria.app.auth.data

import com.xinlei.frontend.linkoria.app.auth.data.local.TokenDataStore
import com.xinlei.frontend.linkoria.app.auth.data.remote.AuthApiService
import com.xinlei.frontend.linkoria.app.auth.data.remote.dto.LoginRequest
import com.xinlei.frontend.linkoria.app.auth.data.remote.dto.RefreshRequest
import com.xinlei.frontend.linkoria.app.auth.data.remote.dto.RegisterRequest
import com.xinlei.frontend.linkoria.app.auth.domain.AuthRepository
import com.xinlei.frontend.linkoria.app.auth.domain.AuthUser
import com.xinlei.frontend.linkoria.app.core.network.NetworkResult
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val api : AuthApiService,
    private val tokenDataStore: TokenDataStore
) : AuthRepository {

    override suspend fun login(
        email: String,
        password: String
    ): NetworkResult<AuthUser> =
        try {
            val response = api.login(LoginRequest(email, password))
            tokenDataStore.saveTokens(
                accessToken = response.accessToken,
                refreshToken = response.refreshToken,
                userId = response.userId,
                username = response.username
            )
            NetworkResult.Success(AuthUser(
                userId = response.userId ?: "",
                username = response.username ?: ""
            ))
        } catch (e: Exception) {
            NetworkResult.Error(null,e.message)
        }

    override suspend fun register(
        username: String,
        email: String,
        password: String
    ): NetworkResult<AuthUser> =
        try {
            val response = api.register(RegisterRequest(username,email,password))
            tokenDataStore.saveTokens(
                accessToken = response.accessToken,
                refreshToken = response.refreshToken,
                userId = response.userId,
                username = response.username
            )
            NetworkResult.Success(AuthUser(
                userId = response.userId ?: "",
                username = response.username ?: ""
            ))
        }catch (e: Exception) {
            NetworkResult.Error(null,e.message)
        }

    override suspend fun refresh(): NetworkResult<Unit> =
        try {
            val refreshToken = tokenDataStore.refreshToken.firstOrNull() ?:
                return NetworkResult.Error(null, "No refresh token")
            val response = api.refresh(RefreshRequest(refreshToken))
            tokenDataStore.saveTokens(
                accessToken = response.accessToken,
                refreshToken = response.refreshToken
            )
            NetworkResult.Success(Unit)
        } catch (e: Exception) {
            NetworkResult.Error(null, e.message)
        }

    override suspend fun logout(): NetworkResult<Unit> =
        try {
            val refreshToken = tokenDataStore.refreshToken.firstOrNull() ?:
                return NetworkResult.Error(null, "No refresh token")
            api.logout(RefreshRequest(refreshToken))
            tokenDataStore.clearTokens()
            NetworkResult.Success(Unit)
        } catch (e: Exception) {
            NetworkResult.Error(null, e.message)
        }
}
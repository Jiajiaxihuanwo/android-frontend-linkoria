package com.xinlei.frontend.linkoria.app.auth.domain

import com.xinlei.frontend.linkoria.app.core.network.NetworkResult

interface AuthRepository {
    suspend fun login(email: String, password: String): NetworkResult<AuthUser>
    suspend fun register(username: String, email: String, password: String): NetworkResult<AuthUser>
    suspend fun refresh(): NetworkResult<Unit>
    suspend fun logout(): NetworkResult<Unit>
}
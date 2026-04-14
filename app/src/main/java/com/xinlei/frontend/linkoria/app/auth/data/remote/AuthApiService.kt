package com.xinlei.frontend.linkoria.app.auth.data.remote

import com.xinlei.frontend.linkoria.app.auth.data.remote.dto.AuthResponse
import com.xinlei.frontend.linkoria.app.auth.data.remote.dto.LoginRequest
import com.xinlei.frontend.linkoria.app.auth.data.remote.dto.RefreshRequest
import com.xinlei.frontend.linkoria.app.auth.data.remote.dto.RegisterRequest
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApiService {

    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): AuthResponse

    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): AuthResponse

    @POST("auth/refresh")
    suspend fun refresh(@Body request: RefreshRequest): AuthResponse

    @POST("auth/logout")
    suspend fun logout(@Body request: RefreshRequest)
}
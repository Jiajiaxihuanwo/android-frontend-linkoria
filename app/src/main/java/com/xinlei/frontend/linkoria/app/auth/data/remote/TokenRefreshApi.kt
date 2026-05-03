package com.xinlei.frontend.linkoria.app.auth.data.remote

import com.xinlei.frontend.linkoria.app.auth.data.remote.dto.AuthResponse
import com.xinlei.frontend.linkoria.app.auth.data.remote.dto.RefreshRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface TokenRefreshApi {
    @POST("auth/refresh")
    suspend fun refresh(@Body request: RefreshRequest): Response<AuthResponse>
}
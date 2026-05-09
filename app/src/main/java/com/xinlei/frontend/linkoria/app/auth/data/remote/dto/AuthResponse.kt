package com.xinlei.frontend.linkoria.app.auth.data.remote.dto

import java.time.Instant

data class AuthResponse(
    val accessToken: String,
    val refreshToken: String,
    val tokenType: String,
    val userId: String?,
    val username: String?,
    val refreshTokenExpiresAt: Instant
)
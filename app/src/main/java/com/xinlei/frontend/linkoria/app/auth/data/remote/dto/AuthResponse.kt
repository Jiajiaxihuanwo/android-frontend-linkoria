package com.xinlei.frontend.linkoria.app.auth.data.remote.dto

data class AuthResponse(
    val accessToken: String,
    val refreshToken: String,
    val tokenType: String,
    val userId: String,
    val username: String
)
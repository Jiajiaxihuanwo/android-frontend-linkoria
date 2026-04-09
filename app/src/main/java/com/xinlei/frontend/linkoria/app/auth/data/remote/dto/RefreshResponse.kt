package com.xinlei.frontend.linkoria.app.auth.data.remote.dto

data class RefreshResponse(
    val accessToken: String,
    val refreshToken: String,
    val tokenType: String
)
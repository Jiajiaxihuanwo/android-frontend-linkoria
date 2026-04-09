package com.xinlei.frontend.linkoria.app.auth.data.remote.dto

data class RegisterRequest(
    val email: String,
    val password: String,
    val username: String
)
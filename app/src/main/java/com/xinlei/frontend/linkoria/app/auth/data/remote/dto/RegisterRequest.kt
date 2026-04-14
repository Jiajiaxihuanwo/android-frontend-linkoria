package com.xinlei.frontend.linkoria.app.auth.data.remote.dto

data class RegisterRequest(
    val username: String,
    val email: String,
    val password: String
)
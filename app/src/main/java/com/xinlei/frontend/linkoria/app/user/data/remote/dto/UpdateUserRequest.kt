package com.xinlei.frontend.linkoria.app.user.data.remote.dto

data class UpdateUserRequest(
    val username: String,
    val email: String,
    val avatarUrl: String
)
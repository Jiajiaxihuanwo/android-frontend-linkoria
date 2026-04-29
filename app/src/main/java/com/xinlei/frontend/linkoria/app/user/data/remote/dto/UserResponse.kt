package com.xinlei.frontend.linkoria.app.user.data.remote.dto

data class UserResponse(
    val id: String,
    val username: String,
    val email: String,
    val avatarUrl: String?,
    val isActive: Boolean,
    val createdAt: String,
    val updatedAt: String
)
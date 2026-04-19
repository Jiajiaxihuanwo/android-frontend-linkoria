package com.xinlei.frontend.linkoria.app.user.domain.model

data class User (
    val id: String,
    val username: String,
    val email: String,
    val avatarUrl: String,
    val initialLetter: String = username.firstOrNull()?.uppercase() ?: "?"
)
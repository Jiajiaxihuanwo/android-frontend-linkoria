package com.xinlei.frontend.linkoria.app.user.domain.model

import java.time.Instant

data class User (
    val id: String,
    val username: String,
    val email: String,
    val avatarUrl: String,
    val bio: String,
    val initialLetter: String = username.firstOrNull()?.uppercase() ?: "?",
    val createdAt: String
)
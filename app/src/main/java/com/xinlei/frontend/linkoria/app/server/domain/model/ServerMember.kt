package com.xinlei.frontend.linkoria.app.server.domain.model

data class ServerMember (
    val userId: String,
    val username: String,
    val avatarUrl: String?,
    val role: ServerRole
)
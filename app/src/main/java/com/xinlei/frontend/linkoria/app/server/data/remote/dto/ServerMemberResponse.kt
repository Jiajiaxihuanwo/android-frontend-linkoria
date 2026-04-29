package com.xinlei.frontend.linkoria.app.server.data.remote.dto

data class ServerMemberResponse(
    val userId: String,
    val username: String,
    val avatarUrl: String?,
    val role: String // Mapearemos esto al Enum
)
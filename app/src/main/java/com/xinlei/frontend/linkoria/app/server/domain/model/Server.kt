package com.xinlei.frontend.linkoria.app.server.domain.model

data class Server (
    val id: Long,
    val name: String,
    val iconUrl: String?,
    val inviteCode: String?,
    val memberCount: Int = 0
)
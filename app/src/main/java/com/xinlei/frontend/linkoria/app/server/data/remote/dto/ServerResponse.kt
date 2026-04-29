package com.xinlei.frontend.linkoria.app.server.data.remote.dto

data class ServerResponse(
    val id: Long,
    val name: String,
    val iconUrl: String?,
    val inviteCode: String?
)
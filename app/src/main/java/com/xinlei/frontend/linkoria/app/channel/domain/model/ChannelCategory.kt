package com.xinlei.frontend.linkoria.app.channel.domain.model

data class ChannelCategory(
    val id: Long,
    val name: String,
    val serverId: Long,
    val channels: List<Channel> = emptyList()
)
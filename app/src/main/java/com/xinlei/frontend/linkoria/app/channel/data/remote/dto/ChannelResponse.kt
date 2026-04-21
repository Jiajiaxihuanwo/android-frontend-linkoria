package com.xinlei.frontend.linkoria.app.channel.data.remote.dto

data class ChannelResponse(
    val id: Long,
    val name: String,
    val serverId: Long,
    val channelCategoryId: Long?
)
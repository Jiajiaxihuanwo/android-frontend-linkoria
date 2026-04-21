package com.xinlei.frontend.linkoria.app.channel.domain.model

data class Channel(
    val id: Long,
    val name: String,
    val serverId: Long,
    val channelCategoryId: Long?
)
package com.xinlei.frontend.linkoria.app.channel.data.remote.dto

data class CreateChannelRequest(
    val name: String,
    val channelCategoryId: Long? = null
)
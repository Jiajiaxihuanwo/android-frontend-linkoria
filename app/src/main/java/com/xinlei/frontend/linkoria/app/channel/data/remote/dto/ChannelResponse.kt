package com.xinlei.frontend.linkoria.app.channel.data.remote.dto

import com.google.gson.annotations.SerializedName

data class ChannelResponse(
    val id: Long,
    val name: String,
    @SerializedName("ServerId") val serverId: Long,
    @SerializedName("ChannelCategoryId") val channelCategoryId: Long?
)
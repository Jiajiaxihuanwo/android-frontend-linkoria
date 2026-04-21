package com.xinlei.frontend.linkoria.app.channel.data.mapper

import com.xinlei.frontend.linkoria.app.channel.data.remote.dto.ChannelCategoryResponse
import com.xinlei.frontend.linkoria.app.channel.data.remote.dto.ChannelResponse
import com.xinlei.frontend.linkoria.app.channel.domain.model.Channel
import com.xinlei.frontend.linkoria.app.channel.domain.model.ChannelCategory

fun ChannelResponse.toDomain(): Channel {
    return Channel(
        id = this.id,
        name = this.name,
        serverId = this.serverId,
        channelCategoryId = this.channelCategoryId
    )
}

fun ChannelCategoryResponse.toDomain(): ChannelCategory {
    return ChannelCategory(
        id = this.id,
        name = this.name,
        serverId = this.serverId,
        channels = emptyList() // Inicialmente vacío, se llena con getChannels si es necesario
    )
}
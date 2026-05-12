package com.xinlei.frontend.linkoria.app.conversation.data.mapper

import com.xinlei.frontend.linkoria.app.conversation.data.remote.dto.ConversationResponse
import com.xinlei.frontend.linkoria.app.conversation.domain.model.Conversation
import com.xinlei.frontend.linkoria.app.conversation.domain.model.ConversationType

fun ConversationResponse.toDomain(): Conversation {
    return Conversation(
        id = this.id,
        // Convertimos el String del JSON al Enum de Dominio
        type = if (this.type == "CHANNEL") ConversationType.CHANNEL else ConversationType.DM,
        targetId = this.targetId,
        targetUsername = this.targetUsername,
        targetIconUrl = this.targetIconUrl,
        channelId = this.channelId
    )
}
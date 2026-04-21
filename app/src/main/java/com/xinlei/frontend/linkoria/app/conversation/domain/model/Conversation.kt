package com.xinlei.frontend.linkoria.app.conversation.domain.model

import java.util.UUID

data class Conversation(
    val id: Long,
    val type: ConversationType,
    val targetUserId: String? = null, // Solo para DMs
    val channelId: Long? = null,    // Solo para canales
)
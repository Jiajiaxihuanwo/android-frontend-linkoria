package com.xinlei.frontend.linkoria.app.conversation.data.remote.dto

data class ConversationResponse(
    val id: Long,
    val type: String,           // Vendrá como "DM" o "CHANNEL"
    val targetUserId: String?,  // Presente si es DM
    val channelId: Long?        // Presente si es CHANNEL
)
package com.xinlei.frontend.linkoria.app.conversation.data.remote.dto

data class ConversationResponse(
    val id: Long,
    val type: String,           // Vendrá como "DM" o "CHANNEL"
    val targetId: String? = null,
    val targetUsername: String? = null,
    val targetIconUrl: String? = null,
    val channelId: Long? = null
)
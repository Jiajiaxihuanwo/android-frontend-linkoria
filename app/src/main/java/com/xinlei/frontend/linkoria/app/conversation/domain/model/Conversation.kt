package com.xinlei.frontend.linkoria.app.conversation.domain.model

data class Conversation(
    val id: Long,
    val type: ConversationType,
    val targetId: String? = null,           // Solo para Dm
    val targetUsername: String? = null,     // Solo para Dm
    val targetIconUrl: String? = null,      // Solo para Dm
    val channelId: Long? = null,    // Solo para canales
)
package com.xinlei.frontend.linkoria.app.message.data.dto.response

import java.time.Instant

data class MessageResponse(
    val messageId: Long,
    val conversationId: Long,
    val userId: String,
    val content: String,
    val messageType: String,
    val replyToMessageId: Long? = null,
    val isEdited: Boolean = false,
    val isReply: Boolean = false,
    val createdAt: Instant,
    val updatedAt: Instant
)
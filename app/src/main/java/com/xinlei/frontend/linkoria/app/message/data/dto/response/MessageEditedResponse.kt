package com.xinlei.frontend.linkoria.app.message.data.dto.response

import java.time.Instant

data class MessageEditedResponse(
    val messageId: Long,
    val conversationId: Long,
    val userId: String,
    val content: String,
    val isEdited: Boolean = true,
    val updatedAt: Instant
)
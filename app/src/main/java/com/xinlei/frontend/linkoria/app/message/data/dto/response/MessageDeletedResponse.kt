package com.xinlei.frontend.linkoria.app.message.data.dto.response

data class MessageDeletedResponse(
    val messageId: Long,
    val conversationId: Long,
    val deleted: Boolean = true
)
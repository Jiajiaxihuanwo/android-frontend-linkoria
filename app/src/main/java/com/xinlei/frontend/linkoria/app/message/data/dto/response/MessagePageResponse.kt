package com.xinlei.frontend.linkoria.app.message.data.dto.response

data class MessagePageResponse(
    val messages: List<MessageResponse>,
    val nextCursor: Long? = null,
    val hasMore: Boolean = false
)
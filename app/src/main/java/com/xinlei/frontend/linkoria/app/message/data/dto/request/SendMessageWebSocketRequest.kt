package com.xinlei.frontend.linkoria.app.message.data.dto.request

data class SendMessageWebSocketRequest(
    val content: String,
    val messageType: String = "TEXT",
    val replyToMessageId: Long? = null
)
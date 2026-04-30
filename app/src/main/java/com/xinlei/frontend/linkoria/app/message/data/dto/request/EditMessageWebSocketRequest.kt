package com.xinlei.frontend.linkoria.app.message.data.dto.request

data class EditMessageWebSocketRequest(
    val messageId: Long,
    val newContent: String
)
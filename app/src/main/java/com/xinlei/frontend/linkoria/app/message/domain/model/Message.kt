package com.xinlei.frontend.linkoria.app.message.domain.model

import java.time.Instant

data class Message(
    val id: Long,
    val conversationId: Long,
    val userId: String,
    val content: String,
    val messageType: MessageType,
    val replyToMessageId: Long? = null,
    val createdAt: Instant,
    val updatedAt: Instant
)

enum class MessageType {
    TEXT,
    IMAGE,
    FILE,
    VIDEO
}

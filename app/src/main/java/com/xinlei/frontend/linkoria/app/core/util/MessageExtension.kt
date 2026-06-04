package com.xinlei.frontend.linkoria.app.core.util

import com.xinlei.frontend.linkoria.app.message.domain.model.Message
import com.xinlei.frontend.linkoria.app.message.domain.model.MessageType

fun Message.formatPreview(
    currentUserId: String,
    senderName: String?
): String {
    val prefix = if (this.userId == currentUserId) {
        "You"
    } else {
        senderName
    }

    val body = when (this.messageType) {
        MessageType.TEXT -> this.content
        MessageType.IMAGE -> "📷 Photo"
        MessageType.FILE -> "📁 File"
        MessageType.VIDEO -> "🎥 Video"
    }

    return "$prefix: $body"
}
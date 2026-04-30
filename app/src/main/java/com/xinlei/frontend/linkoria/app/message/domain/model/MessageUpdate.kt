package com.xinlei.frontend.linkoria.app.message.domain.model

import java.time.Instant

sealed class MessageUpdate {

    data class Created(
        val message: Message
    ) : MessageUpdate()

    data class Edited(
        val messageId: Long,
        val newContent: String,
        val updatedAt: Instant
    ) : MessageUpdate()

    data class Deleted(
        val messageId: Long
    ) : MessageUpdate()

    data class Error(
        val exception: Throwable,
        val messageId: Long? = null
    ) : MessageUpdate()
}
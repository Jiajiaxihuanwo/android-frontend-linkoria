package com.xinlei.frontend.linkoria.app.message.domain.model

data class PagedMessages(
    val messages: List<Message>,
    val nextCursor: Long?,
    val hasMore: Boolean
)
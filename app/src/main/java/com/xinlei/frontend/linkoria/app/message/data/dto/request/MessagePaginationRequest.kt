package com.xinlei.frontend.linkoria.app.message.data.dto.request

data class MessagePaginationRequest(
    val cursor: Long? = null,
    val limit: Int = 50,
    val direction: PaginationDirection = PaginationDirection.BACKWARDS
)

enum class PaginationDirection {
    BACKWARDS,
    FORWARDS
}
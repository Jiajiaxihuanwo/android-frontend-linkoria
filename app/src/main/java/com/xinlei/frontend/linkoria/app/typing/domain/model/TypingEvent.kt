package com.xinlei.frontend.linkoria.app.typing.domain.model

import java.util.UUID

data class TypingEvent(
    val conversationId: Long,
    val userId: UUID,
    val action: TypingAction
)

enum class TypingAction {
    START,
    STOP
}
package com.xinlei.frontend.linkoria.app.message.domain.usecase

import com.xinlei.frontend.linkoria.app.message.domain.repository.MessageRepository
import javax.inject.Inject

class UnsubscribeFromConversationUseCase @Inject constructor(
    private val repository: MessageRepository
) {
    suspend operator fun invoke(conversationId: Long) {
        repository.unsubscribeFromConversation(conversationId)
    }
}
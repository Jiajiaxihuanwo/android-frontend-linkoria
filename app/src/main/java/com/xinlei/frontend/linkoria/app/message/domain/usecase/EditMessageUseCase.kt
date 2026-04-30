package com.xinlei.frontend.linkoria.app.message.domain.usecase

import com.xinlei.frontend.linkoria.app.core.network.NetworkResult
import com.xinlei.frontend.linkoria.app.message.domain.repository.MessageRepository
import javax.inject.Inject

class EditMessageUseCase @Inject constructor(
    private val repository: MessageRepository
) {
    suspend operator fun invoke(
        conversationId: Long,
        messageId: Long,
        newContent: String
    ): NetworkResult<Unit> {
        return repository.editMessage(conversationId, messageId, newContent)
    }
}
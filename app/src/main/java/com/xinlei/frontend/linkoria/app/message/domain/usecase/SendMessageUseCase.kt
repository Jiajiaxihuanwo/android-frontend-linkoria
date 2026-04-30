package com.xinlei.frontend.linkoria.app.message.domain.usecase

import com.xinlei.frontend.linkoria.app.core.network.NetworkResult
import com.xinlei.frontend.linkoria.app.message.domain.model.Message
import com.xinlei.frontend.linkoria.app.message.domain.repository.MessageRepository
import javax.inject.Inject

class SendMessageUseCase @Inject constructor(
    private val repository: MessageRepository
) {
    suspend operator fun invoke(
        conversationId: Long,
        content: String,
        messageType: String = "TEXT",
        replyToMessageId: Long? = null
    ): NetworkResult<Message> {
        return repository.sendMessage(
            conversationId,
            content,
            messageType,
            replyToMessageId
        )
    }
}
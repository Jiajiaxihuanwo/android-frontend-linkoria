package com.xinlei.frontend.linkoria.app.message.domain.usecase

import com.xinlei.frontend.linkoria.app.core.network.NetworkResult
import com.xinlei.frontend.linkoria.app.message.domain.model.Message
import com.xinlei.frontend.linkoria.app.message.domain.repository.MessageRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetLastMessageUseCase @Inject constructor(
    private val repository: MessageRepository
) {
    operator fun invoke(conversationId: Long): Flow<NetworkResult<Message?>> {
        return repository.getLastMessage(conversationId)
    }
}
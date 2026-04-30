package com.xinlei.frontend.linkoria.app.message.domain.usecase

import com.xinlei.frontend.linkoria.app.core.network.NetworkResult
import com.xinlei.frontend.linkoria.app.message.domain.model.MessageUpdate
import com.xinlei.frontend.linkoria.app.message.domain.repository.MessageRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveMessageUpdatesUseCase @Inject constructor(
    private val repository: MessageRepository
) {
    operator fun invoke(conversationId: Long): Flow<NetworkResult<MessageUpdate>> {
        return repository.observeMessageUpdates(conversationId)
    }
}
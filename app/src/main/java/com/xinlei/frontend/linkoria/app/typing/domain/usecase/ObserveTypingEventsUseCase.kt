package com.xinlei.frontend.linkoria.app.typing.domain.usecase

import com.xinlei.frontend.linkoria.app.core.network.NetworkResult
import com.xinlei.frontend.linkoria.app.typing.domain.model.TypingEvent
import com.xinlei.frontend.linkoria.app.typing.domain.repository.TypingRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveTypingEventsUseCase @Inject constructor(
    private val typingRepository: TypingRepository
) {
    operator fun invoke(conversationId: Long): Flow<NetworkResult<TypingEvent>> {
        return typingRepository.observeTypingEvents(conversationId)
    }
}
package com.xinlei.frontend.linkoria.app.typing.domain.usecase

import com.xinlei.frontend.linkoria.app.core.network.NetworkResult
import com.xinlei.frontend.linkoria.app.typing.domain.model.TypingAction
import com.xinlei.frontend.linkoria.app.typing.domain.repository.TypingRepository
import javax.inject.Inject

class SendTypingStartUseCase @Inject constructor(
    private val typingRepository: TypingRepository
) {
    suspend operator fun invoke(conversationId: Long): NetworkResult<Unit> {
        return typingRepository.sendTypingAction(conversationId, TypingAction.START)
    }
}
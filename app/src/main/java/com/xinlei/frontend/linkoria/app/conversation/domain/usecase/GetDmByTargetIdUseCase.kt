package com.xinlei.frontend.linkoria.app.conversation.domain.usecase

import com.xinlei.frontend.linkoria.app.conversation.domain.ConversationRepository
import javax.inject.Inject

class GetDmByTargetIdUseCase @Inject constructor(
    private val repository: ConversationRepository
) {
    operator fun invoke(targetId: String) = repository.getDmByTargetId(targetId)
}
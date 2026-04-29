package com.xinlei.frontend.linkoria.app.conversation.domain.usecase

import com.xinlei.frontend.linkoria.app.conversation.domain.ConversationRepository
import javax.inject.Inject

class GetChannelConversationUseCase @Inject constructor(
    private val repository: ConversationRepository
) {
    operator fun invoke(channelId: Long) = repository.getChannelConversation(channelId)
}
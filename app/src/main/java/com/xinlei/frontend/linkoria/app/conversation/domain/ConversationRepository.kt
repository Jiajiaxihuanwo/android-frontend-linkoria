package com.xinlei.frontend.linkoria.app.conversation.domain

import com.xinlei.frontend.linkoria.app.conversation.domain.model.Conversation
import com.xinlei.frontend.linkoria.app.core.network.NetworkResult
import kotlinx.coroutines.flow.Flow
import java.util.UUID

interface ConversationRepository {

    fun createDm(targetId: String): Flow<NetworkResult<Conversation>>

    fun getMyDms(): Flow<NetworkResult<List<Conversation>>>

    fun getDmByTargetId(targetId: String): Flow<NetworkResult<Conversation>>

    fun getChannelConversation(channelId: Long): Flow<NetworkResult<Conversation>>
}
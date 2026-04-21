package com.xinlei.frontend.linkoria.app.conversation.data

import com.xinlei.frontend.linkoria.app.conversation.data.mapper.toDomain
import com.xinlei.frontend.linkoria.app.conversation.data.remote.ConversationApiService
import com.xinlei.frontend.linkoria.app.conversation.data.remote.dto.CreateDmRequest
import com.xinlei.frontend.linkoria.app.conversation.domain.ConversationRepository
import com.xinlei.frontend.linkoria.app.conversation.domain.model.Conversation
import com.xinlei.frontend.linkoria.app.core.network.BaseRepository
import com.xinlei.frontend.linkoria.app.core.network.NetworkResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class ConversationRepositoryImpl @Inject constructor(
    private val api: ConversationApiService
) : BaseRepository, ConversationRepository {

    override fun createDm(targetId: String): Flow<NetworkResult<Conversation>> = flow {
        emit(NetworkResult.Loading)
        val request = CreateDmRequest(targetId)
        emit(safeApiCall { api.createDm(request).toDomain() })
    }

    override fun getMyDms(): Flow<NetworkResult<List<Conversation>>> = flow {
        emit(NetworkResult.Loading)
        emit(safeApiCall { api.getMyDms().map {it.toDomain()} })
    }

    override fun getDmByTargetId(targetId: String): Flow<NetworkResult<Conversation>> = flow {
        emit(NetworkResult.Loading)
        emit(safeApiCall { api.getDmByTargetId(targetId).toDomain() })
    }

    override fun getChannelConversation(channelId: Long): Flow<NetworkResult<Conversation>> = flow {
        emit(NetworkResult.Loading)
        emit(safeApiCall { api.getChannelConversation(channelId).toDomain() })
    }
}
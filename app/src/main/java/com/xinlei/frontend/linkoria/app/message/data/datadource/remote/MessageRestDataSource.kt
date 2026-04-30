package com.xinlei.frontend.linkoria.app.message.data.datadource.remote

import com.xinlei.frontend.linkoria.app.core.network.BaseDataSource
import com.xinlei.frontend.linkoria.app.core.network.NetworkResult
import com.xinlei.frontend.linkoria.app.message.data.dto.response.MessagePageResponse
import com.xinlei.frontend.linkoria.app.message.data.dto.response.MessageResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class MessageRestDataSource @Inject constructor(
    private val apiService: MessageApiService
): BaseDataSource() {
    fun getMessages(
        conversationId: Long,
        cursor: Long? = null,
        limit: Int = 50
    ): Flow<NetworkResult<MessagePageResponse>> = flow {
        emit(NetworkResult.Loading)
        emit(safeApiCall { apiService.getMessages(
            conversationId = conversationId,
            cursor = cursor,
            limit = limit,
            direction = "BACKWARDS"
        ) })
    }

    fun getLastMessage(
        conversationId: Long
    ): Flow<NetworkResult<MessageResponse>> = flow {
        emit(NetworkResult.Loading)
        emit(safeApiCall{ apiService.getLastMessage(conversationId) })
    }
}
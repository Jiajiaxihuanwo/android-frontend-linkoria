package com.xinlei.frontend.linkoria.app.message.domain.usecase

import com.xinlei.frontend.linkoria.app.core.network.NetworkResult
import com.xinlei.frontend.linkoria.app.message.data.dto.request.PaginationDirection
import com.xinlei.frontend.linkoria.app.message.domain.model.PagedMessages
import com.xinlei.frontend.linkoria.app.message.domain.repository.MessageRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetMessagesUseCase @Inject constructor(
    private val repository: MessageRepository
) {
    operator fun invoke(
        conversationId: Long,
        cursor: Long? = null,
        limit: Int = 150,
        paginationDirection: PaginationDirection = PaginationDirection.BACKWARDS
    ): Flow<NetworkResult<PagedMessages>> {
        return repository.getMessages(conversationId, cursor, limit, paginationDirection)
    }
}
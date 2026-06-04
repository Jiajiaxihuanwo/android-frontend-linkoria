package com.xinlei.frontend.linkoria.app.typing.data.repository

import com.xinlei.frontend.linkoria.app.core.network.NetworkResult
import com.xinlei.frontend.linkoria.app.typing.data.datasource.websocket.TypingWebSocketDataSource
import com.xinlei.frontend.linkoria.app.typing.domain.model.TypingAction
import com.xinlei.frontend.linkoria.app.typing.domain.model.TypingEvent
import com.xinlei.frontend.linkoria.app.typing.domain.repository.TypingRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TypingRepositoryImpl @Inject constructor(
    private val webSocketDataSource: TypingWebSocketDataSource
) : TypingRepository {

    override suspend fun sendTypingAction(
        conversationId: Long,
        action: TypingAction
    ): NetworkResult<Unit> {
        return try {
            webSocketDataSource.sendTypingAction(conversationId, action)
            NetworkResult.Success(Unit)
        } catch (e: Exception) {
            NetworkResult.Error(null, "Error al enviar typing action: ${e.message}")
        }
    }

    override fun observeTypingEvents(conversationId: Long): Flow<NetworkResult<TypingEvent>> {
        return webSocketDataSource.subscribeToTypingEvents(conversationId)
    }
}
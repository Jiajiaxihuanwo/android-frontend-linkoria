package com.xinlei.frontend.linkoria.app.message.data.repository

import com.xinlei.frontend.linkoria.app.core.network.NetworkResult
import com.xinlei.frontend.linkoria.app.message.data.datadource.remote.MessageRestDataSource
import com.xinlei.frontend.linkoria.app.message.data.datadource.websocket.MessageWebSocketDataSource
import com.xinlei.frontend.linkoria.app.message.data.mapper.fromMessageResponse
import com.xinlei.frontend.linkoria.app.message.data.mapper.fromMessageResponses
import com.xinlei.frontend.linkoria.app.message.domain.model.Message
import com.xinlei.frontend.linkoria.app.message.domain.model.MessageType
import com.xinlei.frontend.linkoria.app.message.domain.model.MessageUpdate
import com.xinlei.frontend.linkoria.app.message.domain.repository.MessageRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MessageRepositoryImpl @Inject constructor(
    private val restDataSource: MessageRestDataSource,
    private val webSocketDataSource: MessageWebSocketDataSource
) : MessageRepository {

    private val activeSubscriptions = ConcurrentHashMap<Long, Boolean>()

    override fun getMessages(
        conversationId: Long,
        cursor: Long?,
        limit: Int
    ): Flow<NetworkResult<List<Message>>> {
        return restDataSource.getMessages(conversationId, cursor, limit)
            .map { result ->
                when (result) {
                    is NetworkResult.Success -> {
                        NetworkResult.Success(fromMessageResponses(result.data.messages))
                    }
                    is NetworkResult.Error -> result
                    is NetworkResult.Loading -> result
                }
            }
    }

    override fun getLastMessage(conversationId: Long): Flow<NetworkResult<Message?>> {
        return restDataSource.getLastMessage(conversationId)
            .map { result ->
                when (result) {
                    is NetworkResult.Success -> {
                        NetworkResult.Success(fromMessageResponse(result.data))
                    }
                    is NetworkResult.Error -> result
                    is NetworkResult.Loading -> result
                }
            }
    }

    override fun observeMessageUpdates(conversationId: Long): Flow<NetworkResult<MessageUpdate>> {
        return webSocketDataSource.subscribeToConversationUpdates(conversationId)
    }

    override suspend fun sendMessage(
        conversationId: Long,
        content: String,
        messageType: String,
        replyToMessageId: Long?
    ): NetworkResult<Message> {
        return when (
            val result = webSocketDataSource.sendMessage(
                conversationId,
                content,
                messageType,
                replyToMessageId
            )
        ) {
            is NetworkResult.Success -> {
                NetworkResult.Success(Message(
                    id = 0,
                    conversationId = conversationId,
                    userId = "",
                    content = content,
                    messageType = MessageType.valueOf(
                        messageType
                    ),
                    replyToMessageId = replyToMessageId,
                    createdAt = java.time.Instant.now(),
                    updatedAt = java.time.Instant.now()
                ))
            }
            is NetworkResult.Error -> result
            is NetworkResult.Loading -> result
        }
    }

    override suspend fun editMessage(
        conversationId: Long,
        messageId: Long,
        newContent: String
    ): NetworkResult<Unit> {
        return webSocketDataSource.editMessage(conversationId, messageId, newContent)
    }

    override suspend fun deleteMessage(
        conversationId: Long,
        messageId: Long
    ): NetworkResult<Unit> {
        return webSocketDataSource.deleteMessage(conversationId, messageId)
    }

    override suspend fun subscribeToConversation(conversationId: Long): NetworkResult<Unit> {
        activeSubscriptions[conversationId] = true
        return NetworkResult.Success(Unit)
    }

    override suspend fun unsubscribeFromConversation(conversationId: Long) {
        activeSubscriptions.remove(conversationId)
    }

    override suspend fun clearCache() {
        activeSubscriptions.clear()
    }
}
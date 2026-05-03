package com.xinlei.frontend.linkoria.app.message.domain.repository

import com.xinlei.frontend.linkoria.app.core.network.NetworkResult
import com.xinlei.frontend.linkoria.app.message.domain.model.Message
import com.xinlei.frontend.linkoria.app.message.domain.model.MessageUpdate
import kotlinx.coroutines.flow.Flow

interface MessageRepository {

    fun getMessages(
        conversationId: Long,
        cursor: Long? = null,
        limit: Int = 50
    ): Flow<NetworkResult<List<Message>>>

    fun getLastMessage(conversationId: Long): Flow<NetworkResult<Message?>>

    fun observeMessageUpdates(conversationId: Long): Flow<NetworkResult<MessageUpdate>>

    suspend fun sendMessage(
        conversationId: Long,
        content: String,
        messageType: String = "TEXT",
        replyToMessageId: Long? = null
    ): NetworkResult<Message>

    suspend fun editMessage(
        conversationId: Long,
        messageId: Long,
        newContent: String
    ): NetworkResult<Unit>

    suspend fun deleteMessage(
        conversationId: Long,
        messageId: Long
    ): NetworkResult<Unit>

    suspend fun subscribeToConversation(conversationId: Long): NetworkResult<Unit>

    suspend fun unsubscribeFromConversation(conversationId: Long)

    /**
     * Limpia el caché local de mensajes
     */
    suspend fun clearCache()
}
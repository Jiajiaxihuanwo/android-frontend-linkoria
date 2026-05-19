package com.xinlei.frontend.linkoria.app.message.data.mapper

import com.xinlei.frontend.linkoria.app.message.data.dto.response.MessageResponse
import com.xinlei.frontend.linkoria.app.message.domain.model.Message
import com.xinlei.frontend.linkoria.app.message.domain.model.MessageType
import com.xinlei.frontend.linkoria.app.message.domain.model.PagedMessages

/**
 * Convierte MessageResponse (DTO) a Message (domain model)
 *
 * @param response DTO del servidor
 * @return Message de dominio
 */
fun fromMessageResponse(response: MessageResponse): Message {
    return Message(
        id = response.messageId,
        conversationId = response.conversationId,
        userId = response.userId,
        content = response.content,
        messageType = MessageType.valueOf(response.messageType),
        replyToMessageId = response.replyToMessageId,
        createdAt = response.createdAt,
        updatedAt = response.updatedAt
    )
}

/**
 * Convierte una lista de MessageResponse a una lista de Message
 *
 * @param responses Lista de DTOs del servidor
 * @return Lista de Messages de dominio
 */

fun fromPagedMessagesResponses(responses: List<MessageResponse>, hasMore: Boolean, nextCursor: Long?): PagedMessages {
    val messages = responses.map { fromMessageResponse(it) }
    return PagedMessages(messages = messages, hasMore = hasMore, nextCursor = nextCursor)
}
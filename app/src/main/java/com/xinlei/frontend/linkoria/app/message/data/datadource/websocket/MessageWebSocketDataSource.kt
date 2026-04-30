package com.xinlei.frontend.linkoria.app.message.data.datadource.websocket

import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.xinlei.frontend.linkoria.app.core.network.NetworkResult
import com.xinlei.frontend.linkoria.app.message.data.dto.request.DeleteMessageWebSocketRequest
import com.xinlei.frontend.linkoria.app.message.data.dto.request.EditMessageWebSocketRequest
import com.xinlei.frontend.linkoria.app.message.data.dto.request.SendMessageWebSocketRequest
import com.xinlei.frontend.linkoria.app.message.data.dto.response.MessageDeletedResponse
import com.xinlei.frontend.linkoria.app.message.data.dto.response.MessageEditedResponse
import com.xinlei.frontend.linkoria.app.message.data.dto.response.MessageResponse
import com.xinlei.frontend.linkoria.app.message.data.dto.response.WebSocketEventWrapper
import com.xinlei.frontend.linkoria.app.message.data.mapper.fromMessageResponse
import com.xinlei.frontend.linkoria.app.message.domain.model.Message
import com.xinlei.frontend.linkoria.app.message.domain.model.MessageType
import com.xinlei.frontend.linkoria.app.message.domain.model.MessageUpdate
import com.xinlei.frontend.linkoria.app.websocket.domain.model.WebSocketEvent
import com.xinlei.frontend.linkoria.app.websocket.domain.port.StompClient
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapNotNull
import javax.inject.Inject

class MessageWebSocketDataSource @Inject constructor(
    private val stompClient: StompClient,
    private val gson: Gson
){
    fun subscribeToConversationUpdates(conversationId: Long): Flow<NetworkResult<MessageUpdate>> {
        return stompClient
            .subscribe("/topic/conversation/$conversationId")
            .mapNotNull {  webSocketEvent ->
                when (webSocketEvent) {
                    is WebSocketEvent.Message -> {
                        try {
                            parseWebSocketMessage(webSocketEvent.payload)
                        } catch (e: Exception) {
                            NetworkResult.Error(
                                code = null,
                                message = "Error al parsear evento de WebSocket: ${e.message}"
                            )
                        }
                    }

                    is WebSocketEvent.Error -> {
                        NetworkResult.Error(
                            code = null,
                            message = "Error de WebSocket: ${webSocketEvent.exception.message}"
                        )
                    }

                    is WebSocketEvent.Connected -> {
                        // Ignorar evento de conexión, no es un cambio de mensaje
                        null
                    }

                    is WebSocketEvent.Disconnected -> {
                        // Emitir error cuando se desconecta
                        NetworkResult.Error(
                            code = null,
                            message = "Desconectado del servidor"
                        )
                    }
                }
            }
    }

    suspend fun sendMessage(
        conversationId: Long,
        content: String,
        messageType: String = "TEXT",
        replyToMessageId: Long? = null
    ): NetworkResult<Unit> {
        return try {
            val request = SendMessageWebSocketRequest(
                content = content,
                messageType = messageType,
                replyToMessageId = replyToMessageId
            )
            val payload = gson.toJson(request)
            stompClient.send(
                destination = "/app/message/send/$conversationId",
                body = payload
            )
            NetworkResult.Success(Unit)
        } catch (e: Exception) {
            NetworkResult.Error(
                code = null,
                message = "Error al enviar mensaje: ${e.message}"
            )
        }
    }

    suspend fun editMessage(
        conversationId: Long,
        messageId: Long,
        newContent: String
    ): NetworkResult<Unit> {
        return try {
            val request = EditMessageWebSocketRequest(
                messageId = messageId,
                newContent = newContent
            )
            val payload = gson.toJson(request)
            stompClient.send(
                destination = "/app/message/edit/$conversationId",
                body = payload
            )
            NetworkResult.Success(Unit)
        } catch (e: Exception) {
            NetworkResult.Error(
                code = null,
                message = "Error al editar mensaje: ${e.message}"
            )
        }
    }

    suspend fun deleteMessage(
        conversationId: Long,
        messageId: Long
    ): NetworkResult<Unit> {
        return try {
            val request = DeleteMessageWebSocketRequest(messageId = messageId)
            val payload = gson.toJson(request)
            stompClient.send(
                destination = "/app/message/delete/$conversationId",
                body = payload
            )
            NetworkResult.Success(Unit)
        } catch (e: Exception) {
            NetworkResult.Error(
                code = null,
                message = "Error al eliminar mensaje: ${e.message}"
            )
        }
    }

    /**
     * Parsea un mensaje WebSocket y lo convierte a NetworkResult<MessageUpdate>
     *
     * Manejo polimórfico de eventos:
     * - MESSAGE_CREATED -> MessageUpdate.Created
     * - MESSAGE_EDITED -> MessageUpdate.Edited
     * - MESSAGE_DELETED -> MessageUpdate.Deleted
     *
     * El payload recibido tiene estructura:
     * {
     *   "type": "MESSAGE_CREATED|MESSAGE_EDITED|MESSAGE_DELETED",
     *   "payload": {...},
     *   "timestamp": 1234567890
     * }
     *
     * @param jsonPayload JSON string del mensaje
     * @return NetworkResult<MessageUpdate> con el evento parseado
     */
    private fun parseWebSocketMessage(jsonPayload: String): NetworkResult<MessageUpdate> {
        return try {
            val wrapper = gson.fromJson(jsonPayload, WebSocketEventWrapper::class.java)

            val messageUpdate = when (wrapper.type) {
                "MESSAGE_CREATED" -> {
                    val messageResponse = gson.fromJson(
                        wrapper.payload,
                        MessageResponse::class.java
                    )
                    MessageUpdate.Created(message = fromMessageResponse(messageResponse))
                }

                "MESSAGE_EDITED" -> {
                    val editedResponse = gson.fromJson(
                        wrapper.payload,
                        MessageEditedResponse::class.java
                    )
                    MessageUpdate.Edited(
                        messageId = editedResponse.messageId,
                        newContent = editedResponse.content,
                        updatedAt = editedResponse.updatedAt
                    )
                }

                "MESSAGE_DELETED" -> {
                    val deletedResponse = gson.fromJson(
                        wrapper.payload,
                        MessageDeletedResponse::class.java
                    )
                    MessageUpdate.Deleted(messageId = deletedResponse.messageId)
                }

                else -> {
                    return NetworkResult.Error(
                        code = null,
                        message = "Tipo de evento desconocido: ${wrapper.type}"
                    )
                }
            }

            NetworkResult.Success(messageUpdate)
        } catch (e: JsonSyntaxException) {
            NetworkResult.Error(
                code = null,
                message = "Error al deserializar evento WebSocket: ${e.message}"
            )
        } catch (e: Exception) {
            NetworkResult.Error(
                code = null,
                message = "Error inesperado al procesar evento: ${e.message}"
            )
        }
    }
}
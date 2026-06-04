package com.xinlei.frontend.linkoria.app.typing.data.datasource.websocket

import android.util.Log
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.xinlei.frontend.linkoria.app.core.network.NetworkResult
import com.xinlei.frontend.linkoria.app.message.data.dto.response.WebSocketEventWrapper
import com.xinlei.frontend.linkoria.app.typing.data.dto.request.TypingWebSocketRequest
import com.xinlei.frontend.linkoria.app.typing.data.dto.response.TypingResponse
import com.xinlei.frontend.linkoria.app.typing.domain.model.TypingAction
import com.xinlei.frontend.linkoria.app.typing.domain.model.TypingEvent
import com.xinlei.frontend.linkoria.app.websocket.domain.model.WebSocketEvent
import com.xinlei.frontend.linkoria.app.websocket.domain.port.StompClient
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapNotNull
import java.util.UUID
import javax.inject.Inject

class TypingWebSocketDataSource @Inject constructor(
    private val stompClient: StompClient,
    private val gson: Gson
) {

    fun subscribeToTypingEvents(conversationId: Long): Flow<NetworkResult<TypingEvent>> {
        return stompClient
            .subscribe("/topic/conversation/$conversationId")
            .mapNotNull { webSocketEvent ->
                when (webSocketEvent) {
                    is WebSocketEvent.Message -> {
                        Log.d("STOMP_TYPING", "Raw payload: ${webSocketEvent.payload}")
                        try {
                            parseTypingEvent(webSocketEvent.payload)
                        } catch (e: Exception) {
                            Log.e("STOMP_TYPING", "Error al parsear evento de typing: ${e.message}")
                            NetworkResult.Error(code = null, message = "Error al parsear evento de typing: ${e.message}")
                        }
                    }
                    is WebSocketEvent.Error -> NetworkResult.Error(
                        code = null,
                        message = "Error de WebSocket: ${webSocketEvent.exception.message}"
                    )
                    else -> null
                }
            }
    }

    suspend fun sendTypingAction(conversationId: Long, action: TypingAction) {
        try {
            val request = TypingWebSocketRequest(action = action.name)
            val payload = gson.toJson(request)
            stompClient.send(
                destination = "/app/typing/$conversationId",
                body = payload
            )
        } catch (e: Exception) {
            Log.e("STOMP_TYPING", "Error al enviar typing action: ${e.message}")
        }
    }

    /**
     * Parsea un evento WebSocket y lo convierte a TypingEvent si es de tipo typing.
     *
     * El payload recibido tiene estructura:
     * {
     *   "type": "TYPING_START|TYPING_STOP",
     *   "payload": { "userId": "uuid", "conversationId": 1, "action": "START|STOP" },
     *   "timestamp": 1234567890
     * }
     *
     * Devuelve null si el tipo no es de typing para que mapNotNull lo filtre.
     *
     * @param jsonPayload JSON string del mensaje
     * @return TypingEvent o null si no es un evento de typing
     */
    private fun parseTypingEvent(jsonPayload: String): NetworkResult<TypingEvent>? {
        return try {
            val wrapper = gson.fromJson(jsonPayload, WebSocketEventWrapper::class.java)

            when (wrapper.type) {
                "TYPING_START", "TYPING_STOP" -> {
                    val response = gson.fromJson(wrapper.payload, TypingResponse::class.java)
                    NetworkResult.Success(
                        TypingEvent(
                            conversationId = response.conversationId,
                            userId = UUID.fromString(response.userId),
                            action = if (wrapper.type == "TYPING_START") TypingAction.START else TypingAction.STOP
                        )
                    )
                }
                else -> null
            }
        } catch (e: JsonSyntaxException) {
            NetworkResult.Error(code = null, message = "Error al deserializar evento de typing: ${e.message}")
        }
    }
}
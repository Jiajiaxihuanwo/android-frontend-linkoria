package com.xinlei.frontend.linkoria.app.typing.domain.repository

import com.xinlei.frontend.linkoria.app.core.network.NetworkResult
import com.xinlei.frontend.linkoria.app.typing.domain.model.TypingAction
import com.xinlei.frontend.linkoria.app.typing.domain.model.TypingEvent
import kotlinx.coroutines.flow.Flow

interface TypingRepository {

    /**
     * Envía el estado de escritura al servidor
     */
    suspend fun sendTypingAction(conversationId: Long, action: TypingAction): NetworkResult<Unit>

    /**
     * Observa los eventos de typing de una conversación
     */
    fun observeTypingEvents(conversationId: Long): Flow<NetworkResult<TypingEvent>>
}
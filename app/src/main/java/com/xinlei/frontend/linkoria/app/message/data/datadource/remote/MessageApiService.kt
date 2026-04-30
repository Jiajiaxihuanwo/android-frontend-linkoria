package com.xinlei.frontend.linkoria.app.message.data.datadource.remote

import com.xinlei.frontend.linkoria.app.message.data.dto.response.MessagePageResponse
import com.xinlei.frontend.linkoria.app.message.data.dto.response.MessageResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface MessageApiService {

    @GET("conversations/{conversationId}/messages")
    suspend fun getMessages(
        @Path("conversationId") conversationId: Long,
        @Query("cursor") cursor: Long? = null,
        @Query("limit") limit: Int = 50,
        @Query("direction") direction: String = "BACKWARDS"
    ): MessagePageResponse

    @GET("conversations/{conversationId}/messages/last")
    suspend fun getLastMessage(
        @Path("conversationId") conversationId: Long
    ): MessageResponse
}
package com.xinlei.frontend.linkoria.app.conversation.data.remote

import com.xinlei.frontend.linkoria.app.conversation.data.remote.dto.ConversationResponse
import com.xinlei.frontend.linkoria.app.conversation.data.remote.dto.CreateDmRequest
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ConversationApiService {

    @POST("conversations/dm")
    suspend fun createDm(
        @Body request: CreateDmRequest
    ): ConversationResponse

    @GET("conversations/dm")
    suspend fun getMyDms(): List<ConversationResponse>

    @GET("conversations/dm/{targetId}")
    suspend fun getDmByTargetId(
        @Path("targetId") targetId: String
    ): ConversationResponse

    @GET("conversations/channel/{channelId}")
    suspend fun getChannelConversation(
        @Path("channelId") channelId: Long
    ): ConversationResponse
}
package com.xinlei.frontend.linkoria.app.channel.data.remote

import com.xinlei.frontend.linkoria.app.channel.data.remote.dto.ChannelCategoryResponse
import com.xinlei.frontend.linkoria.app.channel.data.remote.dto.ChannelResponse
import com.xinlei.frontend.linkoria.app.channel.data.remote.dto.CreateChannelCategoryRequest
import com.xinlei.frontend.linkoria.app.channel.data.remote.dto.CreateChannelRequest
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ChannelApiService {

    @POST("servers/{serverId}/channels")
    suspend fun createChannel(
        @Path("serverId") serverId: Long,
        @Body request: CreateChannelRequest
    ): ChannelResponse

    @DELETE("servers/{serverId}/channels/{channelId}")
    suspend fun deleteChannel(
        @Path("serverId") serverId: Long,
        @Path("channelId") channelId: Long
    )

    @GET("servers/{serverId}/channels")
    suspend fun getChannels(
        @Path("serverId") serverId: Long,
        @Query("categoryId") categoryId: Long?
    ): List<ChannelResponse>

    @GET("servers/{serverId}/channels/{channelId}")
    suspend fun getChannelById(
        @Path("serverId") serverId: Long,
        @Path("channelId") channelId: Long
    ): ChannelResponse



    @POST("servers/{serverId}/categories")
    suspend fun createCategory(
        @Path("serverId") serverId: Long,
        @Body request: CreateChannelCategoryRequest
    ): ChannelCategoryResponse

    @DELETE("servers/{serverId}/categories/{categoryId}")
    suspend fun deleteCategory(
        @Path("serverId") serverId: Long,
        @Path("categoryId") categoryId: Long
    )

    @GET("servers/{serverId}/categories/{categoryId}")
    suspend fun getCategoryById(
        @Path("serverId") serverId: Long,
        @Path("categoryId") categoryId: Long
    ): ChannelCategoryResponse

    @GET("servers/{serverId}/categories")
    suspend fun getCategories(
        @Path("serverId") serverId: Long
    ): List<ChannelCategoryResponse>
}
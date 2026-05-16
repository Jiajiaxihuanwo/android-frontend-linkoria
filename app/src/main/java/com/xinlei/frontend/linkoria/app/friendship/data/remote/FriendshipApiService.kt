package com.xinlei.frontend.linkoria.app.friendship.data.remote

import com.xinlei.frontend.linkoria.app.friendship.data.remote.dto.FriendshipResponse
import com.xinlei.frontend.linkoria.app.friendship.data.remote.dto.SendFriendshipRequest
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

interface FriendshipApiService {

    @POST("friendships")
    suspend fun sendFriendshipRequest(
        @Body request: SendFriendshipRequest
    )

    @PATCH("friendships/{targetId}/remove")
    suspend fun removeFriend(
        @Path("targetId") targetId: String
    )

    @PATCH("friendships/{targetId}/decline")
    suspend fun declineFriendship(
        @Path("targetId") targetId: String
    )

    @PATCH("friendships/{targetId}/accept")
    suspend fun acceptFriendship(
        @Path("targetId") targetId: String
    )

    @GET("friendships/pending/sent")
    suspend fun getPendingSentRequests(): List<FriendshipResponse>

    @GET("friendships/pending/received")
    suspend fun getPendingReceivedRequests(): List<FriendshipResponse>

    @GET("friendships/friends")
    suspend fun getFriends(): List<FriendshipResponse>

    @GET("friendships")
    suspend fun getFriendships(): List<FriendshipResponse>
}
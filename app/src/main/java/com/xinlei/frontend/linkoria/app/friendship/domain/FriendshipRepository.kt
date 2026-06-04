package com.xinlei.frontend.linkoria.app.friendship.domain

import com.xinlei.frontend.linkoria.app.core.network.NetworkResult
import com.xinlei.frontend.linkoria.app.friendship.domain.model.Friendship
import kotlinx.coroutines.flow.Flow

interface FriendshipRepository {

    fun getFriends(): Flow<NetworkResult<List<Friendship>>>

    fun getFriendships(): Flow<NetworkResult<List<Friendship>>>

    fun getPendingSentRequests(): Flow<NetworkResult<List<Friendship>>>

    fun getPendingReceivedRequests(): Flow<NetworkResult<List<Friendship>>>

    suspend fun sendFriendshipRequest(targetId: String): NetworkResult<Unit>

    suspend fun acceptFriendship(targetId: String): NetworkResult<Unit>

    suspend fun declineFriendship(targetId: String): NetworkResult<Unit>

    suspend fun removeFriend(targetId: String): NetworkResult<Unit>
}
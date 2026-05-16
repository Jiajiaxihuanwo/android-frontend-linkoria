package com.xinlei.frontend.linkoria.app.friendship.data

import com.xinlei.frontend.linkoria.app.core.network.BaseRepository
import com.xinlei.frontend.linkoria.app.core.network.NetworkResult
import com.xinlei.frontend.linkoria.app.friendship.data.mapper.toDomain
import com.xinlei.frontend.linkoria.app.friendship.data.remote.FriendshipApiService
import com.xinlei.frontend.linkoria.app.friendship.data.remote.dto.SendFriendshipRequest
import com.xinlei.frontend.linkoria.app.friendship.domain.FriendshipRepository
import com.xinlei.frontend.linkoria.app.friendship.domain.model.Friendship
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class FriendshipRepositoryImpl @Inject constructor(
    private val api: FriendshipApiService
) : FriendshipRepository, BaseRepository {

    override fun getFriends(): Flow<NetworkResult<List<Friendship>>> = flow {
        emit(NetworkResult.Loading)
        emit(safeApiCall { api.getFriends().map { it.toDomain() } })
    }

    override fun getFriendships(): Flow<NetworkResult<List<Friendship>>> = flow {
        emit(NetworkResult.Loading)
        emit(safeApiCall { api.getFriendships().map { it.toDomain() } })
    }

    override fun getPendingSentRequests(): Flow<NetworkResult<List<Friendship>>> = flow {
        emit(NetworkResult.Loading)
        emit(safeApiCall { api.getPendingSentRequests().map { it.toDomain() } })
    }

    override fun getPendingReceivedRequests(): Flow<NetworkResult<List<Friendship>>> = flow {
        emit(NetworkResult.Loading)
        emit(safeApiCall { api.getPendingReceivedRequests().map { it.toDomain() } })
    }

    override suspend fun sendFriendshipRequest(targetId: String): NetworkResult<Unit> {
        return safeApiCall {
            api.sendFriendshipRequest(SendFriendshipRequest(targetId))
        }
    }

    override suspend fun acceptFriendship(targetId: String): NetworkResult<Unit> {
        return safeApiCall { api.acceptFriendship(targetId) }
    }

    override suspend fun declineFriendship(targetId: String): NetworkResult<Unit> {
        return safeApiCall { api.declineFriendship(targetId) }
    }

    override suspend fun removeFriend(targetId: String): NetworkResult<Unit> {
        return safeApiCall { api.removeFriend(targetId) }
    }
}
package com.xinlei.frontend.linkoria.app.friendship.domain.usecase

import com.xinlei.frontend.linkoria.app.core.network.NetworkResult
import com.xinlei.frontend.linkoria.app.friendship.domain.FriendshipRepository
import com.xinlei.frontend.linkoria.app.friendship.domain.model.Friendship
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetFriendshipsUseCase @Inject constructor(
    private val repository: FriendshipRepository
) {
    operator fun invoke(): Flow<NetworkResult<List<Friendship>>> {
        return repository.getFriendships()
    }
}
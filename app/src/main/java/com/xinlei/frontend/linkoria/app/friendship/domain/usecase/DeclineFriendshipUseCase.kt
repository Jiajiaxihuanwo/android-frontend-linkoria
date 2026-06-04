package com.xinlei.frontend.linkoria.app.friendship.domain.usecase

import com.xinlei.frontend.linkoria.app.core.network.NetworkResult
import com.xinlei.frontend.linkoria.app.friendship.domain.FriendshipRepository
import javax.inject.Inject

class DeclineFriendshipUseCase @Inject constructor(
    private val repository: FriendshipRepository
) {
    suspend operator fun invoke(targetId: String): NetworkResult<Unit> {
        return repository.declineFriendship(targetId)
    }
}
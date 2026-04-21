package com.xinlei.frontend.linkoria.app.channel.domain.usecase

import com.xinlei.frontend.linkoria.app.channel.domain.ChannelRepository
import com.xinlei.frontend.linkoria.app.core.network.NetworkResult
import javax.inject.Inject

class DeleteCategoryUseCase @Inject constructor(
    private val repository: ChannelRepository
) {
    suspend operator fun invoke(serverId: Long, categoryId: Long): NetworkResult<Unit> {
        return repository.deleteCategory(serverId, categoryId)
    }
}
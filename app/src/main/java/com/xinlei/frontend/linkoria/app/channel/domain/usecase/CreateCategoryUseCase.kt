package com.xinlei.frontend.linkoria.app.channel.domain.usecase

import com.xinlei.frontend.linkoria.app.channel.domain.ChannelRepository
import com.xinlei.frontend.linkoria.app.channel.domain.model.ChannelCategory
import com.xinlei.frontend.linkoria.app.core.network.NetworkResult
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class CreateCategoryUseCase @Inject constructor(
    private val repository: ChannelRepository
) {
    operator fun invoke(serverId: Long, name: String): Flow<NetworkResult<ChannelCategory>> {
        return repository.createCategory(serverId, name)
    }
}
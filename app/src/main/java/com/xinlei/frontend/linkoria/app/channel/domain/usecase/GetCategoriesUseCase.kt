package com.xinlei.frontend.linkoria.app.channel.domain.usecase

import com.xinlei.frontend.linkoria.app.channel.domain.ChannelRepository
import com.xinlei.frontend.linkoria.app.channel.domain.model.ChannelCategory
import com.xinlei.frontend.linkoria.app.core.network.NetworkResult
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetCategoriesUseCase @Inject constructor(
    private val repository: ChannelRepository
) {
    operator fun invoke(serverId: Long): Flow<NetworkResult<List<ChannelCategory>>> {
        return repository.getCategoriesByServer(serverId)
    }
}
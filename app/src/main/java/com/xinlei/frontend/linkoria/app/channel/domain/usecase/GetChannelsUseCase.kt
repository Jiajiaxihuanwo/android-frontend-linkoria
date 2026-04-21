package com.xinlei.frontend.linkoria.app.channel.domain.usecase

import com.xinlei.frontend.linkoria.app.channel.domain.ChannelRepository
import com.xinlei.frontend.linkoria.app.channel.domain.model.Channel
import com.xinlei.frontend.linkoria.app.core.network.NetworkResult
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetChannelsUseCase @Inject constructor(
    private val repository: ChannelRepository
) {
    operator fun invoke(serverId: Long, categoryId: Long? = null): Flow<NetworkResult<List<Channel>>> {
        return repository.getChannels(serverId, categoryId)
    }
}
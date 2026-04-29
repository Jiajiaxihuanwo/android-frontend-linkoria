package com.xinlei.frontend.linkoria.app.channel.domain.usecase

import com.xinlei.frontend.linkoria.app.channel.domain.ChannelRepository
import com.xinlei.frontend.linkoria.app.channel.domain.model.Channel
import com.xinlei.frontend.linkoria.app.core.network.NetworkResult
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetChannelByIdUseCase @Inject constructor(
    private val repository: ChannelRepository
) {
    operator fun invoke(serverId: Long, channelId: Long): Flow<NetworkResult<Channel>> {
        return repository.getChannelById(serverId, channelId)
    }
}
package com.xinlei.frontend.linkoria.app.channel.domain.usecase

import com.xinlei.frontend.linkoria.app.channel.domain.ChannelRepository
import com.xinlei.frontend.linkoria.app.core.network.NetworkResult
import javax.inject.Inject

class DeleteChannelUseCase @Inject constructor(
    private val repository: ChannelRepository
) {
    suspend operator fun invoke(serverId: Long, channelId: Long): NetworkResult<Unit> {
        return repository.deleteChannel(serverId, channelId)
    }
}
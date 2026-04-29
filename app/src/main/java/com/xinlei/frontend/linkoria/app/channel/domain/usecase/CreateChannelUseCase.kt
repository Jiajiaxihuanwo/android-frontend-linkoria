package com.xinlei.frontend.linkoria.app.channel.domain.usecase

import com.xinlei.frontend.linkoria.app.channel.domain.ChannelRepository
import com.xinlei.frontend.linkoria.app.channel.domain.model.Channel
import com.xinlei.frontend.linkoria.app.core.network.NetworkResult
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class CreateChannelUseCase @Inject constructor(
    private val repository: ChannelRepository
) {
    operator fun invoke(serverId: Long, name: String, categoryId: Long? = null): Flow<NetworkResult<Channel>> {
        return repository.createChannel(serverId, name, categoryId)
    }
}
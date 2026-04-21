package com.xinlei.frontend.linkoria.app.server.domain.usecase

import com.xinlei.frontend.linkoria.app.core.network.NetworkResult
import com.xinlei.frontend.linkoria.app.server.domain.ServerRepository
import com.xinlei.frontend.linkoria.app.server.domain.model.ServerMember
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetServerMembersUseCase @Inject constructor(
    private val repository: ServerRepository
) {
    operator fun invoke(serverId: Long): Flow<NetworkResult<List<ServerMember>>> {
        return repository.getServerMembers(serverId)
    }
}
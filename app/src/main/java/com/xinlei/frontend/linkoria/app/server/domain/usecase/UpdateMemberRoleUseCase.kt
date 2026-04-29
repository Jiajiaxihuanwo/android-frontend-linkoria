package com.xinlei.frontend.linkoria.app.server.domain.usecase

import com.xinlei.frontend.linkoria.app.core.network.NetworkResult
import com.xinlei.frontend.linkoria.app.server.domain.ServerRepository
import com.xinlei.frontend.linkoria.app.server.domain.model.ServerMember
import com.xinlei.frontend.linkoria.app.server.domain.model.ServerRole
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class UpdateMemberRoleUseCase @Inject constructor(
    private val repository: ServerRepository
) {
    operator fun invoke(
        serverId: Long,
        userId: String,
        newRole: ServerRole
    ): Flow<NetworkResult<ServerMember>> {
        return repository.updateMemberRole(serverId, userId, newRole)
    }
}
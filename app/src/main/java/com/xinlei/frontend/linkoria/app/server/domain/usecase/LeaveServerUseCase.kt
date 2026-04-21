package com.xinlei.frontend.linkoria.app.server.domain.usecase

import com.xinlei.frontend.linkoria.app.core.network.NetworkResult
import com.xinlei.frontend.linkoria.app.server.domain.ServerRepository
import javax.inject.Inject

class LeaveServerUseCase @Inject constructor(
    private val repository: ServerRepository
) {
    suspend operator fun invoke(serverId: Long): NetworkResult<Unit> {
        return repository.leaveServer(serverId)
    }
}
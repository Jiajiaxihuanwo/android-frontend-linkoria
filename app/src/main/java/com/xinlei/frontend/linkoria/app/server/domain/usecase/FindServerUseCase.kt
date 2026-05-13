package com.xinlei.frontend.linkoria.app.server.domain.usecase

import com.xinlei.frontend.linkoria.app.core.network.NetworkResult
import com.xinlei.frontend.linkoria.app.server.domain.ServerRepository
import com.xinlei.frontend.linkoria.app.server.domain.model.Server
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class FindServerUseCase @Inject constructor(
    private val repository: ServerRepository
) {
    operator fun invoke(inviteCode: String): Flow<NetworkResult<Server>> {
        return repository.findServer(inviteCode)
    }
}
package com.xinlei.frontend.linkoria.app.server.domain.usecase

import com.xinlei.frontend.linkoria.app.core.network.NetworkResult
import com.xinlei.frontend.linkoria.app.server.domain.ServerRepository
import com.xinlei.frontend.linkoria.app.server.domain.model.Server
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class CreateServerUseCase @Inject constructor(
    private val repository: ServerRepository
) {
    operator fun invoke(name: String): Flow<NetworkResult<Server>> {
        return repository.createServer(name)
    }
}
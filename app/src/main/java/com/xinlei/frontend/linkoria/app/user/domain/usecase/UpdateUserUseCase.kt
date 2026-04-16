package com.xinlei.frontend.linkoria.app.user.domain.usecase

import com.xinlei.frontend.linkoria.app.core.network.NetworkResult
import com.xinlei.frontend.linkoria.app.user.data.remote.dto.UpdateUserRequest
import com.xinlei.frontend.linkoria.app.user.domain.UserRepository
import com.xinlei.frontend.linkoria.app.user.domain.model.User
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class UpdateUserUseCase @Inject constructor(
    private val repository: UserRepository
){
    operator fun invoke(
        request: UpdateUserRequest,
        userId: String = ""
    ): Flow<NetworkResult<User>> {
        return repository.updateUser(userId, request)
    }
}
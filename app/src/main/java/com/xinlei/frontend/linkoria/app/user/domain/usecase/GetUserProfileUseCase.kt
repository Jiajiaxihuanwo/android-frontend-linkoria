package com.xinlei.frontend.linkoria.app.user.domain.usecase

import com.xinlei.frontend.linkoria.app.core.network.NetworkResult
import com.xinlei.frontend.linkoria.app.user.domain.UserRepository
import com.xinlei.frontend.linkoria.app.user.domain.model.User
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow

class GetUserProfileUseCase @Inject constructor(
    private val repository: UserRepository
){
    operator fun invoke(): Flow<NetworkResult<User>> {
        return repository.getUserProfile("")
    }
}
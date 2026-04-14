package com.xinlei.frontend.linkoria.app.auth.domain.usecase

import com.xinlei.frontend.linkoria.app.auth.domain.AuthRepository
import com.xinlei.frontend.linkoria.app.auth.domain.AuthUser
import com.xinlei.frontend.linkoria.app.core.network.NetworkResult
import javax.inject.Inject

class RegisterUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(
        username: String,
        email: String,
        password: String
    ): NetworkResult<AuthUser> =
        repository.register(username, email, password)
}
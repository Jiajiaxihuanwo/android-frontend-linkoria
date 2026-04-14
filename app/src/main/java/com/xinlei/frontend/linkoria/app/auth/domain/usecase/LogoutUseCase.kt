package com.xinlei.frontend.linkoria.app.auth.domain.usecase

import com.xinlei.frontend.linkoria.app.auth.domain.AuthRepository
import com.xinlei.frontend.linkoria.app.core.network.NetworkResult
import javax.inject.Inject

class LogoutUseCase @Inject constructor(
    private val repository: AuthRepository
){
    suspend operator fun invoke(): NetworkResult<Unit> =
        repository.logout()
}
package com.xinlei.frontend.linkoria.app.user.domain.usecase

import com.xinlei.frontend.linkoria.app.core.network.NetworkResult
import com.xinlei.frontend.linkoria.app.user.domain.UserRepository
import com.xinlei.frontend.linkoria.app.user.domain.model.User
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SearchUsersUseCase @Inject constructor(
    private val repository: UserRepository
){
    operator fun invoke(query: String): Flow<NetworkResult<List<User>>>{
        return repository.searchUsers(query)
    }
}
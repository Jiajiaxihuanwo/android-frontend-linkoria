package com.xinlei.frontend.linkoria.app.user.domain.usecase

import com.xinlei.frontend.linkoria.app.core.network.NetworkResult
import com.xinlei.frontend.linkoria.app.core.session.SessionManager
import com.xinlei.frontend.linkoria.app.user.domain.UserRepository
import com.xinlei.frontend.linkoria.app.user.domain.model.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class SearchUsersUseCase @Inject constructor(
    private val repository: UserRepository,
    private val sessionManager: SessionManager
){
    operator fun invoke(query: String): Flow<NetworkResult<List<User>>>{
        return repository.searchUsers(query).map { result ->
            when (result) {
                is NetworkResult.Success -> {
                    val filtered = result.data.filter { user ->
                        user.id != sessionManager.getUserIdOnce()
                    }

                    NetworkResult.Success(filtered)
                }

                else -> result
            }
        }
    }
}
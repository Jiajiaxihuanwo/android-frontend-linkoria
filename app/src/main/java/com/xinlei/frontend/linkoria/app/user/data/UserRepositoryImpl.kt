package com.xinlei.frontend.linkoria.app.user.data

import com.xinlei.frontend.linkoria.app.core.network.BaseRepository
import com.xinlei.frontend.linkoria.app.core.network.NetworkResult
import com.xinlei.frontend.linkoria.app.core.session.SessionManager
import com.xinlei.frontend.linkoria.app.user.data.mapper.toDomain
import com.xinlei.frontend.linkoria.app.user.data.remote.UserApiService
import com.xinlei.frontend.linkoria.app.user.data.remote.dto.UpdateUserRequest
import com.xinlei.frontend.linkoria.app.user.domain.UserRepository
import com.xinlei.frontend.linkoria.app.user.domain.model.User
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class UserRepositoryImpl @Inject constructor(
    private val apiService: UserApiService,
    private val sessionManager: SessionManager
) : UserRepository, BaseRepository{

    override fun getUserProfile(userId: String): Flow<NetworkResult<User>> = flow {
        emit(NetworkResult.Loading)
        val result = safeApiCall {
            val targetId = userId.ifBlank {
                sessionManager.getUserIdOnce() ?: throw Exception("Sesión no válida")
            }
            apiService.getUserById(targetId).toDomain()
        }
        emit(result)
    }

    override fun searchUsers(query: String): Flow<NetworkResult<List<User>>> = flow {
        if (query.isBlank()) {
            emit(NetworkResult.Success(emptyList()))
            return@flow
        }
        emit(NetworkResult.Loading)
        val result = safeApiCall {
            apiService.searchUsers(query).map { it.toDomain() }
        }
        emit(result)
    }

    override fun updateUser(
        userId: String,
        request: UpdateUserRequest
    ): Flow<NetworkResult<User>> = flow {
        emit(NetworkResult.Loading)
        val result = safeApiCall {
            val targetId = userId.ifBlank {
                sessionManager.getUserIdOnce() ?: throw Exception("Sesión no válida")
            }
            apiService.updateUser(targetId, request).toDomain()
        }
        emit(result)
    }
}
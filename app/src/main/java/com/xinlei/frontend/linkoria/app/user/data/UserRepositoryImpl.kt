package com.xinlei.frontend.linkoria.app.user.data

import android.util.Log
import com.xinlei.frontend.linkoria.app.core.network.BaseRepository
import com.xinlei.frontend.linkoria.app.core.network.NetworkResult
import com.xinlei.frontend.linkoria.app.core.session.SessionManager
import com.xinlei.frontend.linkoria.app.user.data.mapper.toDomain
import com.xinlei.frontend.linkoria.app.user.data.remote.UserApiService
import com.xinlei.frontend.linkoria.app.user.data.remote.dto.UpdateUserRequest
import com.xinlei.frontend.linkoria.app.user.domain.UserRepository
import com.xinlei.frontend.linkoria.app.user.domain.model.User
import kotlinx.coroutines.delay
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class UserRepositoryImpl @Inject constructor(
    private val apiService: UserApiService,
    private val sessionManager: SessionManager
) : UserRepository, BaseRepository{

    override fun getUserProfile(): Flow<NetworkResult<User>> = flow {
        emit(NetworkResult.Loading)
        delay(500)
        val result = safeApiCall {
            val targetId = sessionManager.getUserIdOnce() ?: throw Exception("Sesión no válida")
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
        request: UpdateUserRequest
    ): Flow<NetworkResult<User>> = flow {
        val result = safeApiCall {
            val targetId = sessionManager.getUserIdOnce() ?: throw Exception("Sesión no válida")
            Log.i("USERID",targetId )
            apiService.updateUser(targetId, request).toDomain()
        }
        emit(result)
    }
}
package com.xinlei.frontend.linkoria.app.user.domain

import com.xinlei.frontend.linkoria.app.core.network.NetworkResult
import com.xinlei.frontend.linkoria.app.user.data.remote.dto.UpdateUserRequest
import com.xinlei.frontend.linkoria.app.user.domain.model.User
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    fun getUserProfile(userId: String): Flow<NetworkResult<User>>

    fun searchUsers(query: String): Flow<NetworkResult<List<User>>>

    fun updateUser(userId: String, request: UpdateUserRequest): Flow<NetworkResult<User>>
}
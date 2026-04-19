package com.xinlei.frontend.linkoria.app.user.data

import com.xinlei.frontend.linkoria.app.core.network.NetworkResult
import com.xinlei.frontend.linkoria.app.core.session.SessionManager
import com.xinlei.frontend.linkoria.app.user.data.mapper.toDomain
import com.xinlei.frontend.linkoria.app.user.data.remote.UserApiService
import com.xinlei.frontend.linkoria.app.user.data.remote.dto.UpdateUserRequest
import com.xinlei.frontend.linkoria.app.user.domain.UserRepository
import com.xinlei.frontend.linkoria.app.user.domain.model.User
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class UserRepositoryImpl @Inject constructor(
    private val apiService: UserApiService,
    private val sessionManager: SessionManager
) : UserRepository{

    override fun getUserProfile(userId: String): Flow<NetworkResult<User>> = flow {
        emit(NetworkResult.Loading)
        try {
            val targetId = userId.ifBlank { sessionManager.getUserIdOnce() }

            if (targetId.isNullOrBlank()) {
                emit(NetworkResult.Error(404,"No se encontró una sesión de usario válida"))
                return@flow
            }

            val response = apiService.getUserById(targetId)
            if (response.isSuccessful) {
                response.body()?.let {
                    emit(NetworkResult.Success(it.toDomain()))
                } ?: emit(NetworkResult.Error(204,"El servidor respondió con datos vacíos"))
            } else {
                emit(NetworkResult.Error(response.code(),"Error en el servidor (Código: ${response.code()})"))
            }
        } catch (e: Exception) {
            emit(NetworkResult.Error(null,e.message))
        }
    }

    override fun searchUsers(query: String): Flow<NetworkResult<List<User>>> = flow {
        if(query.isBlank()) {
            emit(NetworkResult.Success(emptyList()))
            return@flow
        }

        emit(NetworkResult.Loading)
        try {
            val response = apiService.searchUsers(query)
            if(response.isSuccessful) {
                val users = response.body()?.map { it.toDomain() } ?: emptyList()
                emit(NetworkResult.Success(users))
            } else {
                emit(NetworkResult.Error(null,"Error en la búsqueda"))
            }
        } catch (e: Exception) {
            emit(NetworkResult.Error(null, e.message))
        }
    }

    override fun updateUser(
        userId: String,
        request: UpdateUserRequest
    ): Flow<NetworkResult<User>> = flow{
        emit(NetworkResult.Loading)
        try {
            val targetId = userId.ifBlank { sessionManager.getUserIdOnce() }

            if (targetId.isNullOrBlank()) {
                emit(NetworkResult.Error(404,"No se encontró una sesión de usario válida"))
                return@flow
            }

            val response = apiService.updateUser(targetId, request)
            if (response.isSuccessful) {
                response.body()?.let {
                    emit(NetworkResult.Success(it.toDomain()))
                } ?: emit(NetworkResult.Error(null,"Error al actualizar: respuesta sin cuerpo"))
            } else {
                emit(NetworkResult.Error(null,"La actualización del perfil falló en el servidor"))
            }
        } catch (e: Exception) {
            emit(NetworkResult.Error(null, e.message))
        }
    }
}
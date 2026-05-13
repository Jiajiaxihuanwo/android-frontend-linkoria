package com.xinlei.frontend.linkoria.app.server.data

import com.xinlei.frontend.linkoria.app.core.network.BaseRepository
import com.xinlei.frontend.linkoria.app.core.network.NetworkResult
import com.xinlei.frontend.linkoria.app.server.data.mapper.toDomain
import com.xinlei.frontend.linkoria.app.server.data.remote.ServerApiService
import com.xinlei.frontend.linkoria.app.server.data.remote.dto.CreateServerRequest
import com.xinlei.frontend.linkoria.app.server.data.remote.dto.JoinServerRequest
import com.xinlei.frontend.linkoria.app.server.data.remote.dto.UpdateMemberRoleRequest
import com.xinlei.frontend.linkoria.app.server.data.remote.dto.UpdateServerRequest
import com.xinlei.frontend.linkoria.app.server.domain.ServerRepository
import com.xinlei.frontend.linkoria.app.server.domain.model.Server
import com.xinlei.frontend.linkoria.app.server.domain.model.ServerMember
import com.xinlei.frontend.linkoria.app.server.domain.model.ServerRole
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class ServerRepositoryImpl @Inject constructor(
    private val api: ServerApiService
) : ServerRepository, BaseRepository {

    override fun getServer(serverId: Long): Flow<NetworkResult<Server>> = flow {
        emit(NetworkResult.Loading)
        emit(safeApiCall { api.getServer(serverId).toDomain() })
    }

    override fun getJoinedServers(): Flow<NetworkResult<List<Server>>> = flow {
        emit(NetworkResult.Loading)
        emit(safeApiCall { api.getServers().map { it.toDomain() } })
    }

    override fun findServer(inviteCode: String): Flow<NetworkResult<Server>> = flow {
        emit(NetworkResult.Loading)
        emit(safeApiCall { api.findServer(inviteCode).toDomain() })
    }

    override fun updateServer(
        serverId: Long,
        name: String?,
        iconUrl: String?
    ): Flow<NetworkResult<Server>> = flow {
        emit(NetworkResult.Loading)
        val request = UpdateServerRequest(name = name, iconUrl = iconUrl)
        emit(safeApiCall { api.updateServer(serverId, request).toDomain() })
    }

    override fun createServer(name: String, serverIcon: String?): Flow<NetworkResult<Server>> = flow {
        emit(NetworkResult.Loading)
        emit(safeApiCall { api.createServer(CreateServerRequest(name, serverIcon)).toDomain() })
    }

    override fun joinServer(inviteCode: String): Flow<NetworkResult<Server>> = flow {
        emit(NetworkResult.Loading)
        emit(safeApiCall { api.joinServer(JoinServerRequest(inviteCode)).toDomain() })
    }

    override fun getServerMembers(serverId: Long): Flow<NetworkResult<List<ServerMember>>> = flow {
        emit(NetworkResult.Loading)
        emit(safeApiCall { api.getServerMembers(serverId).map { it.toDomain() } })
    }

    override suspend fun kickMember(
        serverId: Long,
        userId: String
    ): NetworkResult<Unit> {
        return safeApiCall { api.kickMember(serverId, userId) }
    }

    override fun updateMemberRole(
        serverId: Long,
        userId: String,
        newRole: ServerRole
    ): Flow<NetworkResult<ServerMember>> = flow {
        emit(NetworkResult.Loading)
        val request = UpdateMemberRoleRequest(newRole.name) // Convertimos el Enum a String
        emit(safeApiCall { api.updateMemberRole(serverId, userId, request).toDomain() })
    }

    override suspend fun leaveServer(serverId: Long): NetworkResult<Unit> {
        return safeApiCall { api.leaveServer(serverId) }
    }

    override suspend fun deleteServer(serverId: Long): NetworkResult<Unit> {
        return safeApiCall { api.deleteServer(serverId) }
    }
}
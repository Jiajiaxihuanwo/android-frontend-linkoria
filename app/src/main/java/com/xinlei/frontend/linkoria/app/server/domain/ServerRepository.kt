package com.xinlei.frontend.linkoria.app.server.domain

import com.xinlei.frontend.linkoria.app.core.network.NetworkResult
import com.xinlei.frontend.linkoria.app.server.domain.model.Server
import com.xinlei.frontend.linkoria.app.server.domain.model.ServerMember
import com.xinlei.frontend.linkoria.app.server.domain.model.ServerRole
import kotlinx.coroutines.flow.Flow

interface ServerRepository {
    fun createServer(name: String, serverIcon: String?): Flow<NetworkResult<Server>>
    fun getServer(serverId: Long): Flow<NetworkResult<Server>>
    fun getJoinedServers(): Flow<NetworkResult<List<Server>>>
    fun findServer(inviteCode: String): Flow<NetworkResult<Server>>
    fun updateServer(serverId: Long, name: String?, iconUrl: String?): Flow<NetworkResult<Server>>
    fun joinServer(inviteCode: String): Flow<NetworkResult<Server>>
    suspend fun leaveServer(serverId: Long): NetworkResult<Unit>
    suspend fun deleteServer(serverId: Long): NetworkResult<Unit>

    fun getServerMembers(serverId: Long): Flow<NetworkResult<List<ServerMember>>>
    suspend fun kickMember(serverId: Long, userId: String): NetworkResult<Unit>
    fun updateMemberRole(
        serverId: Long,
        userId: String,
        newRole: ServerRole
    ): Flow<NetworkResult<ServerMember>>
}
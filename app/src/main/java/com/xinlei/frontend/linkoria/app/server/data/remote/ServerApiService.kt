package com.xinlei.frontend.linkoria.app.server.data.remote

import com.xinlei.frontend.linkoria.app.server.data.remote.dto.CreateServerRequest
import com.xinlei.frontend.linkoria.app.server.data.remote.dto.JoinServerRequest
import com.xinlei.frontend.linkoria.app.server.data.remote.dto.ServerMemberResponse
import com.xinlei.frontend.linkoria.app.server.data.remote.dto.ServerResponse
import com.xinlei.frontend.linkoria.app.server.data.remote.dto.UpdateMemberRoleRequest
import com.xinlei.frontend.linkoria.app.server.data.remote.dto.UpdateServerRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

interface ServerApiService {

    // --- Servidores (ServerController) ---

    @POST("servers")
    suspend fun createServer(@Body request: CreateServerRequest): ServerResponse

    @GET("servers/{serverId}")
    suspend fun getServer(@Path("serverId") serverId: Long): ServerResponse

    @GET("servers")
    suspend fun getServers(): List<ServerResponse>

    @PATCH("servers/{serverId}")
    suspend fun updateServer(
        @Path("serverId") serverId: Long,
        @Body request: UpdateServerRequest
    ): ServerResponse

    @POST("servers/join")
    suspend fun joinServer(@Body request: JoinServerRequest): ServerResponse

    @DELETE("servers/{serverId}/leave")
    suspend fun leaveServer(@Path("serverId") serverId: Long)

    @DELETE("servers/{serverId}")
    suspend fun deleteServer(@Path("serverId") serverId: Long)


    // --- Miembros (ServerMemberController) ---

    @GET("servers/{serverId}/members")
    suspend fun getServerMembers(@Path("serverId") serverId: Long): List<ServerMemberResponse>

    @DELETE("servers/{serverId}/members/{userId}")
    suspend fun kickMember(
        @Path("serverId") serverId: Long,
        @Path("userId") userId: String
    )

    @PATCH("servers/{serverId}/members/{userId}/role")
    suspend fun updateMemberRole(
        @Path("serverId") serverId: Long,
        @Path("userId") userId: String,
        @Body request: UpdateMemberRoleRequest
    ): ServerMemberResponse
}
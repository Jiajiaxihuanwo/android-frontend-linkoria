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

    @POST("api/v1/servers")
    suspend fun createServer(@Body request: CreateServerRequest): ServerResponse

    @GET("api/v1/servers/{serverId}")
    suspend fun getServer(@Path("serverId") serverId: Long): ServerResponse

    @GET("api/v1/servers")
    suspend fun getServers(): List<ServerResponse>

    @PATCH("api/v1/servers/{serverId}")
    suspend fun updateServer(
        @Path("serverId") serverId: Long,
        @Body request: UpdateServerRequest
    ): ServerResponse

    @POST("api/v1/servers/join")
    suspend fun joinServer(@Body request: JoinServerRequest): ServerResponse

    @DELETE("api/v1/servers/{serverId}/leave")
    suspend fun leaveServer(@Path("serverId") serverId: Long)

    @DELETE("api/v1/servers/{serverId}")
    suspend fun deleteServer(@Path("serverId") serverId: Long)


    // --- Miembros (ServerMemberController) ---

    @GET("api/v1/servers/{serverId}/members")
    suspend fun getServerMembers(@Path("serverId") serverId: Long): List<ServerMemberResponse>

    @DELETE("api/v1/servers/{serverId}/members/{userId}")
    suspend fun kickMember(
        @Path("serverId") serverId: Long,
        @Path("userId") userId: String
    )

    @PATCH("api/v1/servers/{serverId}/members/{userId}/role")
    suspend fun updateMemberRole(
        @Path("serverId") serverId: Long,
        @Path("userId") userId: String,
        @Body request: UpdateMemberRoleRequest
    ): ServerMemberResponse
}
package com.xinlei.frontend.linkoria.app.user.data.remote

import com.xinlei.frontend.linkoria.app.user.data.remote.dto.UpdateUserRequest
import com.xinlei.frontend.linkoria.app.user.data.remote.dto.UserResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.Path
import retrofit2.http.Query

interface UserApiService {

    @GET("users/{userId}")
    suspend fun getUserById(
        @Path("userId") userId: String
    ): UserResponse

    @PATCH("users/{userId}")
    suspend fun updateUser(
        @Path("userId") userId: String,
        @Body updateRequest: UpdateUserRequest
    ): UserResponse

    @GET("users/search")
    suspend fun searchUsers(
        @Query("username") username: String
    ): List<UserResponse>
}
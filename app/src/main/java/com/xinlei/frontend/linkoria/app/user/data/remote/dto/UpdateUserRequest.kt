package com.xinlei.frontend.linkoria.app.user.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UpdateUserRequest(
    @SerialName("username")
    val username: String? = null,
    @SerialName("email")
    val email: String? = null,
    @SerialName("avatarUrl")
    val avatarUrl: String? = null,
    @SerialName("bio")
    val bio: String? = null
)
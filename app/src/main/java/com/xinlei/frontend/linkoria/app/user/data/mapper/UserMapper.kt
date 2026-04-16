package com.xinlei.frontend.linkoria.app.user.data.mapper

import com.xinlei.frontend.linkoria.app.user.data.remote.dto.UserResponse
import com.xinlei.frontend.linkoria.app.user.domain.model.User

fun UserResponse.toDomain(): User {
    return User(
        id = this.id,
        username = this.username,
        email = this.email,
        avatarUrl = this.avatarUrl ?: ""
    )
}
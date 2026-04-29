package com.xinlei.frontend.linkoria.app.server.data.mapper

import com.xinlei.frontend.linkoria.app.server.data.remote.dto.ServerMemberResponse
import com.xinlei.frontend.linkoria.app.server.data.remote.dto.ServerResponse
import com.xinlei.frontend.linkoria.app.server.domain.model.Server
import com.xinlei.frontend.linkoria.app.server.domain.model.ServerMember
import com.xinlei.frontend.linkoria.app.server.domain.model.ServerRole
import java.util.UUID

fun ServerResponse.toDomain(): Server {
    return Server(
        id = this.id,
        name = this.name,
        iconUrl = this.iconUrl,
        inviteCode = this.inviteCode
    )
}

fun ServerMemberResponse.toDomain(): ServerMember {
    return ServerMember(
        userId = this.userId,
        username = this.username,
        avatarUrl = this.avatarUrl,
        role = try {
            ServerRole.valueOf(this.role.uppercase())
        } catch (e: IllegalArgumentException) {
            ServerRole.MEMBER
        }
    )
}
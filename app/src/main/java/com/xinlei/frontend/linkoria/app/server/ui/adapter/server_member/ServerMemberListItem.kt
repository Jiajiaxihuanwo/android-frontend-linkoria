package com.xinlei.frontend.linkoria.app.server.ui.adapter.server_member

import com.xinlei.frontend.linkoria.app.server.domain.model.ServerMember

sealed class ServerMemberListItem {
    data class Header(val roleName: String, val count: Int) : ServerMemberListItem()
    data class Member(val serverMember: ServerMember) : ServerMemberListItem()
}
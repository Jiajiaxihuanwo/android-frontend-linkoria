package com.xinlei.frontend.linkoria.app.server.ui.adapter.server_member

import androidx.recyclerview.widget.DiffUtil

class ServerMemberDiffCallback : DiffUtil.ItemCallback<ServerMemberListItem>() {
    override fun areItemsTheSame(old: ServerMemberListItem, new: ServerMemberListItem): Boolean {
        return when {
            old is ServerMemberListItem.Header && new is ServerMemberListItem.Header -> old.roleName == new.roleName
            old is ServerMemberListItem.Member && new is ServerMemberListItem.Member -> old.serverMember.userId == new.serverMember.userId
            else -> false
        }
    }

    override fun areContentsTheSame(old: ServerMemberListItem, new: ServerMemberListItem) = old == new
}
package com.xinlei.frontend.linkoria.app.message.ui.navigation

import android.content.Context
import android.content.Intent
import com.xinlei.frontend.linkoria.app.conversation.ui.dm.friendprofile.FriendProfile
import com.xinlei.frontend.linkoria.app.server.ui.ServerMemberActivity
import javax.inject.Inject

class ChatNavigatorImpl @Inject constructor() : ChatNavigator {

    override fun openProfile(context: Context, chatType: String, targetId: String?, serverId: Long) {
        when (chatType) {
            ChatArgs.Companion.TYPE_DM -> {
                if (targetId.isNullOrEmpty()) return
                Intent(context, FriendProfile::class.java).apply {
                    putExtra("extra_user_id", targetId)
                    context.startActivity(this)
                }
            }
            ChatArgs.Companion.TYPE_CHANNEL -> {
                if (serverId == -1L) return
                Intent(context, ServerMemberActivity::class.java).apply {
                    putExtra("extra_server_id", serverId)
                    context.startActivity(this)
                }
            }
        }
    }
}
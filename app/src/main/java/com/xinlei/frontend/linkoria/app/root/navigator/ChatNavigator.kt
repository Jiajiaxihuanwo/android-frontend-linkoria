package com.xinlei.frontend.linkoria.app.root.navigator

import android.app.Activity
import android.app.TaskStackBuilder
import android.content.Intent
import com.xinlei.frontend.linkoria.app.chat.ui.ChatActivity
import com.xinlei.frontend.linkoria.app.root.MainActivity
import javax.inject.Inject

class ChatNavigator @Inject constructor() {

    fun openDmChat(
        activity: Activity,
        conversationId: Long,
        targetId: String
    ) {
        TaskStackBuilder.create(activity).apply {
            addNextIntent(Intent(activity, MainActivity::class.java).apply {
                putExtra(MainActivity.EXTRA_OPEN_DM_LIST, true)
            })
            addNextIntent(Intent(activity, ChatActivity::class.java).apply {
                putExtra(ChatActivity.EXTRA_CHAT_TYPE, ChatActivity.TYPE_DM)
                putExtra(ChatActivity.EXTRA_CONVERSATION_ID, conversationId)
                putExtra(ChatActivity.EXTRA_TARGET_ID, targetId)
            })
        }.startActivities()
    }

    fun openChannelChat(
        activity: Activity,
        serverId: Long,
        channelId: Long
    ) {
        val intent = Intent(activity, ChatActivity::class.java).apply {
            putExtra(ChatActivity.EXTRA_CHAT_TYPE, ChatActivity.TYPE_CHANNEL)
            putExtra(ChatActivity.EXTRA_SERVER_ID, serverId)
            putExtra(ChatActivity.EXTRA_CHANNEL_ID, channelId)
        }
        activity.startActivity(intent)
    }
}
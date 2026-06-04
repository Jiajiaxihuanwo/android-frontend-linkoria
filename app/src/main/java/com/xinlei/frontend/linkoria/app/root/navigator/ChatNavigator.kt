package com.xinlei.frontend.linkoria.app.root.navigator

import android.app.Activity
import android.app.TaskStackBuilder
import android.content.Intent
import com.xinlei.frontend.linkoria.app.message.ui.ChatActivity
import com.xinlei.frontend.linkoria.app.message.ui.navigation.ChatArgs.Companion.EXTRA_CHANNEL_ID
import com.xinlei.frontend.linkoria.app.message.ui.navigation.ChatArgs.Companion.EXTRA_CHAT_TYPE
import com.xinlei.frontend.linkoria.app.message.ui.navigation.ChatArgs.Companion.EXTRA_CONVERSATION_ID
import com.xinlei.frontend.linkoria.app.message.ui.navigation.ChatArgs.Companion.EXTRA_SERVER_ID
import com.xinlei.frontend.linkoria.app.message.ui.navigation.ChatArgs.Companion.EXTRA_TARGET_ID
import com.xinlei.frontend.linkoria.app.message.ui.navigation.ChatArgs.Companion.TYPE_CHANNEL
import com.xinlei.frontend.linkoria.app.message.ui.navigation.ChatArgs.Companion.TYPE_DM
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
                putExtra(EXTRA_CHAT_TYPE, TYPE_DM)
                putExtra(EXTRA_CONVERSATION_ID, conversationId)
                putExtra(EXTRA_TARGET_ID, targetId)
            })
        }.startActivities()
    }

    fun openChannelChat(
        activity: Activity,
        serverId: Long,
        channelId: Long
    ) {
        val intent = Intent(activity, ChatActivity::class.java).apply {
            putExtra(EXTRA_CHAT_TYPE, TYPE_CHANNEL)
            putExtra(EXTRA_SERVER_ID, serverId)
            putExtra(EXTRA_CHANNEL_ID, channelId)
        }
        activity.startActivity(intent)
    }
}
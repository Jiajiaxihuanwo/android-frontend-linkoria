package com.xinlei.frontend.linkoria.app.message.ui.navigation

import android.content.Intent

data class ChatArgs(
    val chatType: String,
    val conversationId: Long = -1L,
    val targetId: String? = null,
    val serverId: Long = -1L,
    val channelId: Long = -1L
) {
    companion object {
        const val EXTRA_CHAT_TYPE = "extra_chat_type"
        const val EXTRA_CONVERSATION_ID = "extra_conversation_id"
        const val EXTRA_TARGET_ID = "extra_target_id"
        const val EXTRA_SERVER_ID = "extra_server_id"
        const val EXTRA_CHANNEL_ID = "extra_channel_id"

        const val TYPE_DM = "type_dm"
        const val TYPE_CHANNEL = "type_channel"

        fun from(intent: Intent): ChatArgs = ChatArgs(
            chatType = intent.getStringExtra(EXTRA_CHAT_TYPE) ?: "",
            conversationId = intent.getLongExtra(EXTRA_CONVERSATION_ID, -1L),
            targetId = intent.getStringExtra(EXTRA_TARGET_ID),
            serverId = intent.getLongExtra(EXTRA_SERVER_ID, -1L),
            channelId = intent.getLongExtra(EXTRA_CHANNEL_ID, -1L)
        )
    }
}
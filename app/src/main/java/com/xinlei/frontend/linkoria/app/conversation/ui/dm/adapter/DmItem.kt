package com.xinlei.frontend.linkoria.app.conversation.ui.dm.adapter

import com.xinlei.frontend.linkoria.app.conversation.domain.model.Conversation
import com.xinlei.frontend.linkoria.app.message.domain.model.Message

data class DmItem(
    val conversation: Conversation,
    val lastMessage: Message?,
    val lastMessageSenderName: String?,
    val currentUserId: String,
) {}
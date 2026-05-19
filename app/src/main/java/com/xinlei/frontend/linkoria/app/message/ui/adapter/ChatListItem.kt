package com.xinlei.frontend.linkoria.app.message.ui.adapter

import com.xinlei.frontend.linkoria.app.message.domain.model.Message
import com.xinlei.frontend.linkoria.app.user.domain.model.User
import java.time.Instant

sealed class ChatListItem {
    data class DateHeader(val date: Instant) : ChatListItem()
    data class MessageReceived(val message: Message, val sender: User) : ChatListItem()
    data class MessageSent(val message: Message, val sender: User) : ChatListItem()
}
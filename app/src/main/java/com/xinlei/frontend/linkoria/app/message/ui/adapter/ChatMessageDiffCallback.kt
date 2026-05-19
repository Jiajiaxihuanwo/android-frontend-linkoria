package com.xinlei.frontend.linkoria.app.message.ui.adapter

import androidx.recyclerview.widget.DiffUtil

class ChatMessageDiffCallback : DiffUtil.ItemCallback<ChatListItem>() {

    override fun areItemsTheSame(old: ChatListItem, new: ChatListItem): Boolean {
        return when {
            old is ChatListItem.DateHeader      && new is ChatListItem.DateHeader      -> old.date == new.date
            old is ChatListItem.MessageReceived && new is ChatListItem.MessageReceived -> old.message.id == new.message.id
            old is ChatListItem.MessageSent     && new is ChatListItem.MessageSent     -> old.message.id == new.message.id
            else -> false
        }
    }

    override fun areContentsTheSame(old: ChatListItem, new: ChatListItem) = old == new
}
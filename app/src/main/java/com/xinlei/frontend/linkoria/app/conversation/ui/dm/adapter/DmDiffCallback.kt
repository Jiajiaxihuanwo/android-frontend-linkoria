package com.xinlei.frontend.linkoria.app.conversation.ui.dm.adapter

import androidx.recyclerview.widget.DiffUtil
import com.xinlei.frontend.linkoria.app.conversation.domain.model.Conversation
import com.xinlei.frontend.linkoria.app.friendship.domain.model.Friendship

class DmDiffCallback : DiffUtil.ItemCallback<Conversation>() {

    override fun areItemsTheSame(oldItem: Conversation, newItem: Conversation): Boolean =
        oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: Conversation, newItem: Conversation): Boolean =
        oldItem == newItem

    override fun getChangePayload(oldItem: Conversation, newItem: Conversation): Any? {
        return if (oldItem != newItem) {
            true
        } else {
            super.getChangePayload(oldItem, newItem)
        }
    }
}
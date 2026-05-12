package com.xinlei.frontend.linkoria.app.conversation.ui.dm.adapter

import androidx.recyclerview.widget.DiffUtil
import com.xinlei.frontend.linkoria.app.conversation.domain.model.Conversation

class DmDiffCallback : DiffUtil.ItemCallback<Conversation>() {

    override fun areItemsTheSame(oldItem: Conversation, newItem: Conversation): Boolean =
        oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: Conversation, newItem: Conversation): Boolean =
        oldItem == newItem
}
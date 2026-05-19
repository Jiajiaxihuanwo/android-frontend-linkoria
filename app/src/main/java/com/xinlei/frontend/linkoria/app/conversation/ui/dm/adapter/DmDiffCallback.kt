package com.xinlei.frontend.linkoria.app.conversation.ui.dm.adapter

import androidx.recyclerview.widget.DiffUtil
import com.xinlei.frontend.linkoria.app.conversation.domain.model.Conversation

class DmDiffCallback : DiffUtil.ItemCallback<DmItem>() {

    override fun areItemsTheSame(oldItem: DmItem, newItem: DmItem): Boolean =
        oldItem.conversation.id == newItem.conversation.id

    override fun areContentsTheSame(oldItem: DmItem, newItem: DmItem): Boolean =
        oldItem == newItem

    override fun getChangePayload(oldItem: DmItem, newItem: DmItem): Any? {
        return if (oldItem != newItem) {
            true
        } else {
            super.getChangePayload(oldItem, newItem)
        }
    }
}
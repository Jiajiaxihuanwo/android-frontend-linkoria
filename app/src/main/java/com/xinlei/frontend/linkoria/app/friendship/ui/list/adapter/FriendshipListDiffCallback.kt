package com.xinlei.frontend.linkoria.app.friendship.ui.list.adapter

import androidx.recyclerview.widget.DiffUtil
import com.xinlei.frontend.linkoria.app.friendship.domain.model.Friendship

class FriendshipListDiffCallback : DiffUtil.ItemCallback<Friendship>() {

    override fun areItemsTheSame(oldItem: Friendship, newItem: Friendship): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Friendship, newItem: Friendship): Boolean {
        return oldItem == newItem
    }

    override fun getChangePayload(oldItem: Friendship, newItem: Friendship): Any? {
        return if (oldItem != newItem) {
            true
        } else {
            super.getChangePayload(oldItem, newItem)
        }
    }
}
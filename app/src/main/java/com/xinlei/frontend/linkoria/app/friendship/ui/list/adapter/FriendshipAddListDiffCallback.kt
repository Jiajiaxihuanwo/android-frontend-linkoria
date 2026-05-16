package com.xinlei.frontend.linkoria.app.friendship.ui.list.adapter

import com.xinlei.frontend.linkoria.app.friendship.ui.list.model.UserSearchItemUiModel
import com.xinlei.frontend.linkoria.app.user.domain.model.User

class FriendshipAddListDiffCallback : androidx.recyclerview.widget.DiffUtil.ItemCallback<UserSearchItemUiModel>() {

    override fun areItemsTheSame(oldItem: UserSearchItemUiModel, newItem: UserSearchItemUiModel): Boolean {
        return oldItem.user.id == newItem.user.id
    }

    override fun areContentsTheSame(oldItem: UserSearchItemUiModel, newItem: UserSearchItemUiModel): Boolean {
        return oldItem == newItem
    }

    override fun getChangePayload(oldItem: UserSearchItemUiModel, newItem: UserSearchItemUiModel): Any? {
        return if (oldItem != newItem) {
            true
        } else {
            super.getChangePayload(oldItem, newItem)
        }
    }
}
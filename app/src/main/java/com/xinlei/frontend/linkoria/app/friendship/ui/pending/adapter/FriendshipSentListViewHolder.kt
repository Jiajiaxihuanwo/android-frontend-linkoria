package com.xinlei.frontend.linkoria.app.friendship.ui.pending.adapter

import androidx.recyclerview.widget.RecyclerView
import com.xinlei.frontend.linkoria.app.core.ui.image.ImageLoader
import com.xinlei.frontend.linkoria.app.databinding.ItemFriendSentBinding
import com.xinlei.frontend.linkoria.app.friendship.domain.model.Friendship

class FriendshipSentListViewHolder(
    private val binding: ItemFriendSentBinding,
    private val imageLoader: ImageLoader
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(friendship: Friendship) {
        imageLoader.loadIcon(binding.ivAvatar, friendship.avatarUrl)

        binding.tvUsername.text = friendship.friendUsername
    }
}
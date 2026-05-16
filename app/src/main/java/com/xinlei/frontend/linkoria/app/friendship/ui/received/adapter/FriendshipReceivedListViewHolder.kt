package com.xinlei.frontend.linkoria.app.friendship.ui.received.adapter

import androidx.recyclerview.widget.RecyclerView
import com.xinlei.frontend.linkoria.app.core.ui.image.ImageLoader
import com.xinlei.frontend.linkoria.app.databinding.ItemFriendReceivedBinding
import com.xinlei.frontend.linkoria.app.friendship.domain.model.Friendship

class FriendshipReceivedListViewHolder(
    private val binding: ItemFriendReceivedBinding,
    private val imageLoader: ImageLoader,
    private val onAcceptClickListener: (Friendship) -> Unit,
    private val onDeclineClickListener: (Friendship) -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(friendship: Friendship) {
        imageLoader.loadIcon(binding.ivAvatar, friendship.avatarUrl)

        binding.tvUsername.text = friendship.friendUsername

        binding.btnAccept.setOnClickListener {
            onAcceptClickListener(friendship)
        }

        binding.btnDecline.setOnClickListener {
            onDeclineClickListener(friendship)
        }
    }
}
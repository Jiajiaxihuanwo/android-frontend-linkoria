package com.xinlei.frontend.linkoria.app.friendship.ui.list.adapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.xinlei.frontend.linkoria.app.core.ui.image.ImageLoader
import com.xinlei.frontend.linkoria.app.databinding.ItemFriendBinding
import com.xinlei.frontend.linkoria.app.friendship.domain.model.Friendship

class FriendshipListViewHolder (
    private val binding: ItemFriendBinding,
    private val imageLoader: ImageLoader,
    private val onChatClickListener: (Friendship) -> Unit,
    private val onMoreClickListener: (Friendship, View) -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(friendship: Friendship) {
        imageLoader.loadIcon(binding.ivAvatar,friendship.avatarUrl)

        // Establecer username
        binding.tvUsername.text = friendship.friendUsername

        // Listeners para los botones
        binding.btnChat.setOnClickListener {
            onChatClickListener(friendship)
        }

        binding.btnMore.setOnClickListener { view ->
            onMoreClickListener(friendship, view)
        }
    }
}
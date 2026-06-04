package com.xinlei.frontend.linkoria.app.friendship.ui.list.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.xinlei.frontend.linkoria.app.core.ui.image.ImageLoader
import com.xinlei.frontend.linkoria.app.databinding.ItemFriendBinding
import com.xinlei.frontend.linkoria.app.friendship.domain.model.Friendship

class FriendshipListAdapter(
    private val imageLoader: ImageLoader,
    private var onChatClickListener: (Friendship) -> Unit = {},
    private val onMoreClickListener: (Friendship, View) -> Unit
) : ListAdapter<Friendship, FriendshipListViewHolder>(FriendshipListDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendshipListViewHolder {
        val binding = ItemFriendBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FriendshipListViewHolder(binding, imageLoader, onChatClickListener, onMoreClickListener)
    }

    override fun onBindViewHolder(holder: FriendshipListViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}
package com.xinlei.frontend.linkoria.app.friendship.ui.pending.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.xinlei.frontend.linkoria.app.core.ui.image.ImageLoader
import com.xinlei.frontend.linkoria.app.databinding.ItemFriendSentBinding
import com.xinlei.frontend.linkoria.app.friendship.domain.model.Friendship
import com.xinlei.frontend.linkoria.app.friendship.ui.pending.adapter.FriendshipSentListDiffCallback
import com.xinlei.frontend.linkoria.app.friendship.ui.pending.adapter.FriendshipSentListViewHolder

class FriendshipSentListAdapter(
    private val imageLoader: ImageLoader
) : ListAdapter<Friendship, FriendshipSentListViewHolder>(FriendshipSentListDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendshipSentListViewHolder {
        val binding = ItemFriendSentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FriendshipSentListViewHolder(binding, imageLoader)
    }

    override fun onBindViewHolder(holder: FriendshipSentListViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}
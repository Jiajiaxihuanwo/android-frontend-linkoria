package com.xinlei.frontend.linkoria.app.friendship.ui.received.adapter

import com.xinlei.frontend.linkoria.app.friendship.ui.received.adapter.FriendshipReceivedListViewHolder
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.xinlei.frontend.linkoria.app.core.ui.image.ImageLoader
import com.xinlei.frontend.linkoria.app.databinding.ItemFriendReceivedBinding
import com.xinlei.frontend.linkoria.app.friendship.domain.model.Friendship
import com.xinlei.frontend.linkoria.app.friendship.ui.received.adapter.FriendshipReceivedListDiffCallback

class FriendshipReceivedListAdapter(
    private val imageLoader: ImageLoader,
    private var onAcceptClickListener: (Friendship) -> Unit = {},
    private var onDeclineClickListener: (Friendship) -> Unit = {}
) : ListAdapter<Friendship, FriendshipReceivedListViewHolder>(FriendshipReceivedListDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendshipReceivedListViewHolder {
        val binding = ItemFriendReceivedBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FriendshipReceivedListViewHolder(binding, imageLoader, onAcceptClickListener, onDeclineClickListener)
    }

    override fun onBindViewHolder(holder: FriendshipReceivedListViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}
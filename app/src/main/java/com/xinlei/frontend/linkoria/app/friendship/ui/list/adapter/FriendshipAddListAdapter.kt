package com.xinlei.frontend.linkoria.app.friendship.ui.list.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.xinlei.frontend.linkoria.app.core.ui.image.ImageLoader
import com.xinlei.frontend.linkoria.app.databinding.ItemFriendAddBinding
import com.xinlei.frontend.linkoria.app.friendship.domain.model.Friendship
import com.xinlei.frontend.linkoria.app.friendship.ui.list.model.UserSearchItemUiModel
import com.xinlei.frontend.linkoria.app.user.domain.model.User

class FriendshipAddListAdapter(
    private val imageLoader: ImageLoader,
    private var onAddClickListener: (UserSearchItemUiModel) -> Unit = {}
) : ListAdapter<UserSearchItemUiModel, FriendshipAddListViewHolder>(FriendshipAddListDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendshipAddListViewHolder {
        val binding = ItemFriendAddBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return FriendshipAddListViewHolder(
            binding,
            imageLoader
        ) { clickedItem ->

            val updatedList = currentList.map { item ->

                if (item.user.id == clickedItem.user.id) {
                    item.copy(isAvailable = false)
                } else {
                    item
                }
            }

            submitList(updatedList)

            onAddClickListener(clickedItem)
        }
    }

    override fun onBindViewHolder(holder: FriendshipAddListViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}
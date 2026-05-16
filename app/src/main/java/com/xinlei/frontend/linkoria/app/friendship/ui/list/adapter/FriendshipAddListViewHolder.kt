package com.xinlei.frontend.linkoria.app.friendship.ui.list.adapter

import com.xinlei.frontend.linkoria.app.core.ui.image.ImageLoader
import com.xinlei.frontend.linkoria.app.databinding.ItemFriendAddBinding
import com.xinlei.frontend.linkoria.app.friendship.ui.list.model.UserSearchItemUiModel
import com.xinlei.frontend.linkoria.app.user.domain.model.User

class FriendshipAddListViewHolder(
    private val binding: ItemFriendAddBinding,
    private val imageLoader: ImageLoader,
    private val onAddClickListener: (UserSearchItemUiModel) -> Unit
) : androidx.recyclerview.widget.RecyclerView.ViewHolder(binding.root) {

    fun bind(userSearchItemUiModel: UserSearchItemUiModel) {
        imageLoader.loadIcon(binding.ivAvatar, userSearchItemUiModel.user.avatarUrl)

        binding.tvUsername.text = userSearchItemUiModel.user.username

        if (userSearchItemUiModel.isAvailable) {

            binding.btnAdd.text = "Add"
            binding.btnAdd.isEnabled = true

            binding.btnAdd.setOnClickListener {
                onAddClickListener(userSearchItemUiModel)
            }

        } else {

            binding.btnAdd.text = "Added"
            binding.btnAdd.isEnabled = false

            binding.btnAdd.setOnClickListener(null)
        }
    }
}
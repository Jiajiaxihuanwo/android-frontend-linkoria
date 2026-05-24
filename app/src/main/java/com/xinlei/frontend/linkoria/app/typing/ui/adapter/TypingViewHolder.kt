package com.xinlei.frontend.linkoria.app.typing.ui.adapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.xinlei.frontend.linkoria.app.core.ui.image.ImageLoader
import com.xinlei.frontend.linkoria.app.databinding.ItemTypingBinding
import com.xinlei.frontend.linkoria.app.user.domain.model.User

class TypingViewHolder(
    private val binding: ItemTypingBinding,
    private val imageLoader: ImageLoader
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(user: User) {
        binding.tvUsername.text = user.username
        imageLoader.loadIcon(binding.ivAvatar, user.avatarUrl)
    }
}
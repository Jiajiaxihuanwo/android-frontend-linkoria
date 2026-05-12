package com.xinlei.frontend.linkoria.app.conversation.ui.dm.adapter

import androidx.recyclerview.widget.RecyclerView
import com.xinlei.frontend.linkoria.app.conversation.domain.model.Conversation
import com.xinlei.frontend.linkoria.app.core.ui.image.ImageLoader
import com.xinlei.frontend.linkoria.app.databinding.ItemDmBinding

class DmViewHolder (
    private val binding: ItemDmBinding,
    private val imageLoader: ImageLoader
) : RecyclerView.ViewHolder(binding.root) {
        fun bind(conversation: Conversation, onItemClick: (Conversation) -> Unit) {
            binding.root.setOnClickListener { onItemClick(conversation) }
            binding.tvUsername.text = conversation.targetUsername
            imageLoader.loadIconNoCache(binding.ivAvatar, conversation.targetIconUrl)
        }
}
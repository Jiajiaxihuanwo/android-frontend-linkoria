package com.xinlei.frontend.linkoria.app.conversation.ui.dm.adapter

import androidx.recyclerview.widget.RecyclerView
import com.xinlei.frontend.linkoria.app.conversation.domain.model.Conversation
import com.xinlei.frontend.linkoria.app.core.ui.image.ImageLoader
import com.xinlei.frontend.linkoria.app.core.util.formatPreview
import com.xinlei.frontend.linkoria.app.core.util.smartElapsed
import com.xinlei.frontend.linkoria.app.databinding.ItemDmBinding

class DmViewHolder (
    private val binding: ItemDmBinding,
    private val imageLoader: ImageLoader
) : RecyclerView.ViewHolder(binding.root) {
        fun bind(dmItem: DmItem, onItemClick: (Conversation) -> Unit) {
            binding.root.setOnClickListener { onItemClick(dmItem.conversation) }
            binding.tvUsername.text = dmItem.conversation.targetUsername
            imageLoader.loadIcon(binding.ivAvatar, dmItem.conversation.targetIconUrl)
            binding.tvTimestamp.text = dmItem.lastMessage?.createdAt?.smartElapsed() ?: ""
            binding.tvLastMessage.text = dmItem.lastMessage?.formatPreview(dmItem.currentUserId, dmItem.lastMessageSenderName) ?: ""
        }
}
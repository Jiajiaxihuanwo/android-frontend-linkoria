package com.xinlei.frontend.linkoria.app.message.ui.adapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.xinlei.frontend.linkoria.app.core.ui.image.ImageLoader
import com.xinlei.frontend.linkoria.app.core.util.smartDate
import com.xinlei.frontend.linkoria.app.core.util.smartTime
import com.xinlei.frontend.linkoria.app.databinding.ItemChatDateHeaderBinding
import com.xinlei.frontend.linkoria.app.databinding.ItemMessageReceivedBinding
import com.xinlei.frontend.linkoria.app.databinding.ItemMessageSentBinding
import com.xinlei.frontend.linkoria.app.message.domain.model.Message
import com.xinlei.frontend.linkoria.app.message.domain.model.MessageType

sealed class ChatMessageViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    class DateHeaderViewHolder(
        private val binding: ItemChatDateHeaderBinding
    ) : ChatMessageViewHolder(binding.root) {

        fun bind(item: ChatListItem.DateHeader) {
            binding.tvDate.text = item.date.smartDate()
        }
    }

    class MessageReceivedViewHolder(
        private val binding: ItemMessageReceivedBinding,
        private val imageLoader: ImageLoader,
        private var onImageClicked: (Message) -> Unit = {}
    ) : ChatMessageViewHolder(binding.root) {

        fun bind(item: ChatListItem.MessageReceived) {
            val message = item.message
            val sender  = item.sender

            binding.tvUsername.text    = sender.username
            binding.tvMessageText.text = message.content
            binding.tvTimestamp.text   = message.createdAt.smartTime()

            if (message.messageType == MessageType.IMAGE) {
                binding.ivAttachment.visibility = View.VISIBLE
                imageLoader.load(binding.ivAttachment, message.content)
                binding.ivAttachment.setOnClickListener { onImageClicked(message) }
                binding.tvMessageText.visibility = View.GONE
            } else {
                binding.ivAttachment.visibility  = View.GONE
                binding.tvMessageText.visibility = View.VISIBLE
            }

            imageLoader.loadIcon( binding.ivAvatar, sender.avatarUrl)
        }
    }

    class MessageSentViewHolder(
        private val binding: ItemMessageSentBinding,
        private val imageLoader: ImageLoader,
        private var onImageClicked: (Message) -> Unit = {}
    ) : ChatMessageViewHolder(binding.root) {

        fun bind(item: ChatListItem.MessageSent) {
            val message = item.message
            val sender  = item.sender

            binding.tvMessageText.text = message.content
            binding.tvTimestamp.text   = message.createdAt.smartTime()

            if (message.messageType == MessageType.IMAGE) {
                binding.ivAttachment.visibility  = View.VISIBLE
                imageLoader.load(binding.ivAttachment, message.content)
                binding.ivAttachment.setOnClickListener { onImageClicked(message) }
                binding.tvMessageText.visibility = View.GONE
            } else {
                binding.ivAttachment.visibility  = View.GONE
                binding.tvMessageText.visibility = View.VISIBLE
            }

            imageLoader.loadIcon(binding.ivAvatar, sender.avatarUrl)
        }
    }
}
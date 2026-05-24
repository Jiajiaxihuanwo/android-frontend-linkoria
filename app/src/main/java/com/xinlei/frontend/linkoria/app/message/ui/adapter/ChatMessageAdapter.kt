package com.xinlei.frontend.linkoria.app.message.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.xinlei.frontend.linkoria.app.core.ui.image.ImageLoader
import com.xinlei.frontend.linkoria.app.databinding.ItemChatDateHeaderBinding
import com.xinlei.frontend.linkoria.app.databinding.ItemMessageReceivedBinding
import com.xinlei.frontend.linkoria.app.databinding.ItemMessageSentBinding
import com.xinlei.frontend.linkoria.app.friendship.ui.list.model.UserSearchItemUiModel
import com.xinlei.frontend.linkoria.app.message.domain.model.Message

class ChatMessageAdapter(
    private val imageLoader: ImageLoader,
    private var onImageClicked: (Message) -> Unit = {}
) : ListAdapter<ChatListItem, ChatMessageViewHolder>(ChatMessageDiffCallback()) {

    companion object {
        private const val VIEW_TYPE_DATE_HEADER   = 0
        private const val VIEW_TYPE_MSG_RECEIVED  = 1
        private const val VIEW_TYPE_MSG_SENT      = 2
    }

    override fun getItemViewType(position: Int) = when (getItem(position)) {
        is ChatListItem.DateHeader      -> VIEW_TYPE_DATE_HEADER
        is ChatListItem.MessageReceived -> VIEW_TYPE_MSG_RECEIVED
        is ChatListItem.MessageSent     -> VIEW_TYPE_MSG_SENT
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatMessageViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            VIEW_TYPE_DATE_HEADER  -> ChatMessageViewHolder.DateHeaderViewHolder(
                ItemChatDateHeaderBinding.inflate(inflater, parent, false)
            )
            VIEW_TYPE_MSG_RECEIVED -> ChatMessageViewHolder.MessageReceivedViewHolder(
                ItemMessageReceivedBinding.inflate(inflater, parent, false),
                imageLoader,
                onImageClicked
            )
            else                   -> ChatMessageViewHolder.MessageSentViewHolder(
                ItemMessageSentBinding.inflate(inflater, parent, false),
                imageLoader,
                onImageClicked
            )
        }
    }

    override fun onBindViewHolder(holder: ChatMessageViewHolder, position: Int) {
        when (holder) {
            is ChatMessageViewHolder.DateHeaderViewHolder      -> holder.bind(getItem(position) as ChatListItem.DateHeader)
            is ChatMessageViewHolder.MessageReceivedViewHolder -> holder.bind(getItem(position) as ChatListItem.MessageReceived)
            is ChatMessageViewHolder.MessageSentViewHolder     -> holder.bind(getItem(position) as ChatListItem.MessageSent)
        }
    }
}
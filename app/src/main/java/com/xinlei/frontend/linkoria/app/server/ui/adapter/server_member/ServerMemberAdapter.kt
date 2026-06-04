package com.xinlei.frontend.linkoria.app.server.ui.adapter.server_member

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.xinlei.frontend.linkoria.app.core.ui.image.ImageLoader
import com.xinlei.frontend.linkoria.app.databinding.ItemServerMemberBinding
import com.xinlei.frontend.linkoria.app.databinding.ItemServerMemberHeaderBinding
import com.xinlei.frontend.linkoria.app.server.domain.model.ServerMember

class ServerMemberAdapter(
    private val imageLoader: ImageLoader,
    private val onMemberClick: ((ServerMember) -> Unit)? = null,
    private val onMoreClick: ((ServerMember) -> Unit)? = null
) : ListAdapter<ServerMemberListItem, ServerMemberViewHolder>(ServerMemberDiffCallback()) {

    companion object {
        private const val VIEW_TYPE_HEADER = 0
        private const val VIEW_TYPE_MEMBER = 1
    }

    override fun getItemViewType(position: Int) = when (getItem(position)) {
        is ServerMemberListItem.Header -> VIEW_TYPE_HEADER
        is ServerMemberListItem.Member -> VIEW_TYPE_MEMBER
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ServerMemberViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            VIEW_TYPE_HEADER -> ServerMemberViewHolder.HeaderViewHolder(
                ItemServerMemberHeaderBinding.inflate(inflater, parent, false)
            )
            else -> ServerMemberViewHolder.MemberViewHolder(
                ItemServerMemberBinding.inflate(inflater, parent, false),
                imageLoader
            )
        }
    }

    override fun onBindViewHolder(holder: ServerMemberViewHolder, position: Int) {
        when (holder) {
            is ServerMemberViewHolder.HeaderViewHolder -> holder.bind(getItem(position) as ServerMemberListItem.Header)
            is ServerMemberViewHolder.MemberViewHolder -> holder.bind(
                getItem(position) as ServerMemberListItem.Member,
                onMemberClick,
                onMoreClick
            )
        }
    }
}
package com.xinlei.frontend.linkoria.app.server.ui.adapter.server_member

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.xinlei.frontend.linkoria.app.core.ui.image.ImageLoader
import com.xinlei.frontend.linkoria.app.databinding.ItemServerMemberBinding
import com.xinlei.frontend.linkoria.app.databinding.ItemServerMemberHeaderBinding
import com.xinlei.frontend.linkoria.app.server.domain.model.ServerMember

sealed class ServerMemberViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    class HeaderViewHolder(private val binding: ItemServerMemberHeaderBinding) : ServerMemberViewHolder(binding.root) {
        fun bind(item: ServerMemberListItem.Header) {
            binding.tvRoleName.text = item.roleName
            binding.tvMemberCount.text = item.count.toString()
        }
    }

    class MemberViewHolder(
        private val binding: ItemServerMemberBinding,
        private val imageLoader: ImageLoader
    ) : ServerMemberViewHolder(binding.root) {
        fun bind(
            item: ServerMemberListItem.Member,
            onMemberClick: ((ServerMember) -> Unit)?,
            onMoreClick: ((ServerMember) -> Unit)?
        ) {
            val member = item.serverMember
            binding.tvUsername.text = member.username

            imageLoader.loadIcon(binding.ivAvatar, member.avatarUrl)

            binding.root.setOnClickListener { onMemberClick?.invoke(member) }

            if (onMoreClick != null) {
                binding.btnMore.visibility = View.VISIBLE
                binding.btnMore.setOnClickListener { onMoreClick(member) }
            } else {
                binding.btnMore.visibility = View.GONE
            }
        }
    }
}
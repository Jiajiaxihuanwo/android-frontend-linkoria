package com.xinlei.frontend.linkoria.app.server.ui.adapter.server_member

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.xinlei.frontend.linkoria.app.R
import com.xinlei.frontend.linkoria.app.databinding.ItemServerMemberBinding
import com.xinlei.frontend.linkoria.app.server.domain.model.ServerMember

class ServerMembersAdapter(
    private val onMemberClick: (ServerMember) -> Unit
) : ListAdapter<ServerMember, ServerMembersAdapter.ServerMemberViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ServerMemberViewHolder {
        val binding = ItemServerMemberBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ServerMemberViewHolder(binding, onMemberClick)
    }

    override fun onBindViewHolder(holder: ServerMemberViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ServerMemberViewHolder(
        private val binding: ItemServerMemberBinding,
        private val onMemberClick: (ServerMember) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(member: ServerMember) {
            binding.apply {
                // 设置用户名
                tvUsername.text = member.username

                // 加载头像
                Glide.with(root.context)
                    .load(member.avatarUrl)
                    .placeholder(R.drawable.ic_user)
                    .error(R.drawable.ic_user)
                    .circleCrop()
                    .into(ivAvatar)

                // 点击监听
                root.setOnClickListener {
                    onMemberClick(member)
                }
            }
        }
    }

    private class DiffCallback : DiffUtil.ItemCallback<ServerMember>() {
        override fun areItemsTheSame(oldItem: ServerMember, newItem: ServerMember): Boolean {
            return oldItem.userId == newItem.userId
        }

        override fun areContentsTheSame(oldItem: ServerMember, newItem: ServerMember): Boolean {
            return oldItem == newItem
        }
    }
}
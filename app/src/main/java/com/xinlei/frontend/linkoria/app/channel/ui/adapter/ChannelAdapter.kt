package com.xinlei.frontend.linkoria.app.channel.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.xinlei.frontend.linkoria.app.R
import com.xinlei.frontend.linkoria.app.channel.domain.model.Channel

class ChannelAdapter(
    private val onChannelClick: (Channel) -> Unit
) : ListAdapter<Channel, ChannelAdapter.ChannelViewHolder>(DIFF_CALLBACK) {

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Channel>() {
            override fun areItemsTheSame(old: Channel, new: Channel) = old.id == new.id
            override fun areContentsTheSame(old: Channel, new: Channel) = old == new
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChannelViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_channel, parent, false)
        return ChannelViewHolder(view, onChannelClick)
    }

    override fun onBindViewHolder(holder: ChannelViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ChannelViewHolder(
        itemView: View,
        private val onChannelClick: (Channel) -> Unit
    ) : RecyclerView.ViewHolder(itemView) {
        private val tvChannelName: TextView = itemView.findViewById(R.id.tv_channel_name)

        fun bind(channel: Channel) {
            tvChannelName.text = channel.name
            itemView.setOnClickListener { onChannelClick(channel) }
        }
    }
}
package com.xinlei.frontend.linkoria.app.channel.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.xinlei.frontend.linkoria.app.R
import com.xinlei.frontend.linkoria.app.channel.domain.model.Channel

class ChannelAdapter(
    private val onChannelClick: (Channel) -> Unit
) : RecyclerView.Adapter<ChannelAdapter.ChannelViewHolder>() {

    private var channels: List<Channel> = emptyList()

    fun submitList(list: List<Channel>) {
        channels = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChannelViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_channel, parent, false)
        return ChannelViewHolder(view, onChannelClick)
    }

    override fun onBindViewHolder(holder: ChannelViewHolder, position: Int) {
        holder.bind(channels[position])
    }

    override fun getItemCount(): Int = channels.size

    class ChannelViewHolder(
        itemView: View,
        private val onChannelClick: (Channel) -> Unit
    ) : RecyclerView.ViewHolder(itemView) {
        private val tvChannelName: TextView = itemView.findViewById(R.id.tv_channel_name)

        fun bind(channel: Channel) {
            tvChannelName.text = channel.name
            itemView.setOnClickListener {
                onChannelClick(channel)
            }
        }
    }
}
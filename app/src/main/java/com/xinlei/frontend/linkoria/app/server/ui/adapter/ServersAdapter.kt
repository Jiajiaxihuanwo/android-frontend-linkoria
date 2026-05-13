package com.xinlei.frontend.linkoria.app.server.ui.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.xinlei.frontend.linkoria.app.channel.ui.ChannelActivity
import com.xinlei.frontend.linkoria.app.databinding.ItemServerBinding
import com.xinlei.frontend.linkoria.app.server.domain.model.Server

class ServersAdapter(
    private val onServerClick: (Server) -> Unit
) : RecyclerView.Adapter<ServersAdapter.ServerViewHolder>() {

    private var servers: List<Server> = emptyList()

    fun submitList(list: List<Server>) {
        servers = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ServerViewHolder {
        val binding = ItemServerBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ServerViewHolder(binding, onServerClick)
    }

    override fun onBindViewHolder(holder: ServerViewHolder, position: Int) {
        holder.bind(servers[position])
    }

    override fun getItemCount(): Int = servers.size

    class ServerViewHolder(
        private val binding: ItemServerBinding,
        private val onServerClick: (Server) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(server: Server) {
            //Glide
            binding.root.setOnClickListener {
                val intent = Intent(binding.root.context, ChannelActivity::class.java)
                binding.root.context.startActivity(intent)
                onServerClick(server)
            }
        }
    }
}
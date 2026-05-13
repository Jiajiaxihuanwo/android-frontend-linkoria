package com.xinlei.frontend.linkoria.app.server.ui.adapter.server.list

import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.xinlei.frontend.linkoria.app.R
import com.xinlei.frontend.linkoria.app.core.ui.image.ImageLoader
import com.xinlei.frontend.linkoria.app.databinding.ItemServerBinding
import com.xinlei.frontend.linkoria.app.server.domain.model.Server

class ServerViewHolder(
    private val binding: ItemServerBinding,
    private val imageLoader: ImageLoader
) : RecyclerView.ViewHolder(binding.root) {

    private val ivIcon: ImageView = itemView.findViewById(R.id.iv_icon)

    fun bind(server: Server, onServerClick: (Server) -> Unit) {
        imageLoader.loadIconNoCache(binding.ivIcon, server.iconUrl)
        itemView.setOnClickListener {
            onServerClick(server)
        }
    }
}
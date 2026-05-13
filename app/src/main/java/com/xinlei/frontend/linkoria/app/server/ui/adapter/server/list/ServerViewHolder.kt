package com.xinlei.frontend.linkoria.app.server.ui.adapter.server.list

import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.xinlei.frontend.linkoria.app.core.ui.image.ImageLoader
import com.xinlei.frontend.linkoria.app.core.util.animateSidebarPress
import com.xinlei.frontend.linkoria.app.databinding.ItemServerBinding
import com.xinlei.frontend.linkoria.app.server.domain.model.Server

class ServerViewHolder(
    private val binding: ItemServerBinding,
    private val imageLoader: ImageLoader
) : RecyclerView.ViewHolder(binding.root) {

    private val ivIcon: ImageView = binding.ivIcon

    init {
        ivIcon.animateSidebarPress()
    }
    fun bind(server: Server, onServerClick: (Server) -> Unit) {
        imageLoader.load(ivIcon, server.iconUrl, server.name)
        ivIcon.setOnClickListener {
            onServerClick(server)
        }
    }
}
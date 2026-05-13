package com.xinlei.frontend.linkoria.app.server.ui.adapter.server.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.xinlei.frontend.linkoria.app.core.ui.image.ImageLoader
import com.xinlei.frontend.linkoria.app.databinding.ItemServerBinding
import com.xinlei.frontend.linkoria.app.server.domain.model.Server
import com.xinlei.frontend.linkoria.app.server.ui.adapter.server.list.ServerViewHolder

class ServersAdapter(
    private val imageLoader: ImageLoader,
    private val onServerClick: (Server) -> Unit
) : ListAdapter<Server, ServerViewHolder>(ServerDiffCallBack()) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ServerViewHolder {
        val binding = ItemServerBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return ServerViewHolder(binding, imageLoader)
    }

    override fun onBindViewHolder(
        holder: ServerViewHolder,
        position: Int
    ) {
        holder.bind(getItem(position), onServerClick)
    }
}
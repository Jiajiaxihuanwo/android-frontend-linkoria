package com.xinlei.frontend.linkoria.app.server.ui.adapter.server

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.xinlei.frontend.linkoria.app.R
import com.xinlei.frontend.linkoria.app.core.ui.image.ImageLoader
import com.xinlei.frontend.linkoria.app.databinding.ItemServerBinding
import com.xinlei.frontend.linkoria.app.server.domain.model.Server

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

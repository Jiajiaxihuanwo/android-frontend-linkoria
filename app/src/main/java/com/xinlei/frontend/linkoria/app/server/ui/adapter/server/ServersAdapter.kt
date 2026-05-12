package com.xinlei.frontend.linkoria.app.server.ui.adapter.server

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.xinlei.frontend.linkoria.app.R
import com.xinlei.frontend.linkoria.app.server.domain.model.Server

class ServersAdapter(
    private val onServerClick: (Server) -> Unit
) : ListAdapter<Server, ServerViewHolder>(ServerDiffCallBack()) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ServerViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_server, parent, false)

        return ServerViewHolder(view, onServerClick)
    }

    override fun onBindViewHolder(
        holder: ServerViewHolder,
        position: Int
    ) {
        holder.bind(getItem(position))
    }
}

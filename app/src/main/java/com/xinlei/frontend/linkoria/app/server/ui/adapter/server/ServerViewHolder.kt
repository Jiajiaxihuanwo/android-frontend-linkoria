package com.xinlei.frontend.linkoria.app.server.ui.adapter.server

import android.view.View
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.xinlei.frontend.linkoria.app.R
import com.xinlei.frontend.linkoria.app.server.domain.model.Server

class ServerViewHolder(itemView: View, private val onServerClick: (Server) -> Unit) : RecyclerView.ViewHolder(itemView) {

    private val ivIcon: ImageView = itemView.findViewById(R.id.iv_icon)

    fun bind(server: Server) {
        // TODO:
        // 这里之后可以加载 server.iconUrl
        // Glide / Coil / Picasso

        // ivIcon.setImageResource(R.drawable.ic_server_placeholder)

        itemView.setOnClickListener {
            onServerClick(server)
        }
    }
}

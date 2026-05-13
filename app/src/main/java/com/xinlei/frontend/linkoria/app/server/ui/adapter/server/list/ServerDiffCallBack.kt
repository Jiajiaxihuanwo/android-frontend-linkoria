package com.xinlei.frontend.linkoria.app.server.ui.adapter.server.list

import androidx.recyclerview.widget.DiffUtil
import com.xinlei.frontend.linkoria.app.server.domain.model.Server

class ServerDiffCallBack : DiffUtil.ItemCallback<Server>() {
    override fun areItemsTheSame(
        p0: Server,
        p1: Server
    ): Boolean {
        return p0 == p1
    }

    override fun areContentsTheSame(
        p0: Server,
        p1: Server
    ): Boolean {
        return p0 == p1
    }
}
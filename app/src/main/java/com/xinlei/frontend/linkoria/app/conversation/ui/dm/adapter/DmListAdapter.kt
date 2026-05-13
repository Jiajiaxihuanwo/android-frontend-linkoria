package com.xinlei.frontend.linkoria.app.conversation.ui.dm.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.xinlei.frontend.linkoria.app.conversation.domain.model.Conversation
import com.xinlei.frontend.linkoria.app.core.ui.image.ImageLoader
import com.xinlei.frontend.linkoria.app.databinding.ItemDmBinding

class   DmListAdapter(
    private val imageLoader: ImageLoader,
    private val onItemClick: (Conversation) -> Unit = {}
) : ListAdapter<Conversation, DmViewHolder>(DmDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DmViewHolder {
        val binding = ItemDmBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return DmViewHolder(binding, imageLoader)
    }

    override fun onBindViewHolder(holder: DmViewHolder, position: Int) {
        holder.bind(getItem(position), onItemClick)
    }
}
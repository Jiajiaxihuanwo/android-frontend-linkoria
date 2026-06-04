package com.xinlei.frontend.linkoria.app.typing.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.xinlei.frontend.linkoria.app.core.ui.image.ImageLoader
import com.xinlei.frontend.linkoria.app.databinding.ItemTypingBinding
import com.xinlei.frontend.linkoria.app.user.domain.model.User

class TypingAdapter(
    private val imageLoader: ImageLoader
) : ListAdapter<User, TypingViewHolder>(TypingDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TypingViewHolder {
        val binding = ItemTypingBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return TypingViewHolder(binding, imageLoader)
    }

    override fun onBindViewHolder(holder: TypingViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}
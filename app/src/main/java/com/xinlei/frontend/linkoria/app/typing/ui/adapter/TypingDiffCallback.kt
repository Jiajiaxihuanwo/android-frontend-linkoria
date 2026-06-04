package com.xinlei.frontend.linkoria.app.typing.ui.adapter

import androidx.recyclerview.widget.DiffUtil
import com.xinlei.frontend.linkoria.app.user.domain.model.User

class TypingDiffCallback : DiffUtil.ItemCallback<User>() {

    override fun areItemsTheSame(old: User, new: User): Boolean {
        return old.id == new.id
    }

    override fun areContentsTheSame(old: User, new: User): Boolean {
        return old == new
    }
}
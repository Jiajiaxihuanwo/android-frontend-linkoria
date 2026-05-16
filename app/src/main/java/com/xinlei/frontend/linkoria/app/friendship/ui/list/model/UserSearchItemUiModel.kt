package com.xinlei.frontend.linkoria.app.friendship.ui.list.model

import com.xinlei.frontend.linkoria.app.user.domain.model.User

data class UserSearchItemUiModel(
    val user: User,
    val isAvailable: Boolean
)
package com.xinlei.frontend.linkoria.app.friendship.data.mapper

import com.xinlei.frontend.linkoria.app.friendship.data.remote.dto.FriendshipResponse
import com.xinlei.frontend.linkoria.app.friendship.domain.model.Friendship

fun FriendshipResponse.toDomain(): Friendship {
    return Friendship(
        id = this.id,
        senderId = this.senderId,
        receiverId = this.receiverId,
        status = this.status,
        friendId = this.friendId,
        friendUsername = this.friendUsername,
        avatarUrl = this.avatarUrl,
        createdAt = this.createdAt,
        updatedAt = this.updatedAt
    )
}
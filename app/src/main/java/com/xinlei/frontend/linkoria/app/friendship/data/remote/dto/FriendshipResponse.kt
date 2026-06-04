package com.xinlei.frontend.linkoria.app.friendship.data.remote.dto

data class FriendshipResponse(
    val id: Long,
    val senderId: String,
    val receiverId: String,
    val status: String,
    val friendId: String,
    val friendUsername: String,
    val avatarUrl: String,
    val createdAt: String,
    val updatedAt: String
)
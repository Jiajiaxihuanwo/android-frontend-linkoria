package com.xinlei.frontend.linkoria.app.channel.domain

import com.xinlei.frontend.linkoria.app.channel.domain.model.Channel
import com.xinlei.frontend.linkoria.app.channel.domain.model.ChannelCategory
import com.xinlei.frontend.linkoria.app.core.network.NetworkResult
import kotlinx.coroutines.flow.Flow

interface ChannelRepository {

    // --- Endpoints de Channel ---
    fun createChannel(serverId: Long, name: String, categoryId: Long?): Flow<NetworkResult<Channel>>

    suspend fun deleteChannel(serverId: Long, channelId: Long): NetworkResult<Unit>

    fun getChannels(serverId: Long, categoryId: Long?): Flow<NetworkResult<List<Channel>>>

    fun getChannelById(serverId: Long, channelId: Long): Flow<NetworkResult<Channel>>

    // --- Endpoints de ChannelCategory ---
    fun createCategory(serverId: Long, name: String): Flow<NetworkResult<ChannelCategory>>

    suspend fun deleteCategory(serverId: Long, categoryId: Long): NetworkResult<Unit>

    fun getCategoryById(serverId: Long, categoryId: Long): Flow<NetworkResult<ChannelCategory>>

    fun getCategoriesByServer(serverId: Long): Flow<NetworkResult<List<ChannelCategory>>>
}
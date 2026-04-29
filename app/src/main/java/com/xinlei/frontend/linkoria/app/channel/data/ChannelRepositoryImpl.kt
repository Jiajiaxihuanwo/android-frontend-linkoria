package com.xinlei.frontend.linkoria.app.channel.data

import com.xinlei.frontend.linkoria.app.channel.data.mapper.toDomain
import com.xinlei.frontend.linkoria.app.channel.data.remote.ChannelApiService
import com.xinlei.frontend.linkoria.app.channel.data.remote.dto.CreateChannelCategoryRequest
import com.xinlei.frontend.linkoria.app.channel.data.remote.dto.CreateChannelRequest
import com.xinlei.frontend.linkoria.app.channel.domain.ChannelRepository
import com.xinlei.frontend.linkoria.app.channel.domain.model.Channel
import com.xinlei.frontend.linkoria.app.channel.domain.model.ChannelCategory
import com.xinlei.frontend.linkoria.app.core.network.BaseRepository
import com.xinlei.frontend.linkoria.app.core.network.NetworkResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class ChannelRepositoryImpl @Inject constructor(
    private val api: ChannelApiService
) : ChannelRepository, BaseRepository {

    // --- CHANNELS ---

    override fun createChannel(serverId: Long, name: String, categoryId: Long?): Flow<NetworkResult<Channel>> = flow {
        emit(NetworkResult.Loading)
        val request = CreateChannelRequest(name, categoryId)
        emit(safeApiCall { api.createChannel(serverId, request).toDomain() })
    }

    override fun getChannels(serverId: Long, categoryId: Long?): Flow<NetworkResult<List<Channel>>> = flow {
        emit(NetworkResult.Loading)
        emit(safeApiCall { api.getChannels(serverId, categoryId).map{it.toDomain()} })
    }

    override fun getChannelById(serverId: Long, channelId: Long): Flow<NetworkResult<Channel>> = flow {
        emit(NetworkResult.Loading)
        emit(safeApiCall { api.getChannelById(serverId, channelId).toDomain() })
    }

    override suspend fun deleteChannel(serverId: Long, channelId: Long): NetworkResult<Unit> {
        return safeApiCall { api.deleteChannel(serverId, channelId) }
    }

    // --- CATEGORIES ---

    override fun createCategory(serverId: Long, name: String): Flow<NetworkResult<ChannelCategory>> = flow {
        emit(NetworkResult.Loading)
        val request = CreateChannelCategoryRequest(name)
        emit(safeApiCall { api.createCategory(serverId, request).toDomain() })
    }

    override fun getCategoriesByServer(serverId: Long): Flow<NetworkResult<List<ChannelCategory>>> = flow {
        emit(NetworkResult.Loading)
        emit(safeApiCall { api.getCategories(serverId).map{it.toDomain()} })
    }

    override fun getCategoryById(serverId: Long, categoryId: Long): Flow<NetworkResult<ChannelCategory>> = flow {
        emit(NetworkResult.Loading)
        emit(safeApiCall { api.getCategoryById(serverId, categoryId).toDomain() })
    }

    override suspend fun deleteCategory(serverId: Long, categoryId: Long): NetworkResult<Unit> {
        return safeApiCall { api.deleteCategory(serverId, categoryId) }
    }
}
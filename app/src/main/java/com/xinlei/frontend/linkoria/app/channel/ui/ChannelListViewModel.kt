package com.xinlei.frontend.linkoria.app.channel.ui

import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xinlei.frontend.linkoria.app.channel.domain.model.Channel
import com.xinlei.frontend.linkoria.app.channel.domain.usecase.CreateChannelUseCase
import com.xinlei.frontend.linkoria.app.channel.domain.usecase.GetChannelsUseCase
import com.xinlei.frontend.linkoria.app.core.network.NetworkResult
import com.xinlei.frontend.linkoria.app.core.ui.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChannelListViewModel @Inject constructor(
    private val getChannelsUseCase: GetChannelsUseCase,
    private val createChannelUseCase: CreateChannelUseCase
) : ViewModel() {

    private val _channelsState = MutableStateFlow<UiState<List<Channel>>>(UiState.Idle)
    val channelsState = _channelsState.asStateFlow()

    private val _selectedChannelId = MutableStateFlow<Long?>(null)
    val selectedChannelId = _selectedChannelId.asStateFlow()

    private val _createChannelState = MutableStateFlow<UiState<Channel>>(UiState.Idle)
    val createChannelState = _createChannelState.asStateFlow()

    fun loadChannels(serverId: Long) {
        viewModelScope.launch {
            _channelsState.value = UiState.Loading
            getChannelsUseCase(serverId, null).collect { result -> // 发起请求，等结果
                when (result) {
                    is NetworkResult.Success -> {
                        _channelsState.value = UiState.Success(result.data)
                    }
                    is NetworkResult.Error -> {
                        _channelsState.value = UiState.Error(result.message ?: "Error loading channels")
                    }
                    else -> Unit
                }
            }
        }
    }


    fun createChannel(serverId: Long, name: String) {
        viewModelScope.launch {
            _createChannelState.value = UiState.Loading
            createChannelUseCase(serverId, name, null).collect { result ->
                android.util.Log.d("DEBUG", "createChannel result: $result")
                when (result) {
                    is NetworkResult.Success -> {
                        android.util.Log.d("DEBUG", "Channel created successfully: ${result.data}")
                        _createChannelState.value = UiState.Success(result.data)
                        loadChannels(serverId)
                    }
                    is NetworkResult.Error -> {
                        android.util.Log.d("DEBUG", "Error: ${result.message}")
                        _createChannelState.value = UiState.Error(result.message ?: "Error al crear canal")
                    }
                    else -> Unit
                }
            }
        }
    }

}
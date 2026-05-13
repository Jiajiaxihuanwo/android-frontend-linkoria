package com.xinlei.frontend.linkoria.app.channel.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xinlei.frontend.linkoria.app.channel.domain.model.Channel
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
    private val getChannelsUseCase: GetChannelsUseCase
) : ViewModel() {

    private val _channelsState = MutableStateFlow<UiState<List<Channel>>>(UiState.Idle)
    val channelsState = _channelsState.asStateFlow()

    private val _selectedChannelId = MutableStateFlow<Long?>(null)
    val selectedChannelId = _selectedChannelId.asStateFlow()

    fun loadChannels(serverId: Long) {
        viewModelScope.launch {
            _channelsState.value = UiState.Loading
            getChannelsUseCase(serverId, null).collect { result ->
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

    fun selectChannel(channelId: Long) {
        _selectedChannelId.value = channelId
    }
}
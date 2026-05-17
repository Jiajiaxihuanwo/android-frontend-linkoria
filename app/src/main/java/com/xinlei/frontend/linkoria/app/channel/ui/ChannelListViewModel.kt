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
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChannelListViewModel @Inject constructor(
    private val getChannelsUseCase: GetChannelsUseCase,
    private val createChannelUseCase: CreateChannelUseCase
) : ViewModel() {

    private val _channelsState = MutableStateFlow<UiState<List<Channel>>>(UiState.Idle)
    val channelsState = _channelsState.asStateFlow()

    private val _allChannels = MutableStateFlow<List<Channel>>(emptyList())
    private val _filterQuery = MutableStateFlow("")

    private val _createChannelState = MutableStateFlow<UiState<Channel>>(UiState.Idle)
    val createChannelState = _createChannelState.asStateFlow()

    private var currentServerId: Long = -1

    private val _selectedChannel = MutableStateFlow<Channel?>(null)
    val selectedChannel = _selectedChannel.asStateFlow()

    fun onChannelClick(channel: Channel) {
        _selectedChannel.value = channel
    }

    fun clearSelectedChannel() {
        _selectedChannel.value = null
    }

    init {
        observeFilterQuery()
    }

    @OptIn(FlowPreview::class)
    private fun observeFilterQuery() {
        _filterQuery
            .debounce(150)
            .distinctUntilChanged()
            .onEach { query -> applyFilter(query) }
            .launchIn(viewModelScope)
    }

    fun onFilterQueryChanged(query: String) {
        _filterQuery.value = query
    }

    private fun applyFilter(query: String) {
        val filtered = if (query.isBlank()) {
            _allChannels.value
        } else {
            _allChannels.value.filter {
                it.name.contains(query, ignoreCase = true)
            }
        }
        _channelsState.value = UiState.Success(filtered)
    }

    fun loadChannels(serverId: Long) {
        currentServerId = serverId
        viewModelScope.launch {
            _channelsState.value = UiState.Loading
            getChannelsUseCase(serverId, null).collect { result ->
                when (result) {
                    is NetworkResult.Success -> {
                        _allChannels.value = result.data
                        applyFilter(_filterQuery.value)
                    }
                    is NetworkResult.Error -> {
                        _channelsState.value = UiState.Error(result.message ?: "Error loading channels")
                    }
                    else -> Unit
                }
            }
        }
    }

    fun createChannel(name: String) {
        viewModelScope.launch {
            _createChannelState.value = UiState.Loading
            createChannelUseCase(currentServerId, name, null).collect { result ->
                when (result) {
                    is NetworkResult.Success -> {
                        _createChannelState.value = UiState.Success(result.data)
                        loadChannels(currentServerId)
                    }
                    is NetworkResult.Error -> {
                        _createChannelState.value = UiState.Error(result.message ?: "Error al crear canal")
                    }
                    else -> Unit
                }
            }
        }
    }
}
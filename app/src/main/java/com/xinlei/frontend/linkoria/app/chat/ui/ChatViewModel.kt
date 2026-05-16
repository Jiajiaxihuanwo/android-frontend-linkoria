package com.xinlei.frontend.linkoria.app.chat.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xinlei.frontend.linkoria.app.channel.domain.model.Channel
import com.xinlei.frontend.linkoria.app.channel.domain.usecase.GetChannelByIdUseCase
import com.xinlei.frontend.linkoria.app.conversation.domain.usecase.GetChannelConversationUseCase
import com.xinlei.frontend.linkoria.app.core.network.NetworkResult
import com.xinlei.frontend.linkoria.app.core.ui.UiState
import com.xinlei.frontend.linkoria.app.user.domain.model.User
import com.xinlei.frontend.linkoria.app.user.domain.usecase.GetUserByIdUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val getUserByIdUseCase: GetUserByIdUseCase,
    private val getChannelByIdUseCase: GetChannelByIdUseCase,
    private val getChannelConversationUseCase: GetChannelConversationUseCase
) : ViewModel() {

    private val _dmState = MutableStateFlow<UiState<User?>>(UiState.Idle)
    val dmState: StateFlow<UiState<User?>> = _dmState.asStateFlow()

    private val _channelState = MutableStateFlow<UiState<Channel>>(UiState.Idle)
    val channelState: StateFlow<UiState<Channel>> = _channelState.asStateFlow()

    private val _conversationId = MutableStateFlow<Long?>(null)
    val conversationId: StateFlow<Long?> = _conversationId.asStateFlow()

    fun initDmChat(conversationId: Long, targetId: String) {
        _conversationId.value = conversationId
        loadUserProfile(targetId)
    }

    fun initChannelChat(serverId: Long, channelId: Long) {
        viewModelScope.launch {
            resolveChannelConversation(channelId)
            loadChannel(serverId, channelId)
        }
    }

    private fun loadUserProfile(targetId: String) {
        viewModelScope.launch {
            _dmState.value = UiState.Loading
            getUserByIdUseCase(targetId).collect { result ->
                when (result) {
                    is NetworkResult.Success -> _dmState.value = UiState.Success(result.data)
                    is NetworkResult.Error -> _dmState.value = UiState.Error(result.message ?: "Error desconocido")
                    else -> Unit
                }
            }
        }
    }

    private suspend fun resolveChannelConversation(channelId: Long) {
        getChannelConversationUseCase(channelId).collect { result ->
            when (result) {
                is NetworkResult.Success -> _conversationId.value = result.data.id
                is NetworkResult.Error -> _channelState.value = UiState.Error(result.message ?: "Error al obtener conversación")
                else -> Unit
            }
        }
    }

    private fun loadChannel(serverId: Long, channelId: Long) {
        viewModelScope.launch {
            _channelState.value = UiState.Loading
            getChannelByIdUseCase(serverId, channelId).collect { result ->
                when (result) {
                    is NetworkResult.Success -> _channelState.value = UiState.Success(result.data)
                    is NetworkResult.Error -> _channelState.value = UiState.Error(result.message ?: "Error al cargar canal")
                    else -> Unit
                }
            }
        }
    }
}
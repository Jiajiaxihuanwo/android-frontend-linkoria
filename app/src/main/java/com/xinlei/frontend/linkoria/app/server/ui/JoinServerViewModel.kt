package com.xinlei.frontend.linkoria.app.server.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xinlei.frontend.linkoria.app.core.network.NetworkResult
import com.xinlei.frontend.linkoria.app.core.ui.UiState
import com.xinlei.frontend.linkoria.app.server.domain.model.Server
import com.xinlei.frontend.linkoria.app.server.domain.usecase.FindServerUseCase
import com.xinlei.frontend.linkoria.app.server.domain.usecase.JoinServerUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class JoinServerViewModel @Inject constructor(
    private val findServerUseCase: FindServerUseCase,
    private val joinServerUseCase: JoinServerUseCase
) : ViewModel() {

    private val _findState = MutableStateFlow<UiState<Server>>(UiState.Idle)
    val findState = _findState.asStateFlow()

    private val _joinState = MutableStateFlow<UiState<Unit>>(UiState.Idle)
    val joinState = _joinState.asStateFlow()

    fun findServer(inviteCode: String) {
        viewModelScope.launch {
            _findState.value = UiState.Loading
            findServerUseCase(inviteCode).collect { result ->
                when (result) {
                    is NetworkResult.Success -> {
                        _findState.value = UiState.Success(result.data)
                    }
                    is NetworkResult.Error -> {
                        _findState.value = UiState.Error(result.message ?: "Error desconocido")
                    }
                    else -> Unit
                }
            }
        }
    }

    fun joinServer() {
        val server = (_findState.value as? UiState.Success)?.data ?: return
        viewModelScope.launch {
            _joinState.value = UiState.Loading
            joinServerUseCase(server.inviteCode!!).collect { result ->
                when (result) {
                    is NetworkResult.Success -> {
                        _joinState.value = UiState.Success(Unit)
                    }
                    is NetworkResult.Error -> {
                        _joinState.value = UiState.Error(result.message ?: "Error desconocido")
                    }
                    else -> Unit
                }
            }
        }
    }

    fun resetFindState() {
        _findState.value = UiState.Idle
    }

    fun resetJoinState() {
        _joinState.value = UiState.Idle
    }
}
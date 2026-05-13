package com.xinlei.frontend.linkoria.app.root

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xinlei.frontend.linkoria.app.core.network.NetworkResult
import com.xinlei.frontend.linkoria.app.core.ui.UiState
import com.xinlei.frontend.linkoria.app.server.domain.model.Server
import com.xinlei.frontend.linkoria.app.server.domain.usecase.CreateServerUseCase
import com.xinlei.frontend.linkoria.app.server.domain.usecase.GetJoinedServersUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val createServerUseCase: CreateServerUseCase,
    private val joinedServersUseCase: GetJoinedServersUseCase
) : ViewModel(){
    private val _serverListState = MutableStateFlow<UiState<List<Server>>>(UiState.Idle)
    val userListState = _serverListState.asStateFlow()
    private val _createServerState = MutableStateFlow<UiState<Server>>(UiState.Idle)
    val createServerState = _createServerState.asStateFlow()

    fun observerServerList() {
        viewModelScope.launch {
            _serverListState.value = UiState.Loading
            joinedServersUseCase().collect { result ->
                when (result) {
                    is NetworkResult.Success -> {
                        _serverListState.value = UiState.Success(result.data)
                    }

                    is NetworkResult.Error -> {
                        _serverListState.value = UiState.Error(result.message ?: "Error en observar la lista")
                    }

                    else -> Unit
                }
            }
        }
    }
}
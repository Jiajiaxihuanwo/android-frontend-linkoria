package com.xinlei.frontend.linkoria.app.server.ui.members

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xinlei.frontend.linkoria.app.core.network.NetworkResult
import com.xinlei.frontend.linkoria.app.core.ui.UiState
import com.xinlei.frontend.linkoria.app.server.domain.model.ServerMember
import com.xinlei.frontend.linkoria.app.server.domain.usecase.GetServerMembersUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ServerMembersViewModel @Inject constructor(
    private val getServerMembersUseCase: GetServerMembersUseCase
) : ViewModel() {

    private val _membersState = MutableStateFlow<UiState<List<ServerMember>>>(UiState.Idle)
    val membersState: StateFlow<UiState<List<ServerMember>>> = _membersState.asStateFlow()

    private var allMembers: List<ServerMember> = emptyList()

    fun loadServerMembers(serverId: Long) {
        viewModelScope.launch {
            _membersState.value = UiState.Loading
            getServerMembersUseCase(serverId).collect { result ->
                when (result) {
                    is NetworkResult.Success -> {
                        allMembers = result.data
                        _membersState.value = UiState.Success(result.data)
                    }

                    is NetworkResult.Error -> {
                        _membersState.value = UiState.Error(result.message ?: "Error al cargar miembros")
                    }

                    is NetworkResult.Loading -> {
                        _membersState.value = UiState.Loading
                    }
                }
            }
        }
    }

    fun searchMembers(query: String) {
        if (query.isBlank()) {
            _membersState.value = UiState.Success(allMembers)
            return
        }

        val filtered = allMembers.filter { member ->
            member.username.contains(query, ignoreCase = true)
        }

        _membersState.value = UiState.Success(filtered)
    }
}
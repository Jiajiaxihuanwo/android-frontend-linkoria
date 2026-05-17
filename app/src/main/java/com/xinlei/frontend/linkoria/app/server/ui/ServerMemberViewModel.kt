package com.xinlei.frontend.linkoria.app.server.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xinlei.frontend.linkoria.app.core.network.NetworkResult
import com.xinlei.frontend.linkoria.app.core.ui.UiState
import com.xinlei.frontend.linkoria.app.friendship.domain.usecase.SendFriendshipRequestUseCase
import com.xinlei.frontend.linkoria.app.server.domain.model.Server
import com.xinlei.frontend.linkoria.app.server.domain.model.ServerMember
import com.xinlei.frontend.linkoria.app.server.domain.model.ServerRole
import com.xinlei.frontend.linkoria.app.server.domain.usecase.DeleteServerUseCase
import com.xinlei.frontend.linkoria.app.server.domain.usecase.GetServerMembersUseCase
import com.xinlei.frontend.linkoria.app.server.domain.usecase.GetServerUseCase
import com.xinlei.frontend.linkoria.app.server.domain.usecase.LeaveServerUseCase
import com.xinlei.frontend.linkoria.app.server.ui.adapter.server_member.ServerMemberListItem
import com.xinlei.frontend.linkoria.app.user.domain.model.User
import com.xinlei.frontend.linkoria.app.user.domain.usecase.GetUserByIdUseCase
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
class ServerMemberViewModel @Inject constructor(
    private val getServerUseCase: GetServerUseCase,
    private val getServerMembersUseCase: GetServerMembersUseCase,
    private val sendFriendshipRequestUseCase: SendFriendshipRequestUseCase,
    private val getUserByIdUseCase: GetUserByIdUseCase,
    private val deleteServerUseCase: DeleteServerUseCase,
    private val leaveServerUseCase: LeaveServerUseCase,
) : ViewModel() {

    private val _serverState = MutableStateFlow<UiState<Server>>(UiState.Idle)
    val serverState = _serverState.asStateFlow()

    private val _membersState = MutableStateFlow<UiState<List<ServerMemberListItem>>>(UiState.Idle)
    val membersState = _membersState.asStateFlow()

    private val _selectedMember = MutableStateFlow<ServerMember?>(null)
    val selectedMember = _selectedMember.asStateFlow()

    private val _friendRequestState = MutableStateFlow<UiState<Unit>>(UiState.Idle)
    val friendRequestState = _friendRequestState.asStateFlow()

    private val _allMembers = MutableStateFlow<List<ServerMember>>(emptyList())
    private val _filterQuery = MutableStateFlow("")

    private val _selectedMemberUser = MutableStateFlow<UiState<User>>(UiState.Idle)
    val selectedMemberUser = _selectedMemberUser.asStateFlow()

    private val _deleteServerState = MutableStateFlow<UiState<Unit>>(UiState.Idle)
    val deleteServerState = _deleteServerState.asStateFlow()

    private val _leaveServerState = MutableStateFlow<UiState<Unit>>(UiState.Idle)
    val leaveServerState = _leaveServerState.asStateFlow()

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
            _allMembers.value
        } else {
            _allMembers.value.filter {
                it.username.contains(query, ignoreCase = true)
            }
        }
        _membersState.value = UiState.Success(buildMemberList(filtered))
    }

    fun load(serverId: Long) {
        viewModelScope.launch {
            launch { loadServer(serverId) }
            launch { loadMembers(serverId) }
        }
    }

    fun onMemberClick(member: ServerMember) {
        _selectedMember.value = member
        loadMemberUser(member.userId)
    }
    fun sendFriendRequest(targetId: String) {
        viewModelScope.launch {
            _friendRequestState.value = UiState.Loading
            when (val result = sendFriendshipRequestUseCase(targetId)) {
                is NetworkResult.Success -> _friendRequestState.value = UiState.Success(Unit)
                is NetworkResult.Error -> _friendRequestState.value = UiState.Error(result.message ?: "Error al enviar solicitud")
                else -> Unit
            }
        }
    }

    fun clearFriendRequestState() {
        _friendRequestState.value = UiState.Idle
    }

    private fun loadMemberUser(userId: String) {
        viewModelScope.launch {
            _selectedMemberUser.value = UiState.Loading
            getUserByIdUseCase(userId).collect { result ->
                when (result) {
                    is NetworkResult.Success -> _selectedMemberUser.value = UiState.Success(result.data)
                    is NetworkResult.Error -> _selectedMemberUser.value = UiState.Error(result.message ?: "Error al cargar usuario")
                    else -> Unit
                }
            }
        }
    }

    fun clearSelectedMember() {
        _selectedMember.value = null
        _selectedMemberUser.value = UiState.Idle
    }

    private suspend fun loadServer(serverId: Long) {
        _serverState.value = UiState.Loading
        getServerUseCase(serverId).collect { result ->
            when (result) {
                is NetworkResult.Success -> _serverState.value = UiState.Success(result.data)
                is NetworkResult.Error -> _serverState.value = UiState.Error(result.message ?: "Error al cargar servidor")
                else -> Unit
            }
        }
    }

    private suspend fun loadMembers(serverId: Long) {
        _membersState.value = UiState.Loading
        getServerMembersUseCase(serverId).collect { result ->
            when (result) {
                is NetworkResult.Success -> {
                    _allMembers.value = result.data
                    applyFilter(_filterQuery.value)
                }
                is NetworkResult.Error -> _membersState.value = UiState.Error(result.message ?: "Error al cargar miembros")
                else -> Unit
            }
        }
    }

    private fun buildMemberList(members: List<ServerMember>): List<ServerMemberListItem> {
        val result = mutableListOf<ServerMemberListItem>()
        val grouped = members.groupBy { it.role }

        listOf(ServerRole.OWNER, ServerRole.ADMIN, ServerRole.MEMBER).forEach { role ->
            val group = grouped[role] ?: return@forEach
            val roleName = when (role) {
                ServerRole.OWNER -> "Propietario"
                ServerRole.ADMIN -> "Administradores"
                ServerRole.MEMBER -> "Miembros"
            }
            result.add(ServerMemberListItem.Header(roleName, group.size))
            result.addAll(group.map { ServerMemberListItem.Member(it) })
        }

        return result
    }

    fun deleteServer(serverId: Long) {
        viewModelScope.launch {
            _deleteServerState.value = UiState.Loading
            when (val result = deleteServerUseCase(serverId)) {
                is NetworkResult.Success -> _deleteServerState.value = UiState.Success(Unit)
                is NetworkResult.Error -> _deleteServerState.value = UiState.Error(result.message ?: "Error al eliminar servidor")
                else -> Unit
            }
        }
    }

    fun leaveServer(serverId: Long) {
        viewModelScope.launch {
            _leaveServerState.value = UiState.Loading
            when (val result = leaveServerUseCase(serverId)) {
                is NetworkResult.Success -> _leaveServerState.value = UiState.Success(Unit)
                is NetworkResult.Error -> _leaveServerState.value = UiState.Error(result.message ?: "Error al abandonar servidor")
                else -> Unit
            }
        }
    }
}
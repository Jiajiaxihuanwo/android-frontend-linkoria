package com.xinlei.frontend.linkoria.app.friendship.ui.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xinlei.frontend.linkoria.app.conversation.domain.model.Conversation
import com.xinlei.frontend.linkoria.app.conversation.domain.usecase.CreateDmUseCase
import com.xinlei.frontend.linkoria.app.core.network.NetworkResult
import com.xinlei.frontend.linkoria.app.core.ui.UiState
import com.xinlei.frontend.linkoria.app.friendship.domain.model.Friendship
import com.xinlei.frontend.linkoria.app.friendship.domain.usecase.GetFriendsUseCase
import com.xinlei.frontend.linkoria.app.friendship.domain.usecase.GetFriendshipsUseCase
import com.xinlei.frontend.linkoria.app.friendship.domain.usecase.RemoveFriendUseCase
import com.xinlei.frontend.linkoria.app.friendship.domain.usecase.SendFriendshipRequestUseCase
import com.xinlei.frontend.linkoria.app.user.domain.model.User
import com.xinlei.frontend.linkoria.app.user.domain.usecase.SearchUsersUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(FlowPreview::class)
@HiltViewModel
class FriendListViewModel @Inject constructor(
    private val getFriendsUseCase: GetFriendsUseCase,
    private val getFriendshipsUseCase: GetFriendshipsUseCase,
    private val searchUsersUseCase: SearchUsersUseCase,
    private val sendFriendshipRequestUseCase: SendFriendshipRequestUseCase,
    private val createDmUseCase: CreateDmUseCase,
    private val removeFriendUseCase: RemoveFriendUseCase,
) : ViewModel() {

    // Lista completa de amigos cargada desde el backend (fuente de verdad local)
    private val _allFriends = MutableStateFlow<List<Friendship>>(emptyList())
    val allFriends = _allFriends.asStateFlow()

    private val _allFriendships = MutableStateFlow<List<Friendship>>(emptyList())
    val allFriendships = _allFriendships.asStateFlow()

    // Lista filtrada que consume el adapter rv_friends
    private val _friendsState = MutableStateFlow<UiState<List<Friendship>>>(UiState.Idle)
    val friendsState: StateFlow<UiState<List<Friendship>>> = _friendsState.asStateFlow()

    // Query del et_filter para filtrar amigos localmente
    private val _filterQuery = MutableStateFlow("")

    // Resultados de búsqueda de usuarios nuevos para rv_search_results
    private val _searchState = MutableStateFlow<UiState<List<User>>>(UiState.Idle)
    val searchState: StateFlow<UiState<List<User>>> = _searchState.asStateFlow()

    // Estado del envío de solicitud de amistad
    private val _sendRequestState = MutableStateFlow<UiState<Unit>>(UiState.Idle)
    val sendRequestState: StateFlow<UiState<Unit>> = _sendRequestState.asStateFlow()

    private val _deleteFriendState = MutableStateFlow<UiState<String>>(UiState.Idle)
    val deleteFriendState = _deleteFriendState.asStateFlow()

    // Estado para entrar al DM
    private val _createDmState = MutableStateFlow<UiState<Conversation>>(UiState.Idle)
    val createDmState = _createDmState.asStateFlow()

    var pendingTargetId: String? = null
        private set


    init {
        observeFilterQuery()
    }

    fun loadFriends() {
        viewModelScope.launch {
            _friendsState.value = UiState.Loading
            getFriendsUseCase().collect { result ->
                when (result) {
                    is NetworkResult.Success -> {
                        _allFriends.value = result.data
                        applyFilter(_filterQuery.value)
                    }
                    is NetworkResult.Error -> {
                        _friendsState.value = UiState.Error(result.message ?: "Error desconocido")
                    }
                    else -> Unit
                }
            }
        }

        viewModelScope.launch {
            getFriendshipsUseCase().collect { result ->
                when (result) {
                    is NetworkResult.Success -> {
                        _allFriendships.value = result.data
                    }
                    else -> Unit
                }
            }
        }
    }

    fun onFilterQueryChanged(query: String) {
        _filterQuery.value = query
    }

    private fun observeFilterQuery() {
        _filterQuery
            .debounce(150)
            .distinctUntilChanged()
            .onEach { query -> applyFilter(query) }
            .launchIn(viewModelScope)
    }

    private fun applyFilter(query: String) {
        val filtered = if (query.isBlank()) {
            _allFriends.value
        } else {
            _allFriends.value.filter {
                it.friendUsername.contains(query, ignoreCase = true)
            }
        }
        _friendsState.value = UiState.Success(filtered)
    }

    fun searchUsers(query: String) {
        if (query.isBlank()) {
            _searchState.value = UiState.Idle
            return
        }
        viewModelScope.launch {
            _searchState.value = UiState.Loading
            searchUsersUseCase(query).collect { result ->
                when (result) {
                    is NetworkResult.Success -> {
                        _searchState.value = UiState.Success(result.data)
                    }
                    is NetworkResult.Error -> {
                        _searchState.value = UiState.Error(result.message ?: "Error desconocido")
                    }
                    else -> Unit
                }
            }
        }
    }

    fun clearSearchResults() {
        _searchState.value = UiState.Idle
    }

    fun sendFriendshipRequest(targetId: String) {
        viewModelScope.launch {
            _sendRequestState.value = UiState.Loading
            when (val result = sendFriendshipRequestUseCase(targetId)) {
                is NetworkResult.Success -> {
                    _sendRequestState.value = UiState.Success(Unit)
                }
                is NetworkResult.Error -> {
                    _sendRequestState.value = UiState.Error(result.message ?: "Error desconocido")
                }
                else -> Unit
            }
        }
    }

    fun deleteFriend(targetId: String) {
        viewModelScope.launch {
            _sendRequestState.value = UiState.Loading
            when (val result = removeFriendUseCase(targetId)) {
                is NetworkResult.Success -> {
                    _deleteFriendState.value = UiState.Success(targetId)
                }
                is NetworkResult.Error -> {
                    _sendRequestState.value = UiState.Error(result.message ?: "Error desconocido")
                }
                else -> Unit
            }
        }
    }

    fun createDm(targetId: String) {
        pendingTargetId = targetId
        viewModelScope.launch {
            _createDmState.value = UiState.Loading
            createDmUseCase(targetId).collect { result ->
                when (result) {
                    is NetworkResult.Success -> {
                        _createDmState.value = UiState.Success(result.data)
                    }
                    is NetworkResult.Error -> {
                        _createDmState.value = UiState.Error(result.message ?: "Error desconocido")
                    }
                    else -> Unit
                }
            }
        }
    }

    fun resetSendRequestState() {
        _sendRequestState.value = UiState.Idle
    }

    fun resetDeleteFriendState() {
        _deleteFriendState.value = UiState.Idle
    }
}
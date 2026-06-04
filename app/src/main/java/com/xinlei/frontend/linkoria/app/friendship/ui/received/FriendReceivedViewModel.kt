package com.xinlei.frontend.linkoria.app.friendship.ui.received

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xinlei.frontend.linkoria.app.core.network.NetworkResult
import com.xinlei.frontend.linkoria.app.core.ui.UiState
import com.xinlei.frontend.linkoria.app.friendship.domain.model.Friendship
import com.xinlei.frontend.linkoria.app.friendship.domain.usecase.AcceptFriendshipUseCase
import com.xinlei.frontend.linkoria.app.friendship.domain.usecase.DeclineFriendshipUseCase
import com.xinlei.frontend.linkoria.app.friendship.domain.usecase.GetPendingReceivedRequestsUseCase
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
class FriendReceivedViewModel @Inject constructor(
    private val getPendingReceivedRequestsUseCase: GetPendingReceivedRequestsUseCase,
    private val acceptFriendshipUseCase: AcceptFriendshipUseCase,
    private val declineFriendshipUseCase: DeclineFriendshipUseCase
) : ViewModel() {

    // Fuente de verdad local: lista completa cargada del backend
    private val _allReceived = MutableStateFlow<List<Friendship>>(emptyList())

    // Lista filtrada que consume el adapter rv_received
    private val _receivedState = MutableStateFlow<UiState<List<Friendship>>>(UiState.Idle)
    val receivedState: StateFlow<UiState<List<Friendship>>> = _receivedState.asStateFlow()

    // Query del et_filter para filtrar localmente
    private val _filterQuery = MutableStateFlow("")

    // Estado de aceptar solicitud
    private val _acceptState = MutableStateFlow<UiState<Unit>>(UiState.Idle)
    val acceptState: StateFlow<UiState<Unit>> = _acceptState.asStateFlow()

    // Estado de rechazar solicitud
    private val _declineState = MutableStateFlow<UiState<Unit>>(UiState.Idle)
    val declineState: StateFlow<UiState<Unit>> = _declineState.asStateFlow()

    private val _processingId = MutableStateFlow<String?>(null)
    val processingId: StateFlow<String?> = _processingId.asStateFlow()

    init {
        observeFilterQuery()
    }

    fun loadReceivedRequests() {
        viewModelScope.launch {
            _receivedState.value = UiState.Loading
            getPendingReceivedRequestsUseCase().collect { result ->
                when (result) {
                    is NetworkResult.Success -> {
                        _allReceived.value = result.data
                        applyFilter(_filterQuery.value)
                    }
                    is NetworkResult.Error -> {
                        _receivedState.value = UiState.Error(result.message ?: "Error desconocido")
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
            _allReceived.value
        } else {
            _allReceived.value.filter {
                it.friendUsername.contains(query, ignoreCase = true)
            }
        }
        _receivedState.value = UiState.Success(filtered)
    }

    private fun removeFromList(targetId: String) {
        _allReceived.value = _allReceived.value.filter { it.friendId != targetId }
        applyFilter(_filterQuery.value)
    }


    fun acceptFriendship(friendship: Friendship) {
        if (_processingId.value != null) return  // ya hay una operación en curso
        viewModelScope.launch {
            _processingId.value = friendship.friendId
            _acceptState.value = UiState.Loading
            when (val result = acceptFriendshipUseCase(friendship.friendId)) {
                is NetworkResult.Success -> {
                    removeFromList(friendship.friendId)
                    _acceptState.value = UiState.Success(Unit)
                }
                is NetworkResult.Error -> {
                    _acceptState.value = UiState.Error(result.message ?: "Error desconocido")
                }
                else -> Unit
            }
            _processingId.value = null
        }
    }

    fun declineFriendship(friendship: Friendship) {
        if (_processingId.value != null) return  // ya hay una operación en curso
        viewModelScope.launch {
            _processingId.value = friendship.friendId
            _declineState.value = UiState.Loading
            when (val result = declineFriendshipUseCase(friendship.friendId)) {
                is NetworkResult.Success -> {
                    removeFromList(friendship.friendId)
                    _declineState.value = UiState.Success(Unit)
                }
                is NetworkResult.Error -> {
                    _declineState.value = UiState.Error(result.message ?: "Error desconocido")
                }
                else -> Unit
            }
            _processingId.value = null
        }
    }

    fun resetAcceptState() {
        _acceptState.value = UiState.Idle
    }

    fun resetDeclineState() {
        _declineState.value = UiState.Idle
    }
}
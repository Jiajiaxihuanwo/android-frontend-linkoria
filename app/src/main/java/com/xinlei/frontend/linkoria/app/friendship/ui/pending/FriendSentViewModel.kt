package com.xinlei.frontend.linkoria.app.friendship.ui.sent

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xinlei.frontend.linkoria.app.core.network.NetworkResult
import com.xinlei.frontend.linkoria.app.core.ui.UiState
import com.xinlei.frontend.linkoria.app.friendship.domain.model.Friendship
import com.xinlei.frontend.linkoria.app.friendship.domain.usecase.GetPendingSentRequestsUseCase
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
class FriendSentViewModel @Inject constructor(
    private val getPendingSentRequestsUseCase: GetPendingSentRequestsUseCase
) : ViewModel() {

    private val _allSent = MutableStateFlow<List<Friendship>>(emptyList())

    private val _sentState = MutableStateFlow<UiState<List<Friendship>>>(UiState.Idle)
    val sentState: StateFlow<UiState<List<Friendship>>> = _sentState.asStateFlow()

    private val _filterQuery = MutableStateFlow("")

    init {
        observeFilterQuery()
    }

    fun loadSentRequests() {
        viewModelScope.launch {
            _sentState.value = UiState.Loading
            getPendingSentRequestsUseCase().collect { result ->
                when (result) {
                    is NetworkResult.Success -> {
                        _allSent.value = result.data
                        applyFilter(_filterQuery.value)
                    }
                    is NetworkResult.Error -> {
                        _sentState.value = UiState.Error(result.message ?: "Error desconocido")
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
            .debounce(300)
            .distinctUntilChanged()
            .onEach { query -> applyFilter(query) }
            .launchIn(viewModelScope)
    }

    private fun applyFilter(query: String) {
        val filtered = if (query.isBlank()) {
            _allSent.value
        } else {
            _allSent.value.filter {
                it.friendUsername.contains(query, ignoreCase = true)
            }
        }
        _sentState.value = UiState.Success(filtered)
    }
}
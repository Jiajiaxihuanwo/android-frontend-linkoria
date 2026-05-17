package com.xinlei.frontend.linkoria.app.conversation.ui.dm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xinlei.frontend.linkoria.app.conversation.domain.model.Conversation
import com.xinlei.frontend.linkoria.app.conversation.domain.usecase.GetMyDmsUseCase
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
class DmListViewModel @Inject constructor(
    private val getMyDmsUseCase: GetMyDmsUseCase
) : ViewModel() {
    private val _dmListState = MutableStateFlow<UiState<List<Conversation>>>(UiState.Idle)
    val dmListState = _dmListState.asStateFlow()

    private val _allDms = MutableStateFlow<List<Conversation>>(emptyList())
    private val _filterQuery = MutableStateFlow("")
    val filterQuery = _filterQuery.asStateFlow()

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
            _allDms.value
        } else {
            _allDms.value.filter {
                it.targetUsername?.contains(query, ignoreCase = true) ?: false
            }
        }
        _dmListState.value = UiState.Success(filtered)
    }

    fun loadDms() {
        viewModelScope.launch {
            _dmListState.value = UiState.Loading
            getMyDmsUseCase().collect { result ->
                when (result) {
                    is NetworkResult.Success -> {
                        _allDms.value = result.data
                        applyFilter(_filterQuery.value)
                    }
                    is NetworkResult.Error -> {
                        _dmListState.value = UiState.Error(result.message ?: "Error desconocido")
                    }
                    else -> Unit
                }
            }
        }
    }
}
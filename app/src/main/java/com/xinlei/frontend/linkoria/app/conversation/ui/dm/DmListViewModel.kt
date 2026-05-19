package com.xinlei.frontend.linkoria.app.conversation.ui.dm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xinlei.frontend.linkoria.app.conversation.domain.model.Conversation
import com.xinlei.frontend.linkoria.app.conversation.domain.usecase.GetMyDmsUseCase
import com.xinlei.frontend.linkoria.app.conversation.ui.dm.adapter.DmItem
import com.xinlei.frontend.linkoria.app.core.network.NetworkResult
import com.xinlei.frontend.linkoria.app.core.session.SessionManager
import com.xinlei.frontend.linkoria.app.core.ui.UiState
import com.xinlei.frontend.linkoria.app.message.domain.model.Message
import com.xinlei.frontend.linkoria.app.message.domain.usecase.GetLastMessageUseCase
import com.xinlei.frontend.linkoria.app.user.domain.model.User
import com.xinlei.frontend.linkoria.app.user.domain.usecase.GetUserByIdUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DmListViewModel @Inject constructor(
    private val getMyDmsUseCase: GetMyDmsUseCase,
    private val getUserByIdUseCase: GetUserByIdUseCase,
    private val getLastMessageUseCase: GetLastMessageUseCase,
    private val sessionManager: SessionManager,
) : ViewModel() {
    private val _dmListState = MutableStateFlow<UiState<List<DmItem>>>(UiState.Idle)
    val dmListState = _dmListState.asStateFlow()

    private val _allDms = MutableStateFlow<List<DmItem>>(emptyList())
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
                it.conversation.targetUsername?.contains(query, ignoreCase = true) ?: false
            }
        }
        _dmListState.value = UiState.Success(filtered)
    }

    fun loadDms() {
        viewModelScope.launch {
            _dmListState.value = UiState.Loading
            val currentUserId = sessionManager.getUserIdOnce()

            getMyDmsUseCase().collect { result ->
                when (result) {
                    is NetworkResult.Success -> {
                        _allDms.value = result.data.map { conversation ->
                            val lastMessage = (getLastMessageUseCase(conversation.id)
                                .first { it is NetworkResult.Success || it is NetworkResult.Error } as? NetworkResult.Success)?.data

                            val lastMessageSenderName = lastMessage?.userId?.let { senderId ->
                                (getUserByIdUseCase(senderId)
                                    .first { it is NetworkResult.Success || it is NetworkResult.Error } as? NetworkResult.Success)?.data?.username
                            }

                            DmItem(conversation, lastMessage, lastMessageSenderName, currentUserId!!)
                        }
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
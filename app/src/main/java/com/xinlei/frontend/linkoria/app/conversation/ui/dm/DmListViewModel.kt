package com.xinlei.frontend.linkoria.app.conversation.ui.dm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xinlei.frontend.linkoria.app.conversation.domain.model.Conversation
import com.xinlei.frontend.linkoria.app.conversation.domain.usecase.GetMyDmsUseCase
import com.xinlei.frontend.linkoria.app.core.network.NetworkResult
import com.xinlei.frontend.linkoria.app.core.ui.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DmListViewModel @Inject constructor(
    private val getMyDmsUseCase: GetMyDmsUseCase
) : ViewModel() {
    private val _dmListState = MutableStateFlow<UiState<List<Conversation>>>(UiState.Idle)
    val dmListState = _dmListState.asStateFlow()

    fun loadDms() {
        viewModelScope.launch {
            _dmListState.value = UiState.Loading
            getMyDmsUseCase().collect { result ->
                when (result) {
                    is NetworkResult.Success -> {
                        _dmListState.value = UiState.Success(result.data)
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
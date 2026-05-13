package com.xinlei.frontend.linkoria.app.server.ui

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xinlei.frontend.linkoria.app.core.network.NetworkResult
import com.xinlei.frontend.linkoria.app.core.ui.UiState
import com.xinlei.frontend.linkoria.app.server.domain.usecase.CreateServerUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateServerViewModel @Inject constructor(
    private val createServerUseCase: CreateServerUseCase
) : ViewModel() {

    private val _createState = MutableStateFlow<UiState<Unit>>(UiState.Idle)
    val createState = _createState.asStateFlow()

    fun createServer(name: String, serverIcon: Uri?) {
        viewModelScope.launch {
            _createState.value = UiState.Loading
            createServerUseCase(name, serverIcon).collect { result ->
                when (result) {
                    is NetworkResult.Success -> {
                        _createState.value = UiState.Success(Unit)
                    }
                    is NetworkResult.Error -> {
                        _createState.value = UiState.Error(result.message ?: "Error desconocido")
                    }
                    else -> Unit
                }
            }
        }
    }

    fun resetState() {
        _createState.value = UiState.Idle
    }
}
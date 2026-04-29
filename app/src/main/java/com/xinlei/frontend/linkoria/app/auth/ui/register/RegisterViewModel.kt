package com.xinlei.frontend.linkoria.app.auth.ui.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xinlei.frontend.linkoria.app.auth.domain.AuthUser
import com.xinlei.frontend.linkoria.app.auth.domain.usecase.RegisterUseCase
import com.xinlei.frontend.linkoria.app.core.network.NetworkResult
import com.xinlei.frontend.linkoria.app.core.ui.UiEvent
import com.xinlei.frontend.linkoria.app.core.ui.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val registerUseCase: RegisterUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState<AuthUser>>(UiState.Idle)
    val uiState: StateFlow<UiState<AuthUser>> = _uiState.asStateFlow()

    private val _uiEvent = MutableSharedFlow<UiEvent>()
    val uiEvent: SharedFlow<UiEvent> = _uiEvent.asSharedFlow()

    fun register(username: String, email: String, password: String) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            when (val result = registerUseCase(username, email, password)) {
                is NetworkResult.Success -> {
                    _uiEvent.emit(UiEvent.ShowToast("Cuenta creada correctamente"))
                    _uiState.value = UiState.Success(result.data)
                }
                is NetworkResult.Error -> {
                    val message = when (result.code) {
                        null -> "No se puede conectar con el servidor"
                        500 -> "Error interno del servidor"
                        409 -> "Nombre o Email ya tomados"
                        else -> result.message ?: "Error desconocido"
                    }
                    _uiState.value = UiState.Error(message)
                    _uiEvent.emit(UiEvent.ShowToast(result.message ?: "Error desconocido"))
                }
                else -> Unit
            }
        }
    }

}
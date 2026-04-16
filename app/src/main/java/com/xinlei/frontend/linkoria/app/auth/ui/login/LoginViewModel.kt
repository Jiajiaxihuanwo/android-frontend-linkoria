package com.xinlei.frontend.linkoria.app.auth.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xinlei.frontend.linkoria.app.auth.domain.AuthUser
import com.xinlei.frontend.linkoria.app.auth.domain.usecase.LoginUseCase
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
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState<AuthUser>>(UiState.Idle)
    val uiState: StateFlow<UiState<AuthUser>> = _uiState.asStateFlow()

    private val _uiEvent = MutableSharedFlow<UiEvent>()
    val uiEvent: SharedFlow<UiEvent> = _uiEvent.asSharedFlow()

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            when (val result = loginUseCase(email, password)) {
                is NetworkResult.Success -> {
                    _uiState.value = UiState.Success(result.data)
                    _uiEvent.emit(UiEvent.ShowToast("Login realizado correctamente"))
                }
                is NetworkResult.Error -> {
                    val message = when {
                        result.code == null -> "No se puede conectar con el servidor"
                        result.code == 500 -> "Error interno del servidor"
                        result.code == 401 -> "Email o contraseña incorrectos"
                        else -> result.message ?: "Error desconocido"
                    }
                    _uiState.value = UiState.Error(message)
                    _uiEvent.emit(UiEvent.ShowToast(result.message?: "Error desconocido"))
                }
                else -> Unit
            }
        }
    }

    fun onNavigationDone() {
        _uiState.value = UiState.Idle
    }
}
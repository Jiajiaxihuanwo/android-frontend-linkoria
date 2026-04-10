package com.xinlei.frontend.linkoria.app.auth.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xinlei.frontend.linkoria.app.auth.domain.AuthUser
import com.xinlei.frontend.linkoria.app.auth.domain.usecase.LoginUseCase
import com.xinlei.frontend.linkoria.app.core.network.NetworkResult
import com.xinlei.frontend.linkoria.app.core.ui.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState<AuthUser>>(UiState.Idle)
    val uiState: StateFlow<UiState<AuthUser>> = _uiState.asStateFlow()

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            when (val result = loginUseCase(email, password)) {
                is NetworkResult.Success -> _uiState.value = UiState.Success(result.data)
                is NetworkResult.Error -> _uiState.value = UiState.Error(result.message ?: "Error desconocido")
                else -> Unit
            }
        }
    }
}
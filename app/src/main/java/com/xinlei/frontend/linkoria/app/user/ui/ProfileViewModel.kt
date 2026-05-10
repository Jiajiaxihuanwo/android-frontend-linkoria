package com.xinlei.frontend.linkoria.app.user.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xinlei.frontend.linkoria.app.auth.domain.AuthUser
import com.xinlei.frontend.linkoria.app.core.network.NetworkResult
import com.xinlei.frontend.linkoria.app.core.session.SessionManager
import com.xinlei.frontend.linkoria.app.core.ui.UiState
import com.xinlei.frontend.linkoria.app.user.data.remote.dto.UserResponse
import com.xinlei.frontend.linkoria.app.user.domain.model.User
import com.xinlei.frontend.linkoria.app.user.domain.usecase.GetUserProfileUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val sessionManager: SessionManager,
    private val getUserProfileUseCase: GetUserProfileUseCase
) : ViewModel() {

    private val _logoutEvent = MutableStateFlow(false)
    val logoutEvent = _logoutEvent.asStateFlow()
    private val _userState = MutableStateFlow<UiState<User>>(UiState.Idle)
    val userState = _userState.asStateFlow()

    fun logout() {
        viewModelScope.launch {
            sessionManager.clearSession()
            _logoutEvent.value = true
        }
    }

    fun loadProfile() {
        viewModelScope.launch {
            _userState.value = UiState.Loading
            delay(2000)
            getUserProfileUseCase().collect { result ->
                when(result) {
                    is NetworkResult.Success -> {
                        val user = User(result.data.id,result.data.username,result.data.email,"https://devsapihub.com/img-fast-food/cafe_03.jpg")
                        _userState.value = UiState.Success(user)
                    }

                    is NetworkResult.Error -> {
                        _userState.value = UiState.Error(result.message ?: "Error desconocido")
                    }

                    else -> Unit
                }
            }
        }
    }

    fun updateProfile() {

    }
}
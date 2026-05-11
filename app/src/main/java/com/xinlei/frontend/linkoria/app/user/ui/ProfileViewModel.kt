package com.xinlei.frontend.linkoria.app.user.ui

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xinlei.frontend.linkoria.app.auth.domain.AuthUser
import com.xinlei.frontend.linkoria.app.core.network.NetworkResult
import com.xinlei.frontend.linkoria.app.core.session.SessionManager
import com.xinlei.frontend.linkoria.app.core.ui.UiState
import com.xinlei.frontend.linkoria.app.user.data.remote.dto.UserResponse
import com.xinlei.frontend.linkoria.app.user.domain.model.User
import com.xinlei.frontend.linkoria.app.user.domain.usecase.GetUserProfileUseCase
import com.xinlei.frontend.linkoria.app.user.domain.usecase.UpdateUserUseCase
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
    private val getUserProfileUseCase: GetUserProfileUseCase,
    private val updateUserUseCase: UpdateUserUseCase
) : ViewModel() {

    private val _logoutEvent = MutableStateFlow(false)
    val logoutEvent = _logoutEvent.asStateFlow()

    private val _userState = MutableStateFlow<UiState<User>>(UiState.Idle)
    val userState = _userState.asStateFlow()


    private val _updateState = MutableStateFlow<UiState<User>>(UiState.Idle)
    val updateState = _updateState.asStateFlow()

    fun logout() {
        viewModelScope.launch {
            sessionManager.clearSession()
            _logoutEvent.value = true
        }
    }

    fun loadProfile() {
        viewModelScope.launch {
            _userState.value = UiState.Loading
            getUserProfileUseCase().collect { result ->
                when(result) {
                    is NetworkResult.Success -> {
                        val user = User(result.data.id,result.data.username,result.data.email,result.data.avatarUrl)
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

    fun updateProfile(username: String? = null, email: String? = null, avatarUri: Uri? = null) {
        viewModelScope.launch {
            val oldData = (_userState.value as? UiState.Success)?.data
            updateUserUseCase(username = username, email = email, avatarUri = avatarUri).collect { result ->
                when(result) {
                    is NetworkResult.Success -> {
                        val user = result.data.copy(
                            avatarUrl = avatarUri?.toString() ?: result.data.avatarUrl
                        )
                        _userState.value = UiState.Success(user)
                        _updateState.value = UiState.Success(user)
                    }
                    is NetworkResult.Error -> {
                        _updateState.value = UiState.Error(result.message ?: "Error desconocido")
                        _updateState.value = UiState.Idle
                        _userState.value = if (oldData != null) UiState.Success(oldData) else UiState.Idle
                    }
                    else -> Unit
                }
            }
        }
    }

    fun resetUpdateState() {
        _updateState.value = UiState.Idle
    }
}
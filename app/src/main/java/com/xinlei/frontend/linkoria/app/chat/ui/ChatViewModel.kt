package com.xinlei.frontend.linkoria.app.chat.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xinlei.frontend.linkoria.app.core.network.NetworkResult
import com.xinlei.frontend.linkoria.app.core.ui.UiState
import com.xinlei.frontend.linkoria.app.user.domain.model.User
import com.xinlei.frontend.linkoria.app.user.domain.usecase.GetUserByIdUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val getUserByIdUseCase: GetUserByIdUseCase
) : ViewModel() {
    private val _friendProfileState = MutableStateFlow<UiState<User?>>(UiState.Loading)
    val friendProfileState: StateFlow<UiState<User?>> = _friendProfileState.asStateFlow()

    fun loadUserProfile(userId: String) {
        viewModelScope.launch {
            _friendProfileState.value = UiState.Loading
            getUserByIdUseCase(userId).collect { result ->
                when (result) {
                    is NetworkResult.Success -> {
                        val user = result.data
                        _friendProfileState.value = UiState.Success(user)
                    }
                    is NetworkResult.Error -> {
                        _friendProfileState.value = UiState.Error(result.message ?: "Error desconocido")
                    }
                    else -> Unit
                }
            }
        }
    }
}

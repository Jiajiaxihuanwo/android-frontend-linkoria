package com.xinlei.frontend.linkoria.app.conversation.ui.dm.friendprofile

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
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class FriendProfileViewModel @Inject constructor(
    private val getUserByIdUseCase: GetUserByIdUseCase
) : ViewModel() {

    private val _userState = MutableStateFlow<UiState<User?>>(UiState.Loading)
    val userState: StateFlow<UiState<User?>> = _userState.asStateFlow()

    fun loadUser(userId: String) {
        viewModelScope.launch {
            _userState.value = UiState.Loading
            getUserByIdUseCase(userId).collect { result ->
                when (result) {
                    is NetworkResult.Success -> {
                        _userState.value = UiState.Success(result.data)
                    }
                    is NetworkResult.Error -> {
                        _userState.value = UiState.Error(result.message ?: "Error desconocido")
                    }
                    else -> Unit
                }
            }
        }
    }

    fun getMemberSinceDate(user: User): String {
        return try {
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS")
            val date = LocalDateTime.parse(user.createdAt, formatter)
            val output = DateTimeFormatter.ofPattern("d MMM yyyy", Locale("es"))
            date.format(output)
        } catch (e: Exception) {
            "Fecha desconocida"
        }
    }


}
package com.xinlei.frontend.linkoria.app.root

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xinlei.frontend.linkoria.app.core.network.NetworkResult
import com.xinlei.frontend.linkoria.app.core.ui.UiState
import com.xinlei.frontend.linkoria.app.server.domain.model.Server
import com.xinlei.frontend.linkoria.app.server.domain.usecase.CreateServerUseCase
import com.xinlei.frontend.linkoria.app.user.domain.model.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val createServerUseCase: CreateServerUseCase
) : ViewModel(){
    private val _serverstate = MutableStateFlow<UiState<List<Server>>>(UiState.Idle)
    val userState = _serverstate.asStateFlow()


    fun createSever(serverName : String){
        viewModelScope.launch {
            createServerUseCase(serverName).collect { result ->
                when(result) {
                    is NetworkResult.Success -> {
                        result.data
                    }
                    is NetworkResult.Error -> {

                    }

                    else -> Unit
                }
            }
        }
    }
}
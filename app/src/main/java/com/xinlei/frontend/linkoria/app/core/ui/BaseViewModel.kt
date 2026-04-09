package com.xinlei.frontend.linkoria.app.core.ui

import androidx.lifecycle.ViewModel
import com.xinlei.frontend.linkoria.app.core.network.NetworkResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

abstract class BaseViewModel : ViewModel() {

    private val _uiState = MutableStateFlow<UiState<Any>>(UiState.Idle)
    val uiState: StateFlow<UiState<Any>> = _uiState.asStateFlow()

    protected fun setLoading() {
        _uiState.value = UiState.Loading
    }

    protected fun setError(message: String) {
        _uiState.value = UiState.Error(message)
    }

    protected fun setIdle() {
        _uiState.value = UiState.Idle
    }

    protected suspend fun <T> handleResult(
        result: NetworkResult<T>,
//        "un parámetro llamado onSuccess que es una función suspendida, que recibe un valor de tipo T y no devuelve nada"
        onSuccess: suspend (T) -> Unit
    ) {
        when (result) {
            is NetworkResult.Loading -> setLoading()
            is NetworkResult.Error -> setError(result.message ?: "Error desconocido")
            is NetworkResult.Success -> onSuccess(result.data)
        }
    }
}
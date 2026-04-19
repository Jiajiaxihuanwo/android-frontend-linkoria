package com.xinlei.frontend.linkoria.app.core.ui

sealed class UiEvent {
    data class ShowToast(val message: String) : UiEvent()
    data class ShowSnackbar(val message: String, val actionLabel: String? = null) : UiEvent()
}
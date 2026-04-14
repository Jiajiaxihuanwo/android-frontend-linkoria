package com.xinlei.frontend.linkoria.app.core.ui

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.launch

abstract class BaseFragment : Fragment() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeUiState()
    }

    abstract fun getViewModel(): BaseViewModel

    private fun observeUiState() {
        viewLifecycleOwner.lifecycleScope.launch { // Lanza una corrutina ligada al ciclo de vida de la vista del Fragment
            repeatOnLifecycle(Lifecycle.State.STARTED) { // Ejecuta el bloque solo mientras el Fragment esté en estado STARTED (Pause ya no por ejemplo)
                getViewModel().uiState.collect { state -> // Observa el StateFlow 'uiState' del ViewModel; se ejecuta cada vez que cambia el estado
                    when (state) {
                        is UiState.Loading -> onLoading()
                        is UiState.Error -> onError(state.message)
                        is UiState.Success<*> -> Unit
                        is UiState.Idle -> Unit
                    }
                }
            }
        }
    }

    protected open fun onLoading() {}

    protected open fun onError(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
}
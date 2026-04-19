package com.xinlei.frontend.linkoria.app.auth.ui.login

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.xinlei.frontend.linkoria.app.R
import com.xinlei.frontend.linkoria.app.core.ui.UiEvent
import com.xinlei.frontend.linkoria.app.core.ui.UiState
import com.xinlei.frontend.linkoria.app.databinding.FragmentLoginBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private val viewModel: LoginViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupListeners()
        observeUiState()
        observeUiEvent()
    }

    private fun observeUiEvent() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiEvent.collect { event ->
                    when (event) {
                        is UiEvent.ShowToast -> Toast.makeText(requireContext(), event.message, Toast.LENGTH_SHORT).show()
                        else -> Unit
                    }
                }
            }
        }
    }

    private fun observeUiState() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->

                    binding.btnLogin.isEnabled = state !is UiState.Loading
                    binding.tvErrorMessage.visibility = if (state is UiState.Error) View.VISIBLE else View.GONE

                    when (state) {
                        is UiState.Success ->{
                            //TODO: NAVEGAR AL SIGUIENTE FRAGMENT
                            findNavController().popBackStack(R.id.authFragment,false)
                        }
                        is UiState.Error -> {
                            binding.btnLogin.isEnabled = true
                            binding.tvErrorMessage.text = state.message
                            binding.tvErrorMessage.visibility = View.VISIBLE
                        }
                        else -> Unit
                    }
                }
            }
        }
    }

    private fun setupListeners() {
        binding.btnLogin.setOnClickListener {
            val  email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()
            viewModel.login(email, password)
        }

        binding.toolbar.setNavigationOnClickListener {
            findNavController().popBackStack(R.id.authFragment, false)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}
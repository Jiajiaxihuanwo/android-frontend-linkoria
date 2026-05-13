package com.xinlei.frontend.linkoria.app.server.ui

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.xinlei.frontend.linkoria.app.R
import com.xinlei.frontend.linkoria.app.core.ui.UiState
import com.xinlei.frontend.linkoria.app.core.ui.image.ImageLoader
import com.xinlei.frontend.linkoria.app.databinding.ActivityJoinServerBinding
import com.xinlei.frontend.linkoria.app.server.domain.model.Server
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlinx.coroutines.launch

@AndroidEntryPoint
class JoinServerActivity : AppCompatActivity() {

    @Inject
    lateinit var imageLoader: ImageLoader

    private lateinit var binding: ActivityJoinServerBinding
    private val viewModel: JoinServerViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        overrideActivityTransition()
        binding = ActivityJoinServerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        configInsets()
        setupClickListeners()
        setupTextWatcher()
        observeUiState()
    }

    private fun overrideActivityTransition() {
        overrideActivityTransition(OVERRIDE_TRANSITION_OPEN, R.anim.slide_out_down, R.anim.static_on)
        overrideActivityTransition(OVERRIDE_TRANSITION_CLOSE, R.anim.static_on, R.anim.slide_in_up)
    }

    private fun configInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(view.paddingLeft, systemBars.top, view.paddingRight, view.paddingBottom)
            insets
        }
    }

    private fun setupClickListeners() {
        binding.btnBack.setOnClickListener {
            finish()
        }

        binding.icSearch.setOnClickListener {
            val inviteCode = binding.etInvite.text.toString()
            if (inviteCode.isNotBlank()) viewModel.findServer(inviteCode)
        }

        binding.btnJoinServer.setOnClickListener {
            viewModel.joinServer()
        }
    }

    private fun setupTextWatcher() {
        binding.etInvite.doAfterTextChanged {
            if (it.isNullOrBlank()) hideServerPreview()
        }
    }

    private fun observeUiState() {
        observeFindState()
        observeJoinState()
    }

    private fun observeFindState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.findState.collect { state ->
                    when (state) {
                        is UiState.Loading -> binding.icSearch.isEnabled = false
                        is UiState.Success -> {
                            binding.icSearch.isEnabled = true
                            showServerPreview(state.data)
                        }
                        is UiState.Error -> {
                            binding.icSearch.isEnabled = true
                            hideServerPreview()
                            Toast.makeText(this@JoinServerActivity, state.message, Toast.LENGTH_LONG).show()
                            viewModel.resetFindState()
                        }
                        is UiState.Idle ->{
                            binding.icSearch.isEnabled = true
                        }
                    }
                }
            }
        }
    }

    private fun observeJoinState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.joinState.collect { state ->
                    when (state) {
                        is UiState.Loading -> binding.btnJoinServer.isEnabled = false
                        is UiState.Success -> finish()
                        is UiState.Error -> {
                            binding.btnJoinServer.isEnabled = true
                            Toast.makeText(this@JoinServerActivity, state.message, Toast.LENGTH_LONG).show()
                            viewModel.resetJoinState()
                        }
                        is UiState.Idle -> Unit
                    }
                }
            }
        }
    }

    private fun showServerPreview(server: Server) {
        binding.imgServerIcon.visibility = View.VISIBLE
        binding.tvServerName.visibility = View.VISIBLE
        binding.tvServerName.text = server.name
        imageLoader.loadIcon(binding.imgServerIcon, server.iconUrl)
        binding.btnJoinServer.isEnabled = true
    }

    private fun hideServerPreview() {
        binding.imgServerIcon.visibility = View.GONE
        binding.tvServerName.visibility = View.GONE
        binding.btnJoinServer.isEnabled = false
    }
}
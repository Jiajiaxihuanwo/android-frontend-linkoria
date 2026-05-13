package com.xinlei.frontend.linkoria.app.server.ui

import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.xinlei.frontend.linkoria.app.R
import com.xinlei.frontend.linkoria.app.core.ui.UiState
import com.xinlei.frontend.linkoria.app.core.ui.image.ImageLoader
import com.xinlei.frontend.linkoria.app.databinding.ActivityCreateServerBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class CreateServerActivity : AppCompatActivity() {

    @Inject
    lateinit var imageLoader: ImageLoader

    private lateinit var binding: ActivityCreateServerBinding
    private val viewModel: CreateServerViewModel by viewModels()

    private var selectedAvatarUri: Uri? = null


    private val pickImageLauncher = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        uri?.let {
            selectedAvatarUri = it
            binding.ivUpload.visibility = View.INVISIBLE
            binding.ivPlus.visibility = View.INVISIBLE
            binding.ivServerIcon.visibility = View.VISIBLE
            imageLoader.loadIconNoCache(binding.ivServerIcon, selectedAvatarUri.toString())
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        overrideActivityTransition()
        binding = ActivityCreateServerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        configInsets()
        setupClickListeners()
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

        binding.btnCreateServer.setOnClickListener {
            val name = binding.etServerName.text.toString()
            viewModel.createServer(name, selectedAvatarUri)
        }

        binding.layoutUpload.setOnClickListener {
            pickImageLauncher.launch(
                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
            )
        }
    }

    private fun observeUiState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.createState.collect { state ->
                    when (state) {
                        is UiState.Loading -> binding.btnCreateServer.isEnabled = false
                        is UiState.Success -> finish()
                        is UiState.Error -> {
                            binding.btnCreateServer.isEnabled = true
                            Toast.makeText(this@CreateServerActivity, state.message, Toast.LENGTH_LONG).show()
                            viewModel.resetState()
                        }
                        is UiState.Idle -> binding.btnCreateServer.isEnabled = true
                    }
                }
            }
        }
    }
}
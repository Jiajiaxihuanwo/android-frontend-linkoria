package com.xinlei.frontend.linkoria.app.chat.ui

import android.content.Intent
import android.os.Bundle
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.xinlei.frontend.linkoria.app.R
import com.xinlei.frontend.linkoria.app.core.network.NetworkResult
import com.xinlei.frontend.linkoria.app.core.ui.UiState
import com.xinlei.frontend.linkoria.app.databinding.ActivityChatBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
@AndroidEntryPoint
class ChatActivity : AppCompatActivity() {

    private var _binding: ActivityChatBinding? = null
    private val binding get() = _binding!!
    private var otherUserId: String? = null
    private val viewModel: ChatViewModel by viewModels()
    private var currentAvatarUrl: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityChatBinding.inflate(layoutInflater)
        overrideActivityTransition()
        setContentView(binding.root)

        configInsets()
        setupClickListeners()

        otherUserId = intent.getStringExtra("extra_user_id")

        otherUserId?.let {
            viewModel.loadUserProfile(it)
            observeUserData()
        }
    }

    private fun observeUserData() {
        lifecycleScope.launch {
            viewModel.friendProfileState.collect { state ->
                when (state) {
                    is UiState.Loading -> {
                        binding.tvUsername.text = "Cargando..."
                        binding.ivAvatar.setImageResource(R.drawable.ic_user)
                    }
                    is UiState.Success -> {
                        val user = state.data
                        if (user != null) {

                            binding.tvUsername.text = user.username

                            if (user.avatarUrl.isNotEmpty()) {
                                loadAvatarWithGlide(user.avatarUrl)

                                currentAvatarUrl = user.avatarUrl
                            }
                        }
                    }
                    is UiState.Error -> {
                        binding.tvUsername.text = "Error"
                        binding.ivAvatar.setImageResource(R.drawable.ic_user)
                        Toast.makeText(this@ChatActivity, state.message ?: "加载用户信息失败", Toast.LENGTH_SHORT).show()
                    }
                    UiState.Idle -> {

                    }
                }
            }
        }
    }

    private fun loadAvatarWithGlide(avatarUrl: String) {
        Glide.with(this)
            .load(avatarUrl)
            .placeholder(R.drawable.ic_user)
            .error(R.drawable.ic_user)
            .circleCrop()
            .into(binding.ivAvatar)
    }

    private fun setupClickListeners() {
        binding.ivArrowBack.setOnClickListener {
            navigateToMain()
        }

        binding.ivAvatar.setOnClickListener {
            showZoomedImage()
        }

        binding.tvUsername.setOnClickListener {
            openFriendProfile()
        }

        binding.ivMore.setOnClickListener {
            openFriendProfile()
        }

        binding.btnSend.setOnClickListener {
            val messageText = binding.etMessage.text.toString().trim()
            if (messageText.isNotEmpty()) {
                binding.etMessage.setText("")
            }
        }

        binding.btnCamera.setOnClickListener {
        }
    }

    private fun navigateToMain() {
        finish()
    }

    private fun openFriendProfile() {
        if (otherUserId.isNullOrEmpty()) {
            return
        }

        try {
            val intent = Intent(this, Class.forName("com.xinlei.frontend.linkoria.app.conversation.ui.dm.friendprofile.FriendProfile")).apply {
                putExtra("extra_user_id", otherUserId)
            }
            startActivity(intent)
        } catch (e: ClassNotFoundException) {
            android.util.Log.e("ChatActivity", "FriendProfile not found", e)
            Toast.makeText(this, "No ha podido abrir", Toast.LENGTH_SHORT).show()
        }
    }

    private fun overrideActivityTransition() {
        overrideActivityTransition(
            OVERRIDE_TRANSITION_OPEN,
            R.anim.slide_in_right,
            R.anim.static_on
        )
        overrideActivityTransition(
            OVERRIDE_TRANSITION_CLOSE,
            R.anim.static_on,
            R.anim.slide_out_right
        )
    }

    private fun configInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(
                systemBars.left,
                systemBars.top,
                systemBars.right,
                systemBars.bottom
            )
            insets
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    // Dialog foto
    private fun showZoomedImage() {
        val imageUrl = currentAvatarUrl

        if (imageUrl.isNullOrEmpty()) {
            Toast.makeText(this, "No hay foto perfil", Toast.LENGTH_SHORT).show()
            return
        }

        val dialog = android.app.Dialog(this, android.R.style.Theme_Black_NoTitleBar_Fullscreen)
        val imageView = androidx.appcompat.widget.AppCompatImageView(this)
        imageView.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        imageView.scaleType = android.widget.ImageView.ScaleType.FIT_CENTER

        Glide.with(this)
            .load(imageUrl)
            .placeholder(R.drawable.ic_user)
            .error(R.drawable.ic_user)
            .into(imageView)

        dialog.setContentView(imageView)
        dialog.show()

        imageView.setOnClickListener {
            dialog.dismiss()
        }
    }
}
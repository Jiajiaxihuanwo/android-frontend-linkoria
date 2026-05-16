package com.xinlei.frontend.linkoria.app.conversation.ui.dm.friendprofile

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.view.MotionEvent
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.bumptech.glide.Glide
import com.xinlei.frontend.linkoria.app.R
import com.xinlei.frontend.linkoria.app.core.ui.UiState
import com.xinlei.frontend.linkoria.app.core.ui.image.ImageLoader
import com.xinlei.frontend.linkoria.app.databinding.ActivityFriendProfileBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class FriendProfile : AppCompatActivity() {

    private lateinit var binding: ActivityFriendProfileBinding

    private val viewModel: FriendProfileViewModel by viewModels()

    @Inject
    lateinit var imageLoader: ImageLoader

    private var userId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityFriendProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        applyWindowInsets()
        setupBioScroll()

        userId = intent.getStringExtra("extra_user_id")
        userId?.let {
            viewModel.loadUser(it)
            observeUserData()
        }

        setupClickListeners()
    }

    private fun applyWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, 0, systemBars.right, systemBars.bottom)
            insets
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupBioScroll() {
        binding.tvDescription.movementMethod = ScrollingMovementMethod()
        binding.tvDescription.setOnTouchListener { v, event ->
            val canScroll = v.canScrollVertically(1) || v.canScrollVertically(-1)

            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    v.parent.requestDisallowInterceptTouchEvent(canScroll)
                }
                MotionEvent.ACTION_UP -> {
                    v.parent.requestDisallowInterceptTouchEvent(false)
                    v.performClick()
                }
            }

            false
        }
    }

    private fun observeUserData() {
        lifecycleScope.launch {
            repeatOnLifecycle(androidx.lifecycle.Lifecycle.State.STARTED) {
                viewModel.userState.collect { state ->
                    when (state) {
                        is UiState.Loading -> Unit
                        is UiState.Success -> state.data?.let { displayUserInfo(it) }
                        is UiState.Error -> Toast.makeText(this@FriendProfile, state.message, Toast.LENGTH_SHORT).show()
                        else -> {}
                    }
                }
            }
        }
    }

    private fun displayUserInfo(user: com.xinlei.frontend.linkoria.app.user.domain.model.User) {
        binding.tvUsername.text = user.username
        binding.tvUserTag.text = user.email
        binding.tvMemberSince.text = viewModel.getMemberSinceDate(user)
        binding.tvDescription.text = user.bio

        val avatarUrl = user.avatarUrl
        if (avatarUrl.isNotEmpty()) {
            imageLoader.loadIcon(binding.ivAvatar, avatarUrl)

            imageLoader.extractDominantColor(avatarUrl) { color ->
                binding.ivBanner.setBackgroundColor(color)
            }
        }
    }

    private fun setupClickListeners() {
        binding.ivArrowBack.setOnClickListener {
            finish()
        }

        binding.ivAvatar.setOnClickListener {
            showZoomedAvatar()
        }
    }

    private fun showZoomedAvatar() {
        val currentState = viewModel.userState.value
        val imageUrl = (currentState as? UiState.Success)?.data?.avatarUrl

        if (imageUrl.isNullOrEmpty()) {
            Toast.makeText(this, "No hay foto de perfil", Toast.LENGTH_SHORT).show()
            return
        }

        val dialog = android.app.Dialog(this, android.R.style.Theme_Black_NoTitleBar_Fullscreen)
        val imageView = androidx.appcompat.widget.AppCompatImageView(this).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            scaleType = android.widget.ImageView.ScaleType.FIT_CENTER
        }

        Glide.with(this)
            .load(imageUrl)
            .placeholder(R.drawable.ic_user)
            .error(R.drawable.ic_user)
            .into(imageView)

        dialog.setContentView(imageView)
        dialog.show()

        imageView.setOnClickListener { dialog.dismiss() }
    }
}
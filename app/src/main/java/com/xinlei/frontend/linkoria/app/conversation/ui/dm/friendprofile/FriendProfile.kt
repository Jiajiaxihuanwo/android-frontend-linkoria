package com.xinlei.frontend.linkoria.app.conversation.ui.dm.friendprofile

import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
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

        userId = intent.getStringExtra("extra_user_id")

        userId?.let {
            viewModel.loadUser(it)
            observeUserData()
        }

        setupClickListeners()
        setupTextWatcher()
    }

    private fun applyWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun observeUserData() {
        lifecycleScope.launch {
            repeatOnLifecycle(androidx.lifecycle.Lifecycle.State.STARTED) {
                viewModel.userState.collect { state ->
                    when (state) {
                        is UiState.Loading -> {
                            binding.etUsername.setText("Cargando...")
                            binding.tvUserTag.text = "@Cargando"
                            binding.ivAvatar.setImageResource(R.drawable.ic_user)
                        }
                        is UiState.Success -> {
                            val user = state.data
                            user?.let {
                                displayUserInfo(it)
                            }
                        }
                        is UiState.Error -> {
                            binding.etUsername.setText("Error")
                            Toast.makeText(this@FriendProfile, state.message, Toast.LENGTH_SHORT).show()
                        }

                        else -> {}
                    }
                }
            }
        }
    }

    private fun displayUserInfo(user: com.xinlei.frontend.linkoria.app.user.domain.model.User) {
        // 从网络请求获取的数据
        binding.etUsername.setText(user.username)
        binding.tvUserTag.text = "@${user.username.take(8) ?: "unknown"}"
        binding.tvMemberSince.text = "Miembro desde ${getCurrentDate()}"

        val avatarUrl = user.avatarUrl
        if (!avatarUrl.isNullOrEmpty()) {
            Glide.with(this)
                .load(avatarUrl)
                .placeholder(R.drawable.ic_user)
                .error(R.drawable.ic_user)
                .circleCrop()
                .into(binding.ivAvatar)

            // 提取主色调设置 banner 背景
            imageLoader.extractDominantColor(avatarUrl) { color ->
                binding.ivBanner.setBackgroundColor(color)
            }
        } else {
            binding.ivAvatar.setImageResource(R.drawable.ic_user)
            binding.ivBanner.setBackgroundColor(Color.parseColor("#2A1E2D"))
        }
    }

    // 删除原来的 getIntentData() 和旧的 displayUserInfo()

    private fun setupClickListeners() {
        binding.ivArrowBack.setOnClickListener {
            finish()
        }

        binding.btnMore.setOnClickListener {
            showMoreOptions()
        }

        binding.ivAvatar.setOnClickListener {
            showZoomedImage()
        }
    }

    private fun setupTextWatcher() {
        binding.etUsername.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun showMoreOptions() {
        val options = arrayOf("Bloquear usuario", "Reportar", "Copiar ID")
        AlertDialog.Builder(this)
            .setTitle("Más opciones")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> blockUser()
                    1 -> reportUser()
                    2 -> copyUserId()
                }
            }
            .show()
    }

    private fun blockUser() {
        // TODO si se puede
    }

    private fun reportUser() {
        // TODO si se puede
    }

    private fun copyUserId() {
        userId?.let {
            val clipboard = getSystemService(CLIPBOARD_SERVICE) as android.content.ClipboardManager
            val clip = android.content.ClipData.newPlainText("User ID", it)
            clipboard.setPrimaryClip(clip)
            Toast.makeText(this, "ID de usuario copiado", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getCurrentDate(): String {
        val dateFormat = java.text.SimpleDateFormat("d MMM yyyy", java.util.Locale.getDefault())
        return dateFormat.format(java.util.Date())
    }

    private fun showZoomedImage() {
        // 从当前显示的用户数据获取头像 URL
        val currentState = viewModel.userState.value
        val imageUrl = if (currentState is UiState.Success) {
            currentState.data?.avatarUrl
        } else {
            null
        }

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


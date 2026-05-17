package com.xinlei.frontend.linkoria.app.chat.ui

import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.bumptech.glide.Glide
import com.xinlei.frontend.linkoria.app.R
import com.xinlei.frontend.linkoria.app.channel.domain.model.Channel
import com.xinlei.frontend.linkoria.app.conversation.ui.dm.friendprofile.FriendProfile
import com.xinlei.frontend.linkoria.app.core.ui.UiState
import com.xinlei.frontend.linkoria.app.databinding.ActivityChatBinding
import com.xinlei.frontend.linkoria.app.root.navigator.ServerMemberNavigator
import com.xinlei.frontend.linkoria.app.user.domain.model.User
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ChatActivity : AppCompatActivity() {

    @Inject
    lateinit var serverMemberNavigator: ServerMemberNavigator

    private var _binding: ActivityChatBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ChatViewModel by viewModels()

    private var chatType: String? = null
    private var targetId: String? = null
    private var currentAvatarUrl: String? = null

    private var serverId: Long = -1L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityChatBinding.inflate(layoutInflater)
        overrideActivityTransition()
        setContentView(binding.root)

        configInsets()
        setupClickListeners()
        initChat()
    }

    private fun initChat() {
        chatType = intent.getStringExtra(EXTRA_CHAT_TYPE)

        when (chatType) {
            TYPE_DM -> {
                val conversationId = intent.getLongExtra(EXTRA_CONVERSATION_ID, -1L)
                targetId = intent.getStringExtra(EXTRA_TARGET_ID)
                if (conversationId != -1L && !targetId.isNullOrEmpty()) {
                    viewModel.initDmChat(conversationId, targetId!!)
                    observeDmState()
                }
            }
            TYPE_CHANNEL -> {
                serverId = intent.getLongExtra(EXTRA_SERVER_ID, -1L)
                val channelId = intent.getLongExtra(EXTRA_CHANNEL_ID, -1L)
                if (serverId != -1L && channelId != -1L) {
                    viewModel.initChannelChat(serverId, channelId)
                    observeChannelState()
                }
                binding.ivAvatar.visibility = View.GONE
                binding.icChannel.visibility = View.VISIBLE
            }
        }
    }

    private fun observeDmState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.dmState.collect { state ->
                    when (state) {
                        is UiState.Loading -> {
                            binding.tvTitle.text = "Cargando..."
                            binding.ivAvatar.setImageResource(R.drawable.ic_user)
                        }
                        is UiState.Success -> state.data?.let { renderDmToolbar(it) }
                        is UiState.Error -> {
                            binding.tvTitle.text = "Error"
                            binding.ivAvatar.setImageResource(R.drawable.ic_user)
                            Toast.makeText(this@ChatActivity, state.message, Toast.LENGTH_SHORT).show()
                        }
                        else -> Unit
                    }
                }
            }
        }
    }

    private fun observeChannelState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.channelState.collect { state ->
                    when (state) {
                        is UiState.Loading -> binding.tvTitle.text = "Cargando..."
                        is UiState.Success -> renderChannelToolbar(state.data)
                        is UiState.Error -> {
                            binding.tvTitle.text = "Error"
                            Toast.makeText(this@ChatActivity, state.message, Toast.LENGTH_SHORT).show()
                        }
                        else -> Unit
                    }
                }
            }
        }
    }

    private fun renderDmToolbar(user: User) {
        binding.tvTitle.text = user.username
        if (user.avatarUrl.isNotEmpty()) {
            currentAvatarUrl = user.avatarUrl
            Glide.with(this)
                .load(user.avatarUrl)
                .placeholder(R.drawable.ic_user)
                .error(R.drawable.ic_user)
                .circleCrop()
                .into(binding.ivAvatar)
        }
    }

    private fun renderChannelToolbar(channel: Channel) {
        binding.tvTitle.text = channel.name
    }

    private fun setupClickListeners() {
        binding.ivArrowBack.setOnClickListener { finish() }

        binding.ivAvatar.setOnClickListener { showZoomedImage() }

        binding.tvTitle.setOnClickListener { openProfile() }
        binding.toolbarContainer.setOnClickListener { openProfile() }

        binding.btnSend.setOnClickListener {
            val messageText = binding.etMessage.text.toString().trim()
            if (messageText.isNotEmpty()) {
                binding.etMessage.setText("")
            }
        }

        binding.btnCamera.setOnClickListener { }
    }

    private fun openProfile() {
        when (chatType) {
            TYPE_DM -> {
                if (targetId.isNullOrEmpty()) return
                val intent = android.content.Intent(this, FriendProfile::class.java).apply {
                    putExtra("extra_user_id", targetId)
                }
                startActivity(intent)
            }
            TYPE_CHANNEL -> {
                if (serverId == -1L) return
                serverMemberNavigator.openServerMembers(this, serverId)
            }
        }
    }

    private fun showZoomedImage() {
        if (currentAvatarUrl.isNullOrEmpty()) {
            Toast.makeText(this, "No hay foto de perfil", Toast.LENGTH_SHORT).show()
            return
        }

        val dialog = Dialog(this, android.R.style.Theme_Black_NoTitleBar_Fullscreen)
        val imageView = AppCompatImageView(this).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            scaleType = android.widget.ImageView.ScaleType.FIT_CENTER
        }

        Glide.with(this)
            .load(currentAvatarUrl)
            .placeholder(R.drawable.ic_user)
            .error(R.drawable.ic_user)
            .into(imageView)

        dialog.setContentView(imageView)
        dialog.show()
        imageView.setOnClickListener { dialog.dismiss() }
    }

    private fun overrideActivityTransition() {
        overrideActivityTransition(OVERRIDE_TRANSITION_OPEN, R.anim.slide_in_right, R.anim.static_on)
        overrideActivityTransition(OVERRIDE_TRANSITION_CLOSE, R.anim.static_on, R.anim.slide_out_right)
    }

    private fun configInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        const val EXTRA_CHAT_TYPE = "extra_chat_type"
        const val EXTRA_CONVERSATION_ID = "extra_conversation_id"
        const val EXTRA_TARGET_ID = "extra_target_id"
        const val EXTRA_SERVER_ID = "extra_server_id"
        const val EXTRA_CHANNEL_ID = "extra_channel_id"

        const val TYPE_DM = "type_dm"
        const val TYPE_CHANNEL = "type_channel"
    }
}
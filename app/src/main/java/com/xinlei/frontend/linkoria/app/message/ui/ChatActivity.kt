package com.xinlei.frontend.linkoria.app.message.ui

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.xinlei.frontend.linkoria.app.R
import com.xinlei.frontend.linkoria.app.channel.domain.model.Channel
import com.xinlei.frontend.linkoria.app.message.ui.navigation.ChatArgs.Companion.TYPE_CHANNEL
import com.xinlei.frontend.linkoria.app.message.ui.navigation.ChatArgs.Companion.TYPE_DM
import com.xinlei.frontend.linkoria.app.core.ui.UiState
import com.xinlei.frontend.linkoria.app.core.ui.image.ImageLoader
import com.xinlei.frontend.linkoria.app.databinding.ActivityChatBinding
import com.xinlei.frontend.linkoria.app.message.ui.adapter.ChatMessageAdapter
import com.xinlei.frontend.linkoria.app.message.ui.navigation.ChatArgs
import com.xinlei.frontend.linkoria.app.message.ui.navigation.ChatNavigator
import com.xinlei.frontend.linkoria.app.user.domain.model.User
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ChatActivity : AppCompatActivity() {

    @Inject lateinit var chatNavigator: ChatNavigator
    @Inject lateinit var imageLoader: ImageLoader

    private lateinit var args: ChatArgs
    private var _binding: ActivityChatBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ChatViewModel by viewModels()

    private lateinit var chatAdapter: ChatMessageAdapter

    private var isLoadingMore = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)
        overrideActivityTransition()
        configInsets()

        args = ChatArgs.from(intent)
        viewModel.init(args)

        setupToolbarForChatType(args.chatType)
        setupRecyclerView()
        setupClickListeners()
        observeState()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    // region Setup

    private fun setupToolbarForChatType(chatType: String) {
        when (chatType) {
            TYPE_CHANNEL -> {
                binding.ivAvatar.visibility = View.GONE
                binding.icChannel.visibility = View.VISIBLE
            }
        }
    }

    private fun setupRecyclerView() {
        chatAdapter = ChatMessageAdapter(imageLoader)
        binding.rvMessages.apply {
            adapter = chatAdapter
            layoutManager = LinearLayoutManager(this@ChatActivity).also {
                it.stackFromEnd = true
            }
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    if (dy >= 0) return // solo scroll hacia arriba (dy negativo)
                    val lm = layoutManager as LinearLayoutManager
                    if (lm.findFirstVisibleItemPosition() <= 2 && !isLoadingMore) {
                        isLoadingMore = true
                        viewModel.loadMoreMessages()
                    }
                }
            })
        }
    }

    private fun setupClickListeners() {
        binding.ivArrowBack.setOnClickListener { finish() }
        binding.toolbarContainer.setOnClickListener {
            chatNavigator.openProfile(this, args.chatType, args.targetId, args.serverId)
        }
        binding.ivAvatar.setOnClickListener {
            val url = (viewModel.dmState.value as? UiState.Success)?.data?.avatarUrl
            if (!url.isNullOrEmpty()) {
                ZoomedImageDialogFragment.newInstance(url).show(supportFragmentManager, "zoomed_image")
            } else {
                Toast.makeText(this, "No hay foto de perfil", Toast.LENGTH_SHORT).show()
            }
        }
        binding.btnSend.setOnClickListener {
            val content = binding.etMessage.text?.toString()?.trim() ?: return@setOnClickListener
            if (content.isEmpty()) return@setOnClickListener
            viewModel.sendMessage(content)
            binding.etMessage.setText("")
        }
    }

    // region Observe

    private fun observeState() {
        when (args.chatType) {
            TYPE_DM -> observeDmState()
            TYPE_CHANNEL -> observeChannelState()
        }
        observeMessagesState()
        observeSendState()
    }

    private fun observeDmState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.dmState.collect { state ->
                    when (state) {
                        is UiState.Loading -> Unit // TODO: mostrar skeleton/shimmer
                        is UiState.Success -> state.data?.let { renderDmToolbar(it) }
                        is UiState.Error -> {
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
                        is UiState.Loading -> Unit // TODO: mostrar skeleton/shimmer
                        is UiState.Success -> renderChannelToolbar(state.data)
                        is UiState.Error -> {
                            Toast.makeText(this@ChatActivity, state.message, Toast.LENGTH_SHORT).show()
                        }
                        else -> Unit
                    }
                }
            }
        }
    }

    private fun observeMessagesState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.messagesState.collect { state ->
                    when (state) {
                        is UiState.Loading -> Unit // TODO: skeleton/shimmer
                        is UiState.Success -> {
                            chatAdapter.submitList(state.data) {
                                // scroll al final solo si estamos cerca del último mensaje
                                if (isNearBottom()) {
                                    binding.rvMessages.scrollToPosition(chatAdapter.itemCount - 1)
                                }
                            }
                        }
                        is UiState.Error -> Toast.makeText(this@ChatActivity, state.message, Toast.LENGTH_SHORT).show()
                        else -> Unit
                    }
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.isLoadingMore.collect { loading ->
                    isLoadingMore = loading
                }
            }
        }
    }

    private fun observeSendState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.sendState.collect { state ->
                    when (state) {
                        is UiState.Loading -> binding.btnSend.isEnabled = false
                        is UiState.Success -> binding.btnSend.isEnabled = true
                        is UiState.Error -> {
                            binding.btnSend.isEnabled = true
                            Toast.makeText(this@ChatActivity, state.message, Toast.LENGTH_SHORT).show()
                        }
                        else -> Unit
                    }
                }
            }
        }
    }

    // region Render

    private fun renderDmToolbar(user: User) {
        binding.tvTitle.text = user.username
        if (user.avatarUrl.isNotEmpty()) {
            imageLoader.loadIcon(binding.ivAvatar, user.avatarUrl)
        }
    }

    private fun renderChannelToolbar(channel: Channel) {
        binding.tvTitle.text = channel.name
    }

    // region Helpers

    private fun isNearBottom(): Boolean {
        val lm = binding.rvMessages.layoutManager as LinearLayoutManager
        val lastVisible = lm.findLastVisibleItemPosition()
        val total = chatAdapter.itemCount
        return total - lastVisible <= 3
    }

    // region Window

    private fun overrideActivityTransition() {
        overrideActivityTransition(OVERRIDE_TRANSITION_OPEN, R.anim.slide_in_right, R.anim.static_on)
        overrideActivityTransition(OVERRIDE_TRANSITION_CLOSE, R.anim.static_on, R.anim.slide_out_right)
    }

    private fun configInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->
            // Combinamos las barras del sistema con el IME (teclado)
            val types = WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.ime()
            val combinedInsets = insets.getInsets(types)

            // Aplicamos los paddings dinámicos
            view.setPadding(
                combinedInsets.left,
                combinedInsets.top,
                combinedInsets.right,
                combinedInsets.bottom // Esto aumentará cuando el teclado se abra
            )

            insets
        }
    }
}
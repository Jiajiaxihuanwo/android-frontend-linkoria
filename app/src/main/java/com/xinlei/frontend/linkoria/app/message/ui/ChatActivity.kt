package com.xinlei.frontend.linkoria.app.message.ui

import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
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
import com.xinlei.frontend.linkoria.app.typing.ui.adapter.TypingAdapter
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
    private lateinit var typingAdapter: TypingAdapter

    private var isLoadingMore = false

    private val pickImageLauncher = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        uri?.let { viewModel.sendImageMessage(it) }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)
        overrideActivityTransition()
        configInsets()

        args = ChatArgs.from(intent)
        viewModel.init(args)

        setupToolbarAndBackgroundForChatType(args.chatType)
        setupRecyclerView()
        setupClickListeners()
        observeState()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    // region Setup

    private fun setupToolbarAndBackgroundForChatType(chatType: String) {
        when (chatType) {
            TYPE_CHANNEL -> {
                binding.ivAvatar.visibility = View.GONE
                binding.icChannel.visibility = View.VISIBLE
                binding.chatBg.setImageResource(R.drawable.bg_chat_image)
            }
        }
    }

    private fun setupRecyclerView() {
        chatAdapter = ChatMessageAdapter(imageLoader) {
            message -> ZoomedImageDialogFragment.newInstance(message.content).show(supportFragmentManager, "zoomed_image")
        }
        binding.rvMessages.apply {
            adapter = chatAdapter
            layoutManager = LinearLayoutManager(this@ChatActivity).also {
                it.stackFromEnd = true
            }
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    chatAdapter.refreshBubbleColors(recyclerView)

                    if (dy >= 0) return // solo scroll hacia arriba (dy negativo)
                    val lm = layoutManager as LinearLayoutManager
                    if (lm.findFirstVisibleItemPosition() <= 2 && !isLoadingMore) {
                        isLoadingMore = true
                        viewModel.loadMoreMessages()
                    }
                }
            })
        }

        typingAdapter = TypingAdapter(imageLoader)
        binding.rvTyping.apply {
            adapter = typingAdapter
            layoutManager = LinearLayoutManager(this@ChatActivity)
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
            viewModel.onTypingStop()
            viewModel.sendMessage(content)
            binding.etMessage.setText("")
        }
        binding.etMessage.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
            override fun afterTextChanged(s: Editable?) = Unit
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.isNullOrEmpty()) viewModel.onTypingStop()
                else viewModel.onTypingStart()
            }
        })
        binding.btnGallery.setOnClickListener {
            pickImageLauncher.launch(
                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
            )
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
        observeTypingState()
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
                            hideShimmer()
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
                        is UiState.Loading -> {
                            binding.btnSend.isEnabled = false
                            binding.btnGallery.isEnabled = false
                        }
                        is UiState.Success -> {
                            binding.btnSend.isEnabled = true
                            binding.btnGallery.isEnabled = true
                        }
                        is UiState.Error -> {
                            binding.btnSend.isEnabled = true
                            binding.btnGallery.isEnabled = true
                            Toast.makeText(this@ChatActivity, state.message, Toast.LENGTH_SHORT).show()
                        }
                        else -> Unit
                    }
                }
            }
        }
    }

    private fun observeTypingState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.typingUsers.collect { users ->
                    typingAdapter.submitList(users.toList())
                    binding.rvTyping.visibility = if (users.isEmpty()) View.GONE else View.VISIBLE
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

    private fun hideShimmer() {
        binding.shimmerChat.root.visibility = View.GONE
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
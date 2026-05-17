package com.xinlei.frontend.linkoria.app.server.ui

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.PopupWindow
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.xinlei.frontend.linkoria.app.R
import com.xinlei.frontend.linkoria.app.core.ui.UiState
import com.xinlei.frontend.linkoria.app.core.ui.image.ImageLoader
import com.xinlei.frontend.linkoria.app.databinding.ActivityServerMemberBinding
import com.xinlei.frontend.linkoria.app.databinding.BottomSheetUserProfileBinding
import com.xinlei.frontend.linkoria.app.server.domain.model.ServerMember
import com.xinlei.frontend.linkoria.app.server.ui.adapter.server_member.ServerMemberAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject
import androidx.core.graphics.drawable.toDrawable
import com.xinlei.frontend.linkoria.app.databinding.FragmentProfileBinding
import com.xinlei.frontend.linkoria.app.databinding.LayoutPopupServerSettingBinding
import com.xinlei.frontend.linkoria.app.root.navigator.AppNavigator

@AndroidEntryPoint
class ServerMemberActivity : AppCompatActivity() {

    private var _binding: ActivityServerMemberBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ServerMemberViewModel by viewModels()

    @Inject
    lateinit var imageLoader: ImageLoader

    @Inject
    lateinit var appNavigator: AppNavigator

    private lateinit var adapter: ServerMemberAdapter

    private var serverId: Long = -1L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityServerMemberBinding.inflate(layoutInflater)
        setContentView(binding.root)

        configInsets()

        serverId = intent.getLongExtra(EXTRA_SERVER_ID, -1L)
        if (serverId != -1L) {
            viewModel.load(serverId)
        }

        setupRecyclerView()
        setupListeners()
        observeStates()
    }

    private fun setupRecyclerView() {
        adapter = ServerMemberAdapter(
            imageLoader = imageLoader,
            onMemberClick = { member -> viewModel.onMemberClick(member) },
            onMoreClick = null
        )
        binding.rvMembers.layoutManager = LinearLayoutManager(this)
        binding.rvMembers.adapter = adapter
    }

    private fun setupListeners() {
        binding.btnBack.setOnClickListener { finish() }

        binding.etFilter.doOnTextChanged { text, _, _, _ ->
            viewModel.onFilterQueryChanged(text?.toString() ?: "")
        }

        binding.ivCopyCode.setOnClickListener {
            val code = binding.tvServerCode.text.toString()
            if (code.isNotEmpty()) {
                val clipboard = getSystemService(CLIPBOARD_SERVICE) as android.content.ClipboardManager
                clipboard.setPrimaryClip(android.content.ClipData.newPlainText("invite_code", code))
                Toast.makeText(this, "Código copiado", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnSettings.setOnClickListener { showSettingsPopup() }
    }

    private fun observeStates() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch { observeServer() }
                launch { observeMembers() }
                launch { observeSelectedMember() }
                launch { observeFriendRequestState() }
                launch { observeLeaveServerState() }
                launch { observeDeleteServerState() }
            }
        }
    }

    private suspend fun observeServer() {
        viewModel.serverState.collect { state ->
            when (state) {
                is UiState.Success -> {
                    val server = state.data
                    binding.tvServerName.text = server.name
                    server.inviteCode?.let { binding.tvServerCode.text = it }
                }
                is UiState.Error -> Toast.makeText(this, state.message, Toast.LENGTH_SHORT).show()
                else -> Unit
            }
        }
    }

    private suspend fun observeMembers() {
        viewModel.membersState.collect { state ->
            when (state) {
                is UiState.Success -> {
                    adapter.submitList(state.data)
                    val totalMembers = state.data.filterIsInstance<com.xinlei.frontend.linkoria.app.server.ui.adapter.server_member.ServerMemberListItem.Member>().size
                    binding.tvMemberTotal.text = "$totalMembers miembros"
                }
                is UiState.Error -> Toast.makeText(this, state.message, Toast.LENGTH_SHORT).show()
                else -> Unit
            }
        }
    }

    private suspend fun observeSelectedMember() {
        viewModel.selectedMember.collect { member ->
            member?.let {
                ServerMemberProfileBottomSheet().show(supportFragmentManager, ServerMemberProfileBottomSheet.TAG)
            }
        }
    }

    private suspend fun observeFriendRequestState() {
        viewModel.friendRequestState.collect { state ->
            when (state) {
                is UiState.Success -> {
                    Toast.makeText(this, "Solicitud enviada", Toast.LENGTH_SHORT).show()
                    viewModel.clearFriendRequestState()
                }
                is UiState.Error -> {
                    Toast.makeText(this, state.message, Toast.LENGTH_SHORT).show()
                    viewModel.clearFriendRequestState()
                }
                else -> Unit
            }
        }
    }

    private suspend fun observeDeleteServerState() {
        viewModel.deleteServerState.collect { state ->
            when (state) {
                is UiState.Success -> {
                    appNavigator.navigateToDashboard(this)
                }
                is UiState.Error -> {
                    Toast.makeText(this, state.message, Toast.LENGTH_SHORT).show()
                }
                else -> Unit
            }
        }
    }

    private suspend fun observeLeaveServerState() {
        viewModel.leaveServerState.collect { state ->
            when (state) {
                is UiState.Success -> {
                    appNavigator.navigateToDashboard(this)
                }
                is UiState.Error -> {
                    Toast.makeText(this, state.message, Toast.LENGTH_SHORT).show()
                }
                else -> Unit
            }
        }
    }

    private fun configInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(systemBars.left, 0, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun showSettingsPopup() {
        val popupBinding = LayoutPopupServerSettingBinding.inflate(layoutInflater)

        val popup = PopupWindow(
            popupBinding.root,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            true
        )

        popup.elevation = 8f

        popupBinding.btnLeaveServer.setOnClickListener {
            viewModel.leaveServer(serverId)
            popup.dismiss()
        }

        popupBinding.btnDeleteServer.setOnClickListener {
            viewModel.deleteServer(serverId)
            popup.dismiss()
        }

        popup.showAsDropDown(binding.btnSettings, 0, 8)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        const val EXTRA_SERVER_ID = "extra_server_id"
    }
}
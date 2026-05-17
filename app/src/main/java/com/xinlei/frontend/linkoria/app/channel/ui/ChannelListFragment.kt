package com.xinlei.frontend.linkoria.app.channel.ui

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.xinlei.frontend.linkoria.app.channel.ui.adapter.ChannelAdapter
import com.xinlei.frontend.linkoria.app.core.ui.UiState
import com.xinlei.frontend.linkoria.app.databinding.BottomSheetCreateChannelBinding
import com.xinlei.frontend.linkoria.app.databinding.FragmentChannelListBinding
import com.xinlei.frontend.linkoria.app.root.navigator.ChatNavigator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ChannelListFragment : Fragment() {

    @Inject
    lateinit var chatNavigator: ChatNavigator

    private var _binding: FragmentChannelListBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: ChannelAdapter
    private val viewModel: ChannelListViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentChannelListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val serverId = arguments?.getLong("server_id") ?: -1
        val serverName = arguments?.getString("server_name") ?: ""

        binding.tvServerName.text = serverName
        setupRecyclerView()
        observeUiState()
        viewModel.loadChannels(serverId)

        setupListeners()
    }

    private fun setupRecyclerView() {
        adapter = ChannelAdapter { channel ->
            viewModel.onChannelClick(channel)
        }
        binding.channelRecyclerView.layoutManager = LinearLayoutManager(context)
        binding.channelRecyclerView.adapter = adapter
    }

    private fun observeUiState() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.channelsState.collect { state ->
                        when (state) {
                            is UiState.Success -> adapter.submitList(state.data)
                            is UiState.Error -> Toast.makeText(context, state.message, Toast.LENGTH_SHORT).show()
                            else -> Unit
                        }
                    }
                }
                launch {
                    viewModel.selectedChannel.collect { channel ->
                        channel?.let {
                            chatNavigator.openChannelChat(requireActivity(), it.serverId, it.id)
                            viewModel.clearSelectedChannel()
                        }
                    }
                }
            }
        }
    }

    private fun setupListeners() {
        binding.btnSearch.doOnTextChanged { text, _, _, _ ->
            viewModel.onFilterQueryChanged(text?.toString() ?: "")
        }

        binding.btnCreateChannel.setOnClickListener {
            showCreateChannelDialog()
        }
    }

    private fun showCreateChannelDialog() {
        val bottomSheet = BottomSheetDialog(requireContext()).apply {
            behavior.isFitToContents = true
            behavior.skipCollapsed = true
        }
        val sheetBinding = BottomSheetCreateChannelBinding.inflate(layoutInflater)
        bottomSheet.setContentView(sheetBinding.root)

        setupCreateChannelListeners(sheetBinding, bottomSheet)

        bottomSheet.show()

        // deslizar hacia arriba cuando aparece el teclado
        bottomSheet.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)

        sheetBinding.etChannelName.requestFocus()
    }

    private fun setupCreateChannelListeners(
        sheetBinding: BottomSheetCreateChannelBinding,
        bottomSheet: BottomSheetDialog
    ) {
        sheetBinding.btnCancel.setOnClickListener {
            bottomSheet.dismiss()
        }

        sheetBinding.btnCreate.setOnClickListener {
            val name = sheetBinding.etChannelName.text.toString().trim()
            if (name.isNotBlank()) {
                viewModel.createChannel(name)
                bottomSheet.dismiss()
            } else {
                sheetBinding.etChannelName.error = "Introduce un nombre"
            }
        }

        sheetBinding.etChannelName.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                sheetBinding.btnCreate.performClick()
                true
            } else false
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
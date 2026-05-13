package com.xinlei.frontend.linkoria.app.channel.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.xinlei.frontend.linkoria.app.channel.ui.adapter.ChannelAdapter
import com.xinlei.frontend.linkoria.app.core.ui.UiState
import com.xinlei.frontend.linkoria.app.databinding.FragmentChannelListBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ChannelListFragment : Fragment() {

    private var _binding: FragmentChannelListBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: ChannelAdapter
    private val viewModel: ChannelListViewModel by activityViewModels()
    private var currentServerId: Long = -1

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChannelListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupClickListeners()
        observeUiState()
        observeServerSelection()
    }

    private fun observeServerSelection() {
        (activity as? ServerSelectionListener)?.getSelectedServer()?.observe(viewLifecycleOwner) { server ->
            if (server != null && server.id != currentServerId) {
                currentServerId = server.id
                binding.tvServerName.text = server.name
                viewModel.loadChannels(server.id)
            }
        }
    }

    private fun observeUiState() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.channelsState.collect { state ->
                    when (state) {
                        is UiState.Loading -> Unit
                        is UiState.Success -> adapter.submitList(state.data)
                        is UiState.Error -> Toast.makeText(context, state.message, Toast.LENGTH_SHORT).show()
                        else -> Unit
                    }
                }
            }
        }
    }

    private fun setupRecyclerView() {
        adapter = ChannelAdapter { channel ->
            viewModel.selectChannel(channel.id)
            // 直接在这里处理频道点击，或者通过接口通知 Activity
            (activity as? OnChannelSelectedListener)?.onChannelSelected(channel)
        }
        binding.channelRecyclerView.layoutManager = LinearLayoutManager(context)
        binding.channelRecyclerView.adapter = adapter
    }

    private fun setupClickListeners() {
        binding.btnSearch.setOnClickListener {
            Toast.makeText(context, "Search channels", Toast.LENGTH_SHORT).show()
        }

        binding.btnInvite.setOnClickListener {
            Toast.makeText(context, "Invite members", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    interface ServerSelectionListener {
        fun getSelectedServer(): androidx.lifecycle.LiveData<com.xinlei.frontend.linkoria.app.server.domain.model.Server>
    }

    interface OnChannelSelectedListener {
        fun onChannelSelected(channel: com.xinlei.frontend.linkoria.app.channel.domain.model.Channel)
    }
}
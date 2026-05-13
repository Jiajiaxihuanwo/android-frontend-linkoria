package com.xinlei.frontend.linkoria.app.channel.ui

import android.app.AlertDialog
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

    private var _binding: FragmentChannelListBinding? = null//搞到xml
    private val binding get() = _binding!!//搞到东西
    private lateinit var adapter: ChannelAdapter
    private val viewModel: ChannelListViewModel by activityViewModels()

    private var currentServerId: Long = -1//变量

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChannelListBinding.inflate(inflater, container, false)//cargar xml
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val serverId = arguments?.getLong("server_id") ?: -1
        val serverName = arguments?.getString("server_name") ?: ""

        if (serverId != -1L) {
            binding.tvServerName.text = serverName
            currentServerId = serverId
            setupRecyclerView()
            observeUiState()
            viewModel.loadChannels(serverId)
        } else {
            binding.tvServerName.text = "Selecciona un servidor"
        }
        setupClickListeners()
    }

    private fun setupRecyclerView() {
        adapter = ChannelAdapter { channel ->
            Toast.makeText(context, "Abrir canal: ${channel.name}", Toast.LENGTH_SHORT).show()
        }
        binding.channelRecyclerView.layoutManager = LinearLayoutManager(context)
        binding.channelRecyclerView.adapter = adapter
    }

    private fun observeUiState() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.channelsState.collect { state ->
                    when (state) {
                        is UiState.Success -> {
                            adapter.submitList(state.data)
                        }
                        is UiState.Error -> {
                            Toast.makeText(context, state.message, Toast.LENGTH_SHORT).show()
                        }
                        else -> Unit
                    }
                }
            }
        }
    }

    private fun setupClickListeners() {
//        binding.btnSearch.setOnClickListener {
//
//        }
//
//        binding.btnInvite.setOnClickListener {
//
//        }

        binding.btnCreateChannel.setOnClickListener {
            showCreateChannelDialog()
        }
    }

    private fun showCreateChannelDialog() {
        val input = android.widget.EditText(requireContext())
        AlertDialog.Builder(requireContext())
            .setTitle("Crear canal")
            .setMessage("Introduce el nombre del canal")
            .setView(input)
            .setPositiveButton("Crear") { _, _ ->
                val channelName = input.text.toString()
                if (channelName.isNotBlank()) {
                    viewModel.createChannel(currentServerId, channelName)
                } else {
                    Toast.makeText(requireContext(), "Introduce el nombre del canal", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
package com.xinlei.frontend.linkoria.app.friendship.ui.received

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.xinlei.frontend.linkoria.app.core.ui.UiState
import com.xinlei.frontend.linkoria.app.core.ui.image.ImageLoader
import com.xinlei.frontend.linkoria.app.databinding.FragmentFriendReceivedBinding
import com.xinlei.frontend.linkoria.app.friendship.ui.received.adapter.FriendshipReceivedListAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class FriendReceivedFragment : Fragment() {

    private var _binding: FragmentFriendReceivedBinding? = null
    private val binding get() = _binding!!

    private val viewModel: FriendReceivedViewModel by viewModels()

    @Inject
    lateinit var imageLoader: ImageLoader

    private lateinit var friendshipReceivedListAdapter: FriendshipReceivedListAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFriendReceivedBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupAdapters()
        setupListeners()
        observeViewModel()
        viewModel.loadReceivedRequests()
    }

    private fun setupAdapters() {
        friendshipReceivedListAdapter = FriendshipReceivedListAdapter(
            imageLoader = imageLoader,
            onAcceptClickListener = { friendship ->
                viewModel.acceptFriendship(friendship)
            },
            onDeclineClickListener = { friendship ->
                viewModel.declineFriendship(friendship)
            }
        )

        binding.rvReceived.apply {
            adapter = friendshipReceivedListAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun setupListeners() {
        binding.etFilter.addTextChangedListener(object : android.text.TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.onFilterQueryChanged(s?.toString() ?: "")
            }
            override fun afterTextChanged(s: android.text.Editable?) = Unit
        })
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch { observeReceivedState() }
                launch { observeAcceptState() }
                launch { observeDeclineState() }
            }
        }
    }

    private suspend fun observeReceivedState() {
        viewModel.receivedState.collect { state ->
            when (state) {
                is UiState.Loading -> {
                    // TODO: Mostrar shimmer o loading
                }
                is UiState.Success -> {
                    friendshipReceivedListAdapter.submitList(state.data)
                    binding.tvCount.text = "${state.data.size} solicitudes"
                }
                is UiState.Error -> {
                    // TODO: Mostrar error
                }
                UiState.Idle -> Unit
            }
        }
    }

    private suspend fun observeAcceptState() {
        viewModel.acceptState.collect { state ->
            when (state) {
                is UiState.Error -> {
                    Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
                    viewModel.resetAcceptState()
                }
                is UiState.Success -> {
                    viewModel.resetAcceptState()
                }
                else -> Unit
            }
        }
    }

    private suspend fun observeDeclineState() {
        viewModel.declineState.collect { state ->
            when (state) {
                is UiState.Error -> {
                    Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
                    viewModel.resetDeclineState()
                }
                is UiState.Success -> {
                    viewModel.resetDeclineState()
                }
                else -> Unit
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
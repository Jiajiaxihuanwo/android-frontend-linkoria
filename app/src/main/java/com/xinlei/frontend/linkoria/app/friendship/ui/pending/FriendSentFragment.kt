package com.xinlei.frontend.linkoria.app.friendship.ui.pending

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.xinlei.frontend.linkoria.app.core.ui.UiState
import com.xinlei.frontend.linkoria.app.core.ui.image.ImageLoader
import com.xinlei.frontend.linkoria.app.databinding.FragmentFriendReceivedBinding
import com.xinlei.frontend.linkoria.app.databinding.FragmentFriendSentBinding
import com.xinlei.frontend.linkoria.app.friendship.ui.pending.adapter.FriendshipSentListAdapter
import com.xinlei.frontend.linkoria.app.friendship.ui.sent.FriendSentViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class FriendSentFragment : Fragment() {

    private var _binding: FragmentFriendSentBinding? = null
    private val binding get() = _binding!!

    private val viewModel: FriendSentViewModel by viewModels()

    @Inject
    lateinit var imageLoader: ImageLoader

    private lateinit var friendshipSentListAdapter: FriendshipSentListAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFriendSentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupAdapters()
        setupListeners()
        observeViewModel()
        viewModel.loadSentRequests()
    }

    private fun setupAdapters() {
        friendshipSentListAdapter = FriendshipSentListAdapter(
            imageLoader = imageLoader
        )

        binding.rvSent.apply {
            adapter = friendshipSentListAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun setupListeners() {
        binding.etFilter.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.onFilterQueryChanged(s?.toString() ?: "")
            }
            override fun afterTextChanged(s: Editable?) = Unit
        })
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch { observeSentState() }
            }
        }
    }

    private suspend fun observeSentState() {
        viewModel.sentState.collect { state ->
            when (state) {
                is UiState.Loading -> {
                    // TODO: Mostrar shimmer o loading
                }
                is UiState.Success -> {
                    friendshipSentListAdapter.submitList(state.data)
                    binding.tvCount.text = "${state.data.size} enviadas"
                }
                is UiState.Error -> {
                    // TODO: Mostrar error
                }
                UiState.Idle -> Unit
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
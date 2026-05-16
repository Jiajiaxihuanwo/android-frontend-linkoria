package com.xinlei.frontend.linkoria.app.conversation.ui.dm

import android.content.Intent
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
import com.xinlei.frontend.linkoria.app.conversation.ui.dm.adapter.DmListAdapter
import com.xinlei.frontend.linkoria.app.core.ui.UiState
import com.xinlei.frontend.linkoria.app.core.ui.image.ImageLoader
import com.xinlei.frontend.linkoria.app.databinding.FragmentDmListBinding
import com.xinlei.frontend.linkoria.app.friendship.ui.FriendsActivity
import com.xinlei.frontend.linkoria.app.root.navigator.AppNavigator
import com.xinlei.frontend.linkoria.app.user.ui.ProfileViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.getValue

@AndroidEntryPoint
class DMListFragment : Fragment() {

    @Inject
    lateinit var imageLoader: ImageLoader
    @Inject
    lateinit var navigator: AppNavigator

    private var _binding: FragmentDmListBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: DmListAdapter

    private val viewModel: DmListViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDmListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observeUiState()
        setupRecyclerView()
        setupListeners()

        viewModel.loadDms()
    }

    private fun setupListeners() {
        binding.btnAddFriend.setOnClickListener {
            //TODO: delegar a un navigator
            val intent = Intent(requireActivity(), FriendsActivity::class.java)
            startActivity(intent)
        }
    }

    private fun observeUiState() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.dmListState.collect { state ->
                    when (state) {
                        is UiState.Loading -> Unit
                        is UiState.Success -> adapter.submitList(state.data)
                        is UiState.Error -> Toast.makeText(context,state.message, Toast.LENGTH_SHORT)
                        else -> {}
                    }
                }
            }
        }
    }

    private fun setupRecyclerView() {
        adapter = DmListAdapter(imageLoader) {conversation -> navigator.openDm(requireActivity(), conversation.id, conversation.targetUsername!!, conversation.targetIconUrl)}
        binding.dmRecyclerView.layoutManager = LinearLayoutManager(context)
        binding.dmRecyclerView.adapter = adapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
package com.xinlei.frontend.linkoria.app.friendship.ui.list

import android.app.TaskStackBuilder
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.xinlei.frontend.linkoria.app.R
import com.xinlei.frontend.linkoria.app.core.ui.UiState
import com.xinlei.frontend.linkoria.app.core.ui.image.ImageLoader
import com.xinlei.frontend.linkoria.app.databinding.FragmentFriendListBinding
import com.xinlei.frontend.linkoria.app.databinding.LayoutPopupDeleteFriendBinding
import com.xinlei.frontend.linkoria.app.friendship.domain.model.Friendship
import com.xinlei.frontend.linkoria.app.friendship.ui.list.adapter.FriendshipAddListAdapter
import com.xinlei.frontend.linkoria.app.friendship.ui.list.adapter.FriendshipListAdapter
import com.xinlei.frontend.linkoria.app.friendship.ui.list.model.UserSearchItemUiModel
import com.xinlei.frontend.linkoria.app.root.MainActivity
import com.xinlei.frontend.linkoria.app.user.domain.model.User
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class FriendListFragment : Fragment() {

    private var _binding: FragmentFriendListBinding? = null
    private val binding get() = _binding!!

    private val viewModel: FriendListViewModel by viewModels()

    @Inject
    lateinit var imageLoader: ImageLoader

    private lateinit var friendshipListAdapter: FriendshipListAdapter
    private lateinit var friendshipAddListAdapter: FriendshipAddListAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFriendListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupAdapters()
        setupListeners()
        observeViewModel()
        viewModel.loadFriends()
    }

    private fun setupAdapters() {
        friendshipListAdapter = FriendshipListAdapter(
            imageLoader = imageLoader,
            onChatClickListener = { friendship ->
                viewModel.createDm(friendship.friendId)
            },
            onMoreClickListener = { friendship, view ->
                showMorePopup(view, friendship)
            }
        )

        friendshipAddListAdapter = FriendshipAddListAdapter(
            imageLoader = imageLoader,
            onAddClickListener = { userSearchItemUiModel ->
                viewModel.sendFriendshipRequest(userSearchItemUiModel.user.id)
            }
        )

        binding.rvFriends.apply {
            adapter = friendshipListAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }

        binding.rvSearchResults.apply {
            adapter = friendshipAddListAdapter
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

        binding.btnSearchUsers.setOnClickListener {
            val query = binding.etAddFriend.text?.toString() ?: ""
            viewModel.searchUsers(query)
        }

        binding.btnClearSearch.setOnClickListener {
            binding.etAddFriend.setText("")
            val query = binding.etAddFriend.text?.toString() ?: ""
            viewModel.searchUsers(query)
        }
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch { observeFriendsState() }
                launch { observeSearchState() }
                launch { observeSendRequestState() }
                launch { observeDeleteFriendState() }
            }
        }
    }

    private suspend fun observeFriendsState() {
        viewModel.friendsState.collect { state ->
            when (state) {
                is UiState.Loading -> {
                    // TODO: Mostrar shimmer o loading
                }
                is UiState.Success -> {
                    friendshipListAdapter.submitList(state.data)
                    binding.tvCount.text = "${state.data.size} amigos"
                }
                is UiState.Error -> {
                    // TODO: Mostrar error
                }
                UiState.Idle -> Unit
            }
        }
    }

    private suspend fun observeSearchState() {
        viewModel.searchState.collect { state ->
            when (state) {
                is UiState.Loading -> {
                    // TODO: Mostrar shimmer o loading en rv_search_results
                }
                is UiState.Success -> {
                    if (state.data.isEmpty()) {
                        Toast.makeText(requireContext(), "No se encontraron usuarios", Toast.LENGTH_SHORT).show()
                        binding.rvSearchResults.visibility = View.GONE
                        return@collect
                    }
                    val enrichedUsers = enrichUsersWithFriendshipStatus(state.data)
                    friendshipAddListAdapter.submitList(enrichedUsers)
                    binding.rvSearchResults.visibility = View.VISIBLE
                }
                is UiState.Error -> {
                    Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
                    binding.rvSearchResults.visibility = View.GONE
                }
                UiState.Idle -> {
                    binding.rvSearchResults.visibility = View.GONE
                }
            }
        }
    }

    private suspend fun observeSendRequestState() {
        viewModel.sendRequestState.collect { state ->
            when (state) {
                is UiState.Success -> {
                    // TODO: Actualizar visualmente el botón del item enviado
                    viewModel.resetSendRequestState()
                }
                is UiState.Error -> {
                    Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
                    viewModel.resetSendRequestState()
                }
                else -> Unit
            }
        }
    }

    private suspend fun observeCreateDmState() {
        viewModel.createDmState.collect { state ->
            when (state) {
                is UiState.Success -> {
                    //TODO redirigir la navegación
                }
                is UiState.Error -> {
                    Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
                    viewModel.resetSendRequestState()
                }
                else -> Unit
            }
        }
    }

    fun openChatFromDM(context: Context, chatIntent: Intent) {
        TaskStackBuilder.create(context).apply {
            addNextIntent(Intent(context, MainActivity::class.java))
            addNextIntent(chatIntent)
        }.startActivities()
    }

    private suspend fun observeDeleteFriendState() {

        viewModel.deleteFriendState.collect { state ->

            when (state) {

                is UiState.Success -> {

                    val deletedId = state.data

                    friendshipListAdapter.submitList(
                        friendshipListAdapter.currentList.filter {
                            it.friendId != deletedId
                        }
                    )

                    Toast.makeText(requireActivity(), "Has eliminado a un amigo", Toast.LENGTH_SHORT).show()

                    viewModel.resetDeleteFriendState()
                }

                is UiState.Error -> {

                    Toast.makeText(
                        requireContext(),
                        state.message,
                        Toast.LENGTH_SHORT
                    ).show()

                    viewModel.resetDeleteFriendState()
                }

                else -> Unit
            }
        }
    }

    /**
     * Cruza la lista de usuarios buscados con la lista de amigos del ViewModel
     * para determinar el estado del botón Add/Added en cada item.
     * PENDING o ACCEPTED → "Added" + deshabilitado
     * DECLINED, REMOVED o sin relación → "Add" + habilitado
     */
    private fun enrichUsersWithFriendshipStatus(users: List<User>): List<UserSearchItemUiModel> {

        val allFriendships = viewModel.allFriendships.value
        val blockedUserIds = allFriendships
            .filter { it.status == "PENDING" || it.status == "ACCEPTED" }
            .map { it.friendId }
            .toHashSet()

        val uiItems = users.map { user ->
            val isAvailable = user.id !in blockedUserIds

            UserSearchItemUiModel(
                user = user,
                isAvailable = isAvailable
            )
        }

        return uiItems
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun showMorePopup(
        anchorView: View,
        friendship: Friendship
    ) {
        val popupBinding = LayoutPopupDeleteFriendBinding.inflate(layoutInflater)

        val popupWindow = PopupWindow(
            popupBinding.root,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            true
        )

        popupWindow.elevation = 12f

        popupBinding.btnDelete.setOnClickListener {

            viewModel.deleteFriend(friendship.friendId)

            popupWindow.dismiss()
        }

        popupWindow.showAsDropDown(
            anchorView,
            -120,
            0
        )
    }
}
package com.xinlei.frontend.linkoria.app.user.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.xinlei.frontend.linkoria.app.core.ui.UiState
import com.xinlei.frontend.linkoria.app.core.ui.image.ImageLoader
import com.xinlei.frontend.linkoria.app.databinding.FragmentProfileBinding
import com.xinlei.frontend.linkoria.app.root.SplashActivity
import com.xinlei.frontend.linkoria.app.user.domain.model.User
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ProfileViewModel by activityViewModels()

    @Inject
    lateinit var imageLoader: ImageLoader

    private fun getRealViews() = with(binding) {
        listOf(ivAvatar, tvUsername, etDescription)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.loadProfile()
        observeUiState()
        setupClickListeners()
        setUpEditTextScroll()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setUpEditTextScroll() {
        binding.etDescription.setOnTouchListener { v, event ->
            if (v.canScrollVertically(1) || v.canScrollVertically(-1)) {
                v.parent.requestDisallowInterceptTouchEvent(true)
            }

            if (event.action == MotionEvent.ACTION_UP) {
                v.parent.requestDisallowInterceptTouchEvent(false)
                v.performClick()
            }

            false
        }
    }

    private fun setupClickListeners() {
        binding.btnLogout.setOnClickListener {
            viewModel.logout()
        }
        binding.btnEdit.setOnClickListener {
            EditProfileBottomSheet().show(childFragmentManager, "edit_profile")
        }
    }

    private fun observeUiState() {
        observeLogoutState()
        observeProfileState()
    }

    private fun observeLogoutState() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.logoutEvent.collect { event ->
                    when (event) {
                        true -> navigateToSplash()
                        else -> Unit
                    }
                }
            }
        }
    }

    private fun observeProfileState() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.userState.collect { state ->
                    when (state) {
                        is UiState.Loading -> showSkeleton()
                        is UiState.Success -> showUserData(state.data)
                        is UiState.Error -> Toast.makeText(context, state.message, Toast.LENGTH_LONG).show()
                        is UiState.Idle -> Unit
                    }
                }
            }
        }
    }

    private fun showSkeleton() {
        getRealViews().forEach { it.visibility = View.INVISIBLE }
        binding.shimmerContainer.visibility = View.VISIBLE
        binding.shimmerContainer.startShimmer()
    }

    private fun showUserData(user: User) {
        binding.shimmerContainer.stopShimmer()
        binding.shimmerContainer.visibility = View.GONE
        getRealViews().forEach { it.visibility = View.VISIBLE}

        binding.tvUsername.text = user.username

        imageLoader.loadIcon(
            view = binding.ivAvatar,
            url = user.avatarUrl
        )

        imageLoader.extractDominantColor(user.avatarUrl) {
            _binding?.ivBanner?.setBackgroundColor(it)
        }
    }

    private fun navigateToSplash() {
        val intent = Intent(requireActivity(), SplashActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
package com.xinlei.frontend.linkoria.app.server.ui

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.graphics.drawable.toDrawable
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.xinlei.frontend.linkoria.app.R
import com.xinlei.frontend.linkoria.app.core.ui.UiState
import com.xinlei.frontend.linkoria.app.core.ui.image.ImageLoader
import com.xinlei.frontend.linkoria.app.databinding.BottomSheetUserProfileBinding
import com.xinlei.frontend.linkoria.app.databinding.FragmentProfileBinding
import com.xinlei.frontend.linkoria.app.user.domain.model.User
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import javax.inject.Inject

@AndroidEntryPoint
class ServerMemberProfileBottomSheet : BottomSheetDialogFragment() {

    @Inject
    lateinit var imageLoader: ImageLoader

    private val viewModel: ServerMemberViewModel by activityViewModels()

    private var _binding: BottomSheetUserProfileBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = BottomSheetUserProfileBinding.inflate(inflater, container, false).also {
        _binding = it
    }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupBioScroll()
        observeStates()
        setupClickListeners()
    }

    override fun onStart() {
        super.onStart()
        val bottomSheet = dialog?.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
        bottomSheet?.background = Color.TRANSPARENT.toDrawable()
    }

    override fun getTheme() = R.style.TransparentBottomSheet
    @SuppressLint("ClickableViewAccessibility")
    private fun setupBioScroll() {
        binding.tvDescription.movementMethod = ScrollingMovementMethod()
        binding.tvDescription.setOnTouchListener { v, event ->
            val canScroll = v.canScrollVertically(1) || v.canScrollVertically(-1)
            when (event.action) {
                android.view.MotionEvent.ACTION_DOWN -> v.parent.requestDisallowInterceptTouchEvent(canScroll)
                android.view.MotionEvent.ACTION_UP -> {
                    v.parent.requestDisallowInterceptTouchEvent(false)
                    v.performClick()
                }
            }
            false
        }
    }

    private fun observeStates() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.selectedMemberUser.collect { state ->
                    when (state) {
                        is UiState.Loading -> showLoadingState()
                        is UiState.Success -> state.data?.let { renderUser(it) }
                        is UiState.Error -> Toast.makeText(context, state.message, Toast.LENGTH_SHORT).show()
                        else -> Unit
                    }
                }
            }
        }
    }

    private fun showLoadingState() {
        binding.tvUsername.text = ""
        binding.tvUserTag.text = ""
        binding.tvMemberSince.text = ""
        binding.tvDescription.text = ""
        binding.ivAvatar.setImageResource(R.drawable.ic_user)
    }

    private fun renderUser(user: User) {
        binding.tvUsername.text = user.username
        binding.tvUserTag.text = "@${user.username}"
        binding.tvMemberSince.text = getMemberSinceDate(user)
        binding.tvDescription.text = user.bio

        if (user.avatarUrl.isNotEmpty()) {
            imageLoader.loadIcon(binding.ivAvatar, user.avatarUrl)
            imageLoader.extractDominantColor(user.avatarUrl) {
                binding.ivBanner.setBackgroundColor(it)
            }
        } else {
            binding.ivAvatar.setImageResource(R.drawable.ic_user)
        }
    }

    private fun setupClickListeners() {
        binding.btnAddFriend.setOnClickListener {
            val member = viewModel.selectedMember.value ?: return@setOnClickListener
            viewModel.sendFriendRequest(member.userId)
            dismiss()
        }
    }

    private fun getMemberSinceDate(user: User): String {
        return try {
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS")
            val date = LocalDateTime.parse(user.createdAt, formatter)
            val output = DateTimeFormatter.ofPattern("d MMM yyyy", Locale("es"))
            date.format(output)
        } catch (e: Exception) {
            "Fecha desconocida"
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        viewModel.clearSelectedMember()
    }

    companion object {
        const val TAG = "ServerMemberProfileBottomSheet"
    }
}
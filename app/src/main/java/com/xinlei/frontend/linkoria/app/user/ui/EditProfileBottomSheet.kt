package com.xinlei.frontend.linkoria.app.user.ui

import android.annotation.SuppressLint
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.graphics.drawable.toDrawable
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.xinlei.frontend.linkoria.app.R
import com.xinlei.frontend.linkoria.app.core.ui.UiState
import com.xinlei.frontend.linkoria.app.core.ui.image.ImageLoader
import com.xinlei.frontend.linkoria.app.databinding.BottomSheetEditProfileBinding
import com.xinlei.frontend.linkoria.app.user.domain.model.User
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class EditProfileBottomSheet : BottomSheetDialogFragment() {

    @Inject
    lateinit var imageLoader: ImageLoader
    private val viewModel: ProfileViewModel by activityViewModels()
    private lateinit var binding: BottomSheetEditProfileBinding

    private var selectedAvatarUri: Uri? = null

    private val pickImageLauncher = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        uri?.let {
            selectedAvatarUri = it
            imageLoader.loadIconNoCache(binding.ivAvatar, selectedAvatarUri.toString())
            imageLoader.extractDominantColor(selectedAvatarUri.toString(),{binding.ivBanner.setBackgroundColor(it)})
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = BottomSheetEditProfileBinding.inflate(inflater, container, false).also {
            binding = it
        }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observeUiState()
        setUpClickListeners()
        setUpTextChangedListener()
        setUpEditTextScroll()

    }

    override fun onStart() {
        super.onStart()
        val bottomSheet = dialog?.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
        bottomSheet?.background = Color.TRANSPARENT.toDrawable()
    }

    override fun getTheme() = R.style.TransparentBottomSheet

    @SuppressLint("ClickableViewAccessibility")
    private fun setUpEditTextScroll() {
        binding.etBio.setOnTouchListener { v, event ->
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

    private fun setUpClickListeners() {
        binding.flAvatar.setOnClickListener {
            pickImageLauncher.launch(
                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
            )
        }

        binding.btnSave.setOnClickListener {
            viewModel.updateProfile(
                username = binding.etName.text.toString(),
                email = binding.etEmail.text.toString(),
                avatarUri = selectedAvatarUri
            )
        }
    }

    private fun setUpTextChangedListener() {
        binding.etName.addTextChangedListener(object : TextWatcher {

            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {}

            override fun onTextChanged(
                s: CharSequence?,
                start: Int,
                before: Int,
                count: Int
            ) {
                binding.tvUsername.text = s
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun observeUiState() {
        observeUserState()
        observeUpdateState()
    }

    private fun observeUpdateState() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.updateState.collect { state ->
                    when (state) {
                        is UiState.Loading -> Unit
                        is UiState.Success -> {
                            viewModel.resetUpdateState()
                            this@EditProfileBottomSheet.dismiss()
                        }
                        is UiState.Error -> Toast.makeText(context, state.message, Toast.LENGTH_LONG).show()
                        else -> Unit
                    }
                }
            }
        }
    }

    private fun observeUserState() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.userState.collect { state ->
                    when (state) {
                        is UiState.Success -> showUserData(state.data)
                        else -> Unit
                    }
                }
            }
        }
    }

    private fun showUserData(user: User) {
        imageLoader.loadIconNoCache(binding.ivAvatar, user.avatarUrl)
        imageLoader.extractDominantColor(user.avatarUrl){binding.ivBanner.setBackgroundColor(it)}

        binding.tvUsername.text = user.username
        binding.etName.setText(user.username)
        binding.etEmail.setText(user.email)
    }
}
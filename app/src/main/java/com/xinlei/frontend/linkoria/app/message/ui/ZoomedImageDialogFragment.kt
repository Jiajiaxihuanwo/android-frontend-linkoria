package com.xinlei.frontend.linkoria.app.message.ui

import android.R
import android.app.Dialog
import android.os.Bundle
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatImageView
import androidx.fragment.app.DialogFragment
import com.xinlei.frontend.linkoria.app.core.ui.image.ImageLoader
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ZoomedImageDialogFragment : DialogFragment() {

    @Inject
    lateinit var imageLoader: ImageLoader

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val url = arguments?.getString(ARG_URL)

        val imageView = AppCompatImageView(requireContext()).apply {
            layoutParams = ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT)
            scaleType = ImageView.ScaleType.FIT_CENTER
        }

        imageLoader.loadFit(imageView, url)

        return Dialog(requireContext(), R.style.Theme_Black_NoTitleBar_Fullscreen).apply {
            setContentView(imageView)
            imageView.setOnClickListener { dismiss() }
        }
    }

    companion object {
        private const val ARG_URL = "arg_url"

        fun newInstance(url: String) = ZoomedImageDialogFragment().apply {
            arguments = Bundle().apply { putString(ARG_URL, url) }
        }
    }
}
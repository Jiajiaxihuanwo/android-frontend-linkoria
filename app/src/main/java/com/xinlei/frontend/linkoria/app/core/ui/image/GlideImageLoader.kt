package com.xinlei.frontend.linkoria.app.core.ui.image

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.palette.graphics.Palette
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.xinlei.frontend.linkoria.app.R
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class GlideImageLoader @Inject constructor(
    @param:ApplicationContext private val context: Context
) : ImageLoader {

    override fun load(view: ImageView, url: String) {
        Glide.with(context)
            .load(url)
            .centerCrop()
            .into(view)
    }

    override fun loadIcon(view: ImageView, url: String) {
        Glide.with(context)
            .load(url)
            .circleCrop()
            .placeholder(R.drawable.ic_user)
            .into(view)
    }

    override fun extractDominantColor(url: String, onColorReady: (Int) -> Unit) {
        val fallbackColor = ContextCompat.getColor(context, R.color.bg_banner)

        Glide.with(context)
            .asBitmap()
            .load(url)
            .into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(bitmap: Bitmap, transition: Transition<in Bitmap>?) {
                    Palette.from(bitmap).generate { palette ->
                        val color = palette?.getVibrantColor(fallbackColor) ?: fallbackColor
                        onColorReady(color)
                    }
                }

                override fun onLoadCleared(placeholder: Drawable?) {}
            })
    }
}
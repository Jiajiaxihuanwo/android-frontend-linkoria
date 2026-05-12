package com.xinlei.frontend.linkoria.app.core.ui.image

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.palette.graphics.Palette
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.target.Target
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

    override fun extractDominantColor(url: String, onColorReady: (Int) -> Unit) {
        val fallbackColor = ContextCompat.getColor(context, R.color.bg_banner)

        Glide.with(context)
            .asBitmap()
            .load(url)
            .into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(bitmap: Bitmap, transition: Transition<in Bitmap>?) {
                    Palette.from(bitmap).generate { palette ->
                        val color = palette?.getVibrantColor(fallbackColor) ?: fallbackColor
                        val colorPastel = color.toPastel(.4f)

                        onColorReady(colorPastel)
                    }
                }

                override fun onLoadCleared(placeholder: Drawable?) {}
            })
    }

    override fun loadIconNoCache(view: ImageView, url: String?) {
        Glide.with(context)
            .load(url)
            .circleCrop()
            .placeholder(R.drawable.ic_user)
            .into(view)
    }

    private fun Int.toPastel(factor: Float = 0.5f): Int {
        val r = (Color.red(this) + (255 - Color.red(this)) * factor).toInt()
        val g = (Color.green(this) + (255 - Color.green(this)) * factor).toInt()
        val b = (Color.blue(this) + (255 - Color.blue(this)) * factor).toInt()
        return Color.rgb(r, g, b)
    }
}
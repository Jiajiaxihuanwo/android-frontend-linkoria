package com.xinlei.frontend.linkoria.app.core.ui.image

import android.content.Context
import android.widget.ImageView
import com.bumptech.glide.Glide
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
}
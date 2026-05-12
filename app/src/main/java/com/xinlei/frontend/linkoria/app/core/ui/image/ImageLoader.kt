package com.xinlei.frontend.linkoria.app.core.ui.image

import android.widget.ImageView

interface ImageLoader {
    fun load(view: ImageView, url: String)
    fun loadIconNoCache(view: ImageView, url: String)
    fun extractDominantColor(url: String, onColorReady: (Int) -> Unit)
}
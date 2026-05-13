package com.xinlei.frontend.linkoria.app.core.util

import android.view.MotionEvent
import android.view.View
import android.view.animation.AnimationUtils
import com.xinlei.frontend.linkoria.app.R

fun View.animateSidebarPress() {
    setOnTouchListener { v, event ->
        when (event.action) {
            MotionEvent.ACTION_DOWN -> startAnimation(AnimationUtils.loadAnimation(context, R.anim.press_down))
            MotionEvent.ACTION_UP -> {
                startAnimation(AnimationUtils.loadAnimation(context, R.anim.press_up))
                v.performClick()
            }
            MotionEvent.ACTION_CANCEL -> startAnimation(AnimationUtils.loadAnimation(context, R.anim.press_up))
        }
        true
    }
}
package com.xinlei.frontend.linkoria.app.core.util

import android.graphics.Color

object GradientColorProvider {

    fun getColorForPosition(fraction: Float, colors: IntArray): Int {
        if (colors.size == 1) return colors[0]

        val scaledFraction = fraction * (colors.size - 1)
        val index = scaledFraction.toInt().coerceIn(0, colors.size - 2)
        val localFraction = scaledFraction - index

        return blendColors(colors[index], colors[index + 1], localFraction)
    }

    private fun blendColors(from: Int, to: Int, fraction: Float): Int {
        val a = lerp(Color.alpha(from), Color.alpha(to), fraction)
        val r = lerp(Color.red(from), Color.red(to), fraction)
        val g = lerp(Color.green(from), Color.green(to), fraction)
        val b = lerp(Color.blue(from), Color.blue(to), fraction)
        return Color.argb(a, r, g, b)
    }

    private fun lerp(start: Int, end: Int, fraction: Float): Int {
        return (start + (end - start) * fraction).toInt()
    }
}
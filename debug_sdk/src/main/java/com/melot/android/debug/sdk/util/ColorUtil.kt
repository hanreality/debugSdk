package com.melot.android.debug.sdk.util

import android.graphics.Color
import androidx.annotation.ColorInt

/**
 * Author: han.chen
 * Time: 2021/12/9 14:15
 */
object ColorUtil {

    @JvmStatic
    fun parseColorInt(@ColorInt color: Int): String {
        return String.format("#%06X", 0xFFFFFF and color)
    }
    @JvmStatic
    fun isColdColor(@ColorInt color: Int): Boolean {
        val hsv = FloatArray(3)
        Color.colorToHSV(color, hsv)
        return hsv[2] <= 0.8f
    }
}
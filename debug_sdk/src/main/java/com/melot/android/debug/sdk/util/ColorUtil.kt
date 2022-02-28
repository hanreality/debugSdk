package com.melot.android.debug.sdk.util

import android.graphics.Color
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import com.melot.android.debug.sdk.MsKit
import com.melot.android.debug.sdk.R

/**
 * Author: han.chen
 * Time: 2021/12/9 14:15
 */
object ColorUtil {

    val colors:IntArray = intArrayOf(
        R.color.ms_color_1,
        R.color.ms_color_2,
        R.color.ms_color_3,
        R.color.ms_color_4,
        R.color.ms_color_5,
        R.color.ms_color_6,
        R.color.ms_color_7,
        R.color.ms_color_8,
        R.color.ms_color_9,
        R.color.ms_color_10
    )

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

    @JvmStatic
    @ColorInt
    fun getRandomColor() : Int {
        val index = (Math.random() * 9).toInt()
        return ContextCompat.getColor(MsKit.requireApp(), colors[index])
    }
}
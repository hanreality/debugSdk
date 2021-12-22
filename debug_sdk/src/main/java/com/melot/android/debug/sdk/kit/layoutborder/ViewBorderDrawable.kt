package com.melot.android.debug.sdk.kit.layoutborder

import android.graphics.*
import android.graphics.drawable.Drawable
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.ColorInt
import com.melot.android.debug.sdk.MsKit
import com.melot.android.debug.sdk.R
import com.melot.android.debug.sdk.config.LayoutBorderConfig
import com.melot.android.debug.sdk.util.ColorUtil

/**
 * Author: han.chen
 * Time: 2021/12/22 16:25
 */
class ViewBorderDrawable(view: View) : Drawable() {
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)

    private val rect: Rect = Rect()

    init {
        rect.set(0, 0, view.width, view.height)
        paint.style = Paint.Style.STROKE
        paint.color = MsKit.requireApp().resources.getColor(getBorderColor(view))
        paint.strokeWidth = 4f
        paint.pathEffect = DashPathEffect(floatArrayOf(4f, 4f), 0f)
    }

    private fun getBorderColor(view: View): Int {
        return when (view) {
            is ViewGroup -> {
                R.color.ms_color_1
            }
            is Button -> {
                R.color.ms_color_6
            }
            is TextView -> {
                R.color.ms_color_9
            }
            else -> R.color.ms_color_10
        }
    }

    override fun draw(canvas: Canvas) {
        if (LayoutBorderConfig.sLayoutBorderOpen) {
            canvas.drawRect(rect, paint)
        }
    }

    override fun setAlpha(alpha: Int) {

    }

    override fun setColorFilter(colorFilter: ColorFilter?) {

    }

    override fun getOpacity(): Int {
        return PixelFormat.TRANSLUCENT
    }
}
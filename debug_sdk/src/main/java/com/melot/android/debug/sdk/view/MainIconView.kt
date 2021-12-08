package com.melot.android.debug.sdk.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Path
import android.graphics.RectF
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.view.*
import androidx.appcompat.widget.AppCompatImageView
import kotlin.math.pow


/**
 * Author: han.chen
 * Time: 2021/9/9 15:09
 */
class MainIconView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatImageView(context, attrs, defStyleAttr) {


    private val drawPath = Path()
    private val rectF = RectF()

    override fun onDraw(canvas: Canvas?) {
        try {
            rectF.set(0f, 0f, width.toFloat(), height.toFloat())
            drawPath.addRoundRect(rectF, width / 2f, height / 2f, Path.Direction.CW)
            canvas?.clipPath(drawPath)
            super.onDraw(canvas)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
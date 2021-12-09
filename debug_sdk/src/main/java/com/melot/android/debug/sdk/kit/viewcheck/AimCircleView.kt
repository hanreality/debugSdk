package com.melot.android.debug.sdk.kit.viewcheck

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import com.melot.android.debug.sdk.R
import com.melot.android.debug.sdk.util.DevelopUtil

/**
 * Author: han.chen
 * Time: 2021/12/8 19:23
 */
class AimCircleView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private var mPaint: Paint = Paint()

    init {
        mPaint.isAntiAlias = true
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        drawCircle(canvas)
    }

    private fun drawCircle(canvas: Canvas?) {
        val cx = (width / 2).toFloat()
        val cy = (width / 2).toFloat()
        var radius = (width / 2).toFloat()

        mPaint.style = Paint.Style.FILL
        mPaint.color = resources.getColor(R.color.ms_color_FFFFFF)
        mPaint.alpha = 100
        canvas?.drawCircle(cx, cy, radius, mPaint)

        radius = (DevelopUtil.dp2px(40f) / 2).toFloat()
        mPaint.color = resources.getColor(R.color.ms_color_30CC3A4B)
        mPaint.alpha = 50
        canvas?.drawCircle(cx, cy, radius, mPaint)

        radius = (DevelopUtil.dp2px(5f) / 2).toFloat()
        mPaint.color = resources.getColor(R.color.ms_color_CC3A4B)
        canvas?.drawCircle(cx, cy, radius, mPaint)

        radius = (width / 2).toFloat()
        mPaint.style = Paint.Style.STROKE
        mPaint.strokeWidth = 4f
        mPaint.color = resources.getColor(R.color.ms_color_337CC4)
        mPaint.alpha = 100
        canvas?.drawCircle(cx, cy, radius - 2, mPaint)
    }
}
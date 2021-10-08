package com.melot.android.debug.sdk.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Path
import android.graphics.RectF
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.view.MotionEvent
import android.view.WindowManager
import androidx.appcompat.widget.AppCompatImageView


import kotlin.math.abs


/**
 * Author: han.chen
 * Time: 2021/9/9 15:09
 */
class DebugMenuButton @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatImageView(context, attrs, defStyleAttr) {
    private var lastX: Int = 0
    private var lastY: Int = 0
    private var beginX: Int = 0
    private var beginY: Int = 0

    private var screenWidth: Int = 1080
    private var screenHeight: Int = 1920

    private val drawPath = Path()
    private val rectF = RectF()

    init {
        val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val dm = DisplayMetrics()
        wm.defaultDisplay.getMetrics(dm)
        screenWidth = dm.widthPixels
        screenHeight = dm.heightPixels
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                lastX = event.rawX.toInt()
                lastY = event.rawY.toInt()
                beginX = lastX
                beginY = lastY
            }
            MotionEvent.ACTION_MOVE -> {
                val dx = event.rawX.toInt() - lastX
                val dy = event.rawY.toInt() - lastY

                var left = left + dx
                var top = top + dy
                var right = right + dx
                var bottom = bottom + dy
                if (left < 0) {
                    left = 0
                    right = left + width
                }
                if (right > screenWidth) {
                    right = screenWidth
                    left = right - width
                }
                if (top < 0) {
                    top = 0;
                    bottom = top + height
                }
                if (bottom > screenHeight) {
                    bottom = screenHeight
                    top = bottom - height
                }
                layout(left, top, right, bottom)
                lastX = event.rawX.toInt()
                lastY = event.rawY.toInt()
            }
            MotionEvent.ACTION_UP -> {
                return if (abs(lastX - beginX) < 10 && abs(lastY - beginY) < 10) {
                    super.onTouchEvent(event)
                } else {
                    isPressed = false
                    true
                }
            }
        }
        return super.onTouchEvent(event)
    }

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
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
class DebugMenuButton @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatImageView(context, attrs, defStyleAttr) {


    private val drawPath = Path()
    private val rectF = RectF()
    private var mLastX: Float = 0.toFloat()
    private var mLastY: Float = 0.toFloat()
    private var mIsDrag: Boolean? = null
    private var mTouchSlop: Int = 0

    init {
        mTouchSlop = ViewConfiguration.get(context).scaledTouchSlop shl 1
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                mIsDrag = null
                mLastX = event.rawX
                mLastY = event.rawY
            }
            MotionEvent.ACTION_MOVE -> {
                val curX = event.rawX
                val curY = event.rawY
                if (mIsDrag == null) {
                    if ((curX - mLastX).toDouble().pow(2.0) + Math.pow(
                            (curY - mLastY).toDouble(),
                            2.0
                        ) > mTouchSlop
                    ) {
                        mIsDrag = true
                        mLastX = curX
                        mLastY = curY
                    }
                } else if (mIsDrag!!) {
                    val params =
                        layoutParams as? ViewGroup.MarginLayoutParams ?: return super.onTouchEvent(
                            event
                        )
                    params.leftMargin += (curX - mLastX).toInt()
                    params.topMargin += (curY - mLastY).toInt()
                    requestLayout()
                    mLastX = curX
                    mLastY = curY
                }
            }
            MotionEvent.ACTION_CANCEL, MotionEvent.ACTION_UP -> {
                if (mIsDrag == null) {
                    performClick()
                }
                moveIfNeeded()
            }
        }

        return true
    }

    private fun moveIfNeeded() {
        val params = layoutParams as? ViewGroup.MarginLayoutParams ?: return
        if (params.leftMargin < 0) {
            params.leftMargin = 0
        }
        if (params.topMargin < dp2px(30)) {
            params.topMargin = dp2px(30)
        }
        if (params.leftMargin + width > (parent as View).width) {
            params.leftMargin = (parent as View).width - width
        }
        if (params.topMargin + height > (parent as View).height) {
            params.topMargin = (parent as View).height - height
        }
        requestLayout()
    }


    private fun dp2px(dip: Int): Int {
        val density = context.resources.displayMetrics.density
        return (dip * density + 0.5).toInt()
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
package com.melot.android.debug.sdk.kit.viewcheck

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.DashPathEffect
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import com.melot.android.debug.sdk.R
import com.melot.android.debug.sdk.model.ViewInfo
import java.util.ArrayList

/**
 * Author: han.chen
 * Time: 2021/12/8 19:29
 */
class LayoutBorderView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var mRectPaint = Paint()

    private val mViewInfoList: ArrayList<ViewInfo> = ArrayList()

    init {
        val a = getContext().obtainStyledAttributes(attrs, R.styleable.LayoutBorderView)
        val fill = a.getBoolean(R.styleable.LayoutBorderView_msFill, false)
        if (fill) {
            mRectPaint.style = Paint.Style.FILL
            mRectPaint.color = Color.RED
        } else {
            mRectPaint.style = Paint.Style.STROKE
            mRectPaint.strokeWidth = 4f
            mRectPaint.pathEffect = DashPathEffect(floatArrayOf(4f, 4f), 0f)
            mRectPaint.color = Color.RED
        }
        a.recycle()
    }

    override fun onDraw(canvas: Canvas?) {
        mViewInfoList.forEach {
            if (mRectPaint.style == Paint.Style.FILL) {
                mRectPaint.alpha = it.getDrawTimeLevel() * 255
            }
            canvas?.drawRect(it.viewRect, mRectPaint)
        }
    }

    fun showViewLayoutBorder(info: ViewInfo?) {
        mViewInfoList.clear()
        if (info != null) {
            mViewInfoList.add(info)
        }
        invalidate()
    }

    fun showViewLayoutBorder(viewInfoList: List<ViewInfo>?) {
        if (viewInfoList == null) {
            return
        }
        mViewInfoList.clear()
        mViewInfoList.addAll(viewInfoList)
        invalidate()
    }
}
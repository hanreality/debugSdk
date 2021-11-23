package com.melot.android.debug.sdk.view.layer

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.text.*
import android.text.style.BackgroundColorSpan
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.PopupWindow
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import com.melot.android.debug.sdk.util.DevelopUtil

/**
 * Author: han.chen
 * Time: 2021/11/18 10:10
 */
class PopableNameLayer(context: Context) : AbsLayer(context) {

    private var mStartLayer: Int = 0
    private var mEndLayer: Int = 0
    private var mActivity: FragmentActivity? = null
    private var mBagPaint = Paint()
    private var mPaint: Paint
    private var mRadius: Int = 0
    private var targetClazz: Class<*>?

    init {
        mBagPaint.color = Color.argb(63, 255, 0, 255)
        mPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mPaint.textSize = dp2px(10f).toFloat()
        mPaint.color = Color.RED
        mPaint.strokeWidth = dp2px(1f).toFloat()
        mRadius = dp2px(8f)
        targetClazz = try {
            Class.forName("android.widget.PopupWindow.PopupDecorView")
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    override fun onAttached(rootView: View?) {
        super.onAttached(rootView)
        val activity = DevelopUtil.findActivity(rootView)
        if (activity is FragmentActivity) {
            mActivity = activity
        }
    }

    override fun onDetached(rootView: View?) {
        super.onDetached(rootView)
        mActivity = null
    }

    override fun onUiUpdate(canvas: Canvas, rootView: View?) {
        super.onUiUpdate(canvas, rootView)
        mActivity?.let {
            if (!it.isFinishing) {
                mStartLayer = 0
                mEndLayer = 2
                traversal(canvas, it.window.decorView as? ViewGroup, 0)
            }
        }
    }

    private fun traversal(canvas: Canvas, parent: ViewGroup?, currentLayer: Int) {
        parent?.let {
            for (i in 0 until it.childCount) {
                val child = parent.getChildAt(i);
                if (child is ViewGroup) {
                    traversal(canvas, child, currentLayer)
                } else if (child::class.java == targetClazz) {
                    println(child)
                }
            }
        }
    }

    private fun drawInfo(canvas: Canvas, x: Int, y: Int, w: Int, h: Int, text: String) {
        canvas.save()
        drawCorner(canvas, x, y, w, h)
        drawX(canvas, x, y, w, h)
        drawName(canvas, x, y, w, h, text)
        canvas.restore()
    }


    private fun drawName(canvas: Canvas, x: Int, y: Int, w: Int, h: Int, txt: String) {
        val textPaint = TextPaint(mPaint)
        textPaint.textSize = dp2px(13f).toFloat()
        canvas.translate(x.toFloat(), y.toFloat())
        val decorStr = SpannableString(txt)
        decorStr.setSpan(
            BackgroundColorSpan(-0x66000001),
            0,
            txt.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        val staticLayout = StaticLayout(
            decorStr, textPaint,
            (w * 0.8).toInt(), Layout.Alignment.ALIGN_CENTER, 1f, 0f, false
        )
        val txtW = staticLayout.width
        val txtH = staticLayout.height
        if (txtH > 0 && txtH > h * 0.8) {
            val scale = h * 0.8f / txtH
            val realW = (txtW * scale).toInt()
            val realH = (txtH * scale).toInt()
            canvas.translate(((w - realW) / 2).toFloat(), ((h - realH) / 2).toFloat())
            canvas.scale(scale, scale)
        } else {
            canvas.translate(((w - txtW) / 2).toFloat(), ((h - txtH) / 2).toFloat())
        }
        staticLayout.draw(canvas)
    }

    private fun drawX(canvas: Canvas, x: Int, y: Int, w: Int, h: Int) {
        canvas.drawLine(x.toFloat(), y.toFloat(), (x + w).toFloat(), (y + h).toFloat(), mPaint)
        canvas.drawLine((x + w).toFloat(), y.toFloat(), x.toFloat(), (y + h).toFloat(), mPaint)
    }

    private fun drawCorner(canvas: Canvas, x: Int, y: Int, w: Int, h: Int) {
        canvas.drawLine(x.toFloat(), y.toFloat(), (x + mRadius).toFloat(), y.toFloat(), mPaint)
        canvas.drawLine(x.toFloat(), y.toFloat(), x.toFloat(), (y + mRadius).toFloat(), mPaint)
        canvas.drawLine(
            (x + w).toFloat(), y.toFloat(), (x + w - mRadius).toFloat(), y.toFloat(),
            mPaint
        )
        canvas.drawLine(
            (x + w).toFloat(), y.toFloat(), (x + w).toFloat(), (y + mRadius).toFloat(),
            mPaint
        )
        canvas.drawLine(
            x.toFloat(), (y + h).toFloat(), (x + mRadius).toFloat(), (y + h).toFloat(),
            mPaint
        )
        canvas.drawLine(
            x.toFloat(), (y + h).toFloat(), x.toFloat(), (y + h - mRadius).toFloat(),
            mPaint
        )
        canvas.drawLine(
            (x + w).toFloat(), (y + h).toFloat(), (x + w - mRadius).toFloat(), (y + h).toFloat(),
            mPaint
        )
        canvas.drawLine(
            (x + w).toFloat(), (y + h).toFloat(), (x + w).toFloat(), (y + h - mRadius).toFloat(),
            mPaint
        )
    }
}
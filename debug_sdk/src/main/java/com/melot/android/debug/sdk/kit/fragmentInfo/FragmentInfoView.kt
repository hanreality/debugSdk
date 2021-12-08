package com.melot.android.debug.sdk.kit.fragmentInfo

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.widget.FrameLayout
import com.melot.android.debug.sdk.util.BitmapCreator
import com.melot.android.debug.sdk.view.layer.FragmentNameLayer

/**
 * Author: han.chen
 * Time: 2021/12/8 12:22
 */
class FragmentInfoView constructor(
    context: Context
) : FrameLayout(context) {
    private var fragmentNameLayer: FragmentNameLayer? = null
    private val canvas = Canvas()
    private var bitmap: Bitmap? = null

    init {
        fragmentNameLayer = FragmentNameLayer(context)
    }

    private fun drawInfo(canvas: Canvas) {
        clear()
        fragmentNameLayer?.uiUpdate(canvas, rootView)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        setWillNotDraw(false)
        viewTreeObserver.addOnPreDrawListener {
            drawInfo(canvas)
            true
        }
    }

    private fun clear() {
        bitmap?.let {
            if (!it.isRecycled) {
                it.eraseColor(0)
            }
        }
    }

    override fun draw(canvas: Canvas) {
        bitmap?.let {
            if (!it.isRecycled) {
                canvas.drawBitmap(it, 0f, 0f, null)
            }
        }
        super.draw(canvas)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        if (bitmap != null && bitmap!!.width >= w && bitmap!!.height >= h) {
            return
        }
        bitmap = BitmapCreator.create(w, h, Bitmap.Config.ARGB_8888)
        canvas.setBitmap(bitmap)
    }

    fun generateSwitch(isChecked: Boolean) {
        if (isChecked) {
            addView(
                fragmentNameLayer,
                fragmentNameLayer?.getLayoutParams(generateDefaultLayoutParams())
            )
            return
        }
        removeView(fragmentNameLayer)
    }
}
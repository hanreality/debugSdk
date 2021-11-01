package com.melot.android.debug.sdk.view

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.view.View
import android.widget.CompoundButton
import android.widget.FrameLayout
import com.melot.android.debug.sdk.R
import com.melot.android.debug.sdk.util.BitmapCreator
import com.melot.android.debug.sdk.view.layer.AbsLayer
import com.melot.android.debug.sdk.view.layer.FragmentNameLayer

/**
 * Author: han.chen
 * Time: 2021/9/9 16:18
 */
class DebugView constructor(
    context: Context
) : FrameLayout(context) {

    private val debugMenuButton: DebugMenuButton
    private val debugActionView: DebugActionView
    private var fragmentNameLayer: FragmentNameLayer? = null
    private val canvas = Canvas()
    private var bitmap: Bitmap? = null

    init {
        View.inflate(context, R.layout.layout_debug_view, this)

        fragmentNameLayer = FragmentNameLayer(context)

        debugMenuButton = findViewById(R.id.iv_debug_button)
        debugMenuButton.setOnClickListener {
            showMenu()
        }
        debugActionView = findViewById(R.id.debug_action_view)
        debugActionView.switchFragmentCallBack =
            CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->
                generateSwitch(fragmentNameLayer, isChecked)
            }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        setWillNotDraw(false)
        viewTreeObserver.addOnPreDrawListener {
            drawInfo(canvas)
            true
        }
    }
    private fun drawInfo(canvas: Canvas) {
        clear()
        fragmentNameLayer?.uiUpdate(canvas, rootView)
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

    private fun showMenu() {
        if (debugActionView.visibility == View.VISIBLE) {
            debugActionView.dismiss()
        } else {
            debugActionView.show()
        }
    }

    private fun generateSwitch(view: AbsLayer?, isChecked: Boolean) {
        if (isChecked) {
            addView(
                view,
                view?.getLayoutParams(generateDefaultLayoutParams())
            )
            return
        }
        removeView(view)
    }
}
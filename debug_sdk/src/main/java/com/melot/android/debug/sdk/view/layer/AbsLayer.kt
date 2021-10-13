package com.melot.android.debug.sdk.view.layer

import android.content.Context
import android.graphics.Canvas
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.FrameLayout

/**
 * Author: han.chen
 * Time: 2021/10/12 10:04
 */
abstract class AbsLayer(context: Context) : FrameLayout(context) {

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        onAttached(rootView)
    }
    protected open fun onAttached(rootView: View?) {

    }

    override fun onDetachedFromWindow() {
        onDetached(rootView)
        super.onDetachedFromWindow()
    }

    protected open fun onDetached(rootView: View?) {

    }

    fun dp2px(length: Float): Int {
        val scale = resources.displayMetrics.density
        return (length * scale + 0.5f).toInt()
    }

    protected open fun showWindow(view: View?, params: WindowManager.LayoutParams?) {
        val manager = rootView.context
            .getSystemService(Context.WINDOW_SERVICE) as WindowManager
        manager.addView(view, params)
    }

    protected open fun updateWindow(view: View?, params: WindowManager.LayoutParams?) {
        val manager = rootView.context
            .getSystemService(Context.WINDOW_SERVICE) as WindowManager
        manager.updateViewLayout(view, params)
    }

    protected open fun removeWindow(view: View?) {
        val manager = rootView.context
            .getSystemService(Context.WINDOW_SERVICE) as WindowManager
        manager.removeViewImmediate(view)
    }

    protected open fun getLocationAndSize(view: View): IntArray? {
        val locations = IntArray(2)
        view.getLocationInWindow(locations)
        val decorView = view.rootView // dialog 内边距
        return intArrayOf(
            locations[0] - decorView.paddingLeft,
            locations[1] - decorView.paddingTop,
            view.width,
            view.height
        )
    }

    open fun getLayoutParams(params: LayoutParams?): ViewGroup.LayoutParams? {
        return params
    }

    fun uiUpdate(canvas: Canvas, view: View?) {
        val count = canvas.save()
        onUiUpdate(canvas, view)
        canvas.restoreToCount(count)
    }

    protected open fun onUiUpdate(canvas: Canvas, rootView: View?) {}

}
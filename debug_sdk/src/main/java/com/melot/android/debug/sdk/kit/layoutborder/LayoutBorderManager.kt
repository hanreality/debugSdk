package com.melot.android.debug.sdk.kit.layoutborder

import android.app.Activity
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.melot.android.debug.sdk.core.UniversalActivity
import com.melot.android.debug.sdk.util.ActivityUtils
import com.melot.android.debug.sdk.util.LifecycleListenerUtil

/**
 * Author: han.chen
 * Time: 2021/12/22 16:41
 */
class LayoutBorderManager {
    companion object{
        val instance by lazy(LazyThreadSafetyMode.SYNCHRONIZED){
            LayoutBorderManager()
        }
    }

    var isRunning: Boolean = false
    private var viewBorderFrameLayout: ViewBorderFrameLayout? = null
    private val lifecycleListener = object : LifecycleListenerUtil.LifecycleListener {
        override fun onActivityResumed(activity: Activity?) {
            resolveActivity(activity)
        }

        override fun onActivityPaused(activity: Activity?) {

        }

        override fun onFragmentAttached(f: Fragment?) {

        }

        override fun onFragmentDetached(f: Fragment?) {

        }
    }

    private fun resolveActivity(activity: Activity?) {
        if (activity is UniversalActivity) {
            return
        }
        val window = activity?.window ?: return
        val root = window.decorView as? ViewGroup ?: return
        viewBorderFrameLayout = ViewBorderFrameLayout(root.context)
        while (root.childCount != 0) {
            val child = root.getChildAt(0)
            if (child is ViewBorderFrameLayout) {
                viewBorderFrameLayout = child
                viewBorderFrameLayout?.requestLayout()
                return
            }
            root.removeView(child)
            viewBorderFrameLayout?.addView(child)
        }
        root.addView(viewBorderFrameLayout)
    }

    fun start() {
        isRunning = true
        resolveActivity(ActivityUtils.getTopActivity())
        LifecycleListenerUtil.registerListener(lifecycleListener)
    }

    fun stop() {
        isRunning = false
        viewBorderFrameLayout?.requestLayout()
        viewBorderFrameLayout = null
        LifecycleListenerUtil.unregisterListener(lifecycleListener)
    }
}
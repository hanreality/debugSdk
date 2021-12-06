package com.melot.android.debug.sdk.util

import android.app.Activity
import androidx.fragment.app.Fragment

/**
 * Author: han.chen
 * Time: 2021/12/3 17:38
 */
object LifecycleListenerUtil {

    @JvmStatic
    val LIFECYCLE_LISTENERS = ArrayList<LifecycleListener>()

    @JvmStatic
    fun registerListener(listener: LifecycleListener) {
        LIFECYCLE_LISTENERS.add(listener)
    }

    @JvmStatic
    fun unregisterListener(listener: LifecycleListener) {
        LIFECYCLE_LISTENERS.remove(listener)
    }

    interface LifecycleListener {
        fun onActivityResumed(activity: Activity?)

        fun onActivityPaused(activity: Activity?)

        fun onFragmentAttached(f: Fragment?)

        fun onFragmentDetached(f: Fragment?)
    }
}
package com.melot.android.debug.sdk

import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import com.melot.android.debug.sdk.proxy.IDebugProxy
import com.melot.android.debug.sdk.view.DebugMenuButton
import com.melot.android.debug.sdk.view.DebugView

/**
 * Author: han.chen
 * Time: 2021/9/9 15:53
 */
class DebugManager {

    private val debugViewTag = "com.melot.android.debugview"
    private var debugView: DebugView? = null
    var enable: Boolean = false // debug开关
    private lateinit var application: Application
    var currentActivity: Activity? = null
    var debugProxy: IDebugProxy? = null

    companion object {
        val INSTANCE: DebugManager by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            DebugManager()
        }
    }

    fun registerApplication(application: Application) {
        this.application = application
        addDebugView()
    }

    fun registerDebugProxy(debugProxy: IDebugProxy) {
        this.debugProxy = debugProxy
    }

    private fun checkApplication() {
        require(this::application.isInitialized) {
            "please call registerApplication first"
        }
    }


    fun addDebugView() {
        checkApplication()
        application.registerActivityLifecycleCallbacks(lifecycleCallbacks)
    }

    fun removeDebugView() {
        checkApplication()
        application.unregisterActivityLifecycleCallbacks(lifecycleCallbacks)
        debugView?.let {
            val decorView = currentActivity?.window?.decorView as? ViewGroup
            decorView?.let {
                if (it.findViewWithTag<View?>(debugViewTag) != null) {
                    it.removeView(debugView)
                }
            }
        }
    }

    private val lifecycleCallbacks = object : Application.ActivityLifecycleCallbacks {
        override fun onActivityCreated(activity: Activity, bundle: Bundle?) {
            currentActivity = activity
        }

        override fun onActivityStarted(activity: Activity) {
            currentActivity = activity
        }

        override fun onActivityResumed(activity: Activity) {
            currentActivity = activity
            takeIf { debugView == null }?.apply {
                debugView = DebugView(activity)
                debugView?.tag = debugViewTag
            }
            try {
                val decorView = activity.window?.decorView as? ViewGroup
                decorView?.let {
                    if (it.findViewWithTag<View?>(debugViewTag) == null) {
                        it.addView(debugView)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        override fun onActivityPaused(activity: Activity) {
            debugView?.let {
                val decorView = activity.window?.decorView as? ViewGroup
                decorView?.let {
                    if (it.findViewWithTag<View?>(debugViewTag) != null) {
                        it.removeView(debugView)
                    }
                }
            }
        }

        override fun onActivityStopped(activity: Activity) {
        }

        override fun onActivitySaveInstanceState(activity: Activity, p1: Bundle) {
        }

        override fun onActivityDestroyed(activity: Activity) {
        }

    }
}
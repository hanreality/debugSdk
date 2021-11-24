package com.melot.android.debug.sdk

import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import com.melot.android.debug.sdk.proxy.IDebugProxy
import com.melot.android.debug.sdk.view.DebugView
import java.lang.ref.WeakReference

/**
 * Author: han.chen
 * Time: 2021/9/9 15:53
 */
class DebugManager {

    private val debugViewTag = "com.melot.android.debugview"
    private var debugView: DebugView? = null
    var enable: Boolean = false // debug开关
        set(value) {
            field = value
            if (value) {
                addDebugView()
            } else {
                removeDebugView()
            }
        }
    private lateinit var application: Application
    var currentActivity: WeakReference<Activity>? = null
    var debugProxy: IDebugProxy? = null

    companion object {
        val INSTANCE: DebugManager by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            DebugManager()
        }
    }

    fun registerApplication(application: Application) {
        this.application = application
    }

    fun registerDebugProxy(debugProxy: IDebugProxy) {
        this.debugProxy = debugProxy
    }

    private fun checkApplication() {
        require(this::application.isInitialized) {
            "please call registerApplication first"
        }
    }

    private fun attachDebugView(activity: Activity?) {
        if (!enable) {
            return
        }
        takeIf { debugView == null && activity != null}?.apply {
            debugView = DebugView(activity!!.applicationContext)
            debugView?.tag = debugViewTag
        }
        try {
            val decorView = activity?.window?.decorView as? ViewGroup
            decorView?.let { _decorView ->
                if (_decorView.findViewWithTag<View?>(debugViewTag) == null) {
                    debugView?.let {
                        _decorView.addView(it)
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun detachDebugView(activity: Activity?) {
        debugView?.let {
            val decorView = activity?.window?.decorView as? ViewGroup
            decorView?.let {
                if (it.findViewWithTag<View?>(debugViewTag) != null) {
                    it.removeView(debugView)
                }
            }
        }
    }

    private fun addDebugView() {
        checkApplication()
        application.registerActivityLifecycleCallbacks(lifecycleCallbacks)
        attachDebugView(currentActivity?.get())
    }

    private fun removeDebugView() {
        checkApplication()
        application.unregisterActivityLifecycleCallbacks(lifecycleCallbacks)
        detachDebugView(currentActivity?.get())
    }

    private val lifecycleCallbacks = object : Application.ActivityLifecycleCallbacks {
        override fun onActivityCreated(activity: Activity, bundle: Bundle?) {

        }

        override fun onActivityStarted(activity: Activity) {

        }

        override fun onActivityResumed(activity: Activity) {
            currentActivity = WeakReference(activity)
            attachDebugView(activity)
        }

        override fun onActivityPaused(activity: Activity) {
            detachDebugView(activity)
            currentActivity = null
        }

        override fun onActivityStopped(activity: Activity) {
        }

        override fun onActivitySaveInstanceState(activity: Activity, p1: Bundle) {
        }

        override fun onActivityDestroyed(activity: Activity) {
        }

    }
}
package com.melot.android.debug.sdk

import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import com.melot.android.debug.sdk.core.ActivityLifecycleStatusInfo
import com.melot.android.debug.sdk.core.MsKitLifeCycleStatus
import com.melot.android.debug.sdk.core.MsKitManager
import com.melot.android.debug.sdk.core.MsKitViewManager
import com.melot.android.debug.sdk.util.ActivityUtils
import com.melot.android.debug.sdk.util.LifecycleListenerUtil
import com.melot.android.debug.sdk.util.MsKitPermissionUtil

/**
 * Author: han.chen
 * Time: 2021/12/3 17:32
 */
class MsKitActivityLifecycleCallbacks : Application.ActivityLifecycleCallbacks {
    private var startedActivityCounts = 0
    private var sHasRequestPermission = false
    private val sFragmentLifecycleCallbacks: FragmentManager.FragmentLifecycleCallbacks =
        MsKitFragmentLifecycleCallbacks()


    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        try {
            recordActivityLifeCycleStatus(activity, MsKitLifeCycleStatus.CREATED)
            if (ignoreCurrentActivityMsKitView(activity)) {
                return
            }
            ActivityUtils.onActivityCreated(activity, savedInstanceState)
            if (activity is FragmentActivity) {
                activity.supportFragmentManager.registerFragmentLifecycleCallbacks(
                    sFragmentLifecycleCallbacks, true
                )
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onActivityStarted(activity: Activity) {
        try {
            if (ignoreCurrentActivityMsKitView(activity)) {
                return
            }
            ActivityUtils.onActivityStarted(activity)
            if (startedActivityCounts == 0) {
                MsKitViewManager.INSTANCE.notifyForeground()
            }
            startedActivityCounts++
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onActivityResumed(activity: Activity) {

        try {
            recordActivityLifeCycleStatus(activity, MsKitLifeCycleStatus.RESUME)
            //如果是leakCanary页面不进行添加
            if (ignoreCurrentActivityMsKitView(activity)) {
                return
            }
            ActivityUtils.onActivityResumed(activity)
            dispatchOnActivityResumed(activity)
            for (listener in LifecycleListenerUtil.LIFECYCLE_LISTENERS) {
                listener.onActivityResumed(activity)
            }
        } catch (e: Exception) {
        }
    }

    override fun onActivityPaused(activity: Activity) {
        try {
            if (ignoreCurrentActivityMsKitView(activity)) {
                return
            }
            ActivityUtils.onActivityPaused(activity)
            for (listener in LifecycleListenerUtil.LIFECYCLE_LISTENERS) {
                listener.onActivityPaused(activity)
            }
            MsKitViewManager.INSTANCE.onActivityPaused(activity)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onActivityStopped(activity: Activity) {
        try {
            recordActivityLifeCycleStatus(activity, MsKitLifeCycleStatus.STOPPED)
            if (ignoreCurrentActivityMsKitView(activity)) {
                return
            }
            ActivityUtils.onActivityStopped(activity)
            startedActivityCounts--
            if (startedActivityCounts == 0) {
                MsKitViewManager.INSTANCE.notifyBackground()
            }
            MsKitViewManager.INSTANCE.onActivityStopped(activity)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
        try {
            if (ignoreCurrentActivityMsKitView(activity)) {
                return
            }
            ActivityUtils.onActivitySaveInstanceState(activity, outState)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onActivityDestroyed(activity: Activity) {
        try {
            recordActivityLifeCycleStatus(activity, MsKitLifeCycleStatus.DESTROYED)
            if (ignoreCurrentActivityMsKitView(activity)) {
                return
            }
            ActivityUtils.onActivityDestroyed(activity)
            //注销fragment的生命周期回调
            if (activity is FragmentActivity) {
                activity.supportFragmentManager.unregisterFragmentLifecycleCallbacks(
                    sFragmentLifecycleCallbacks
                )
            }
            MsKitViewManager.INSTANCE.onActivityDestroyed(activity)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * 显示所有应该显示的dokitView
     *
     * @param activity
     */
    private fun dispatchOnActivityResumed(activity: Activity) {
        activity.window.decorView.also {
            it.post { MsKit.windowSize.set(it.width, it.height) }
        }
        if (MsKitManager.IS_NORMAL_FLOAT_MODE) {
            //显示内置msKitView icon
            MsKitViewManager.INSTANCE.dispatchOnActivityResumed(activity)
            return
        }
        //悬浮窗权限 vivo 华为可以不需要动态权限 小米需要
        if (MsKitPermissionUtil.canDrawOverlays(activity)) {
            MsKitViewManager.INSTANCE.dispatchOnActivityResumed(activity)
        } else {
            //请求悬浮窗权限
            requestPermission(activity)
        }
    }

    /**
     * 请求悬浮窗权限
     *
     * @param context
     */
    private fun requestPermission(context: Context) {
        if (!MsKitPermissionUtil.canDrawOverlays(context) && !sHasRequestPermission) {
            Toast.makeText(
                context,
                context.getText(R.string.ms_float_permission_toast),
                Toast.LENGTH_SHORT
            ).show()
            //请求悬浮窗权限
            MsKitPermissionUtil.requestDrawOverlays(context)
            sHasRequestPermission = true
        }
    }

    /**
     * 记录当前Activity的生命周期状态
     */
    private fun recordActivityLifeCycleStatus(
        activity: Activity,
        lifeCycleStatus: MsKitLifeCycleStatus
    ) {
        var activityLifeCycleStatusInfo: ActivityLifecycleStatusInfo? =
            MsKitManager.ACTIVITY_LIFECYCLE_INFO_MAP[activity.javaClass.canonicalName]
        if (activityLifeCycleStatusInfo == null) {
            activityLifeCycleStatusInfo = ActivityLifecycleStatusInfo(
                isInvokeStopMethod = false,
                lifeCycleStatus = MsKitLifeCycleStatus.CREATED,
                activityName = activity.javaClass.canonicalName
            )
            MsKitManager.ACTIVITY_LIFECYCLE_INFO_MAP[activity.javaClass.canonicalName!!] =
                activityLifeCycleStatusInfo
        }
        when (lifeCycleStatus) {
            MsKitLifeCycleStatus.CREATED -> {
                activityLifeCycleStatusInfo.lifeCycleStatus =
                    MsKitLifeCycleStatus.CREATED
            }
            MsKitLifeCycleStatus.RESUME -> {
                activityLifeCycleStatusInfo.lifeCycleStatus =
                    MsKitLifeCycleStatus.RESUME
            }
            MsKitLifeCycleStatus.STOPPED -> {
                activityLifeCycleStatusInfo.lifeCycleStatus =
                    MsKitLifeCycleStatus.STOPPED
                activityLifeCycleStatusInfo.isInvokeStopMethod = true
            }
            MsKitLifeCycleStatus.DESTROYED -> {
                MsKitManager.ACTIVITY_LIFECYCLE_INFO_MAP.remove(activity.javaClass.canonicalName)
            }
        }
    }

    companion object {
        private const val TAG = "ActivityLifecycleCallback"

        /**
         * 是否忽略在当前的activity上显示浮标
         *
         * @param activity
         * @return
         */
        private fun ignoreCurrentActivityMsKitView(activity: Activity): Boolean {
            val ignoreActivityClassNames = arrayOf("LeakActivity")
            for (activityClassName in ignoreActivityClassNames) {
                if (activity.javaClass.simpleName == activityClassName) {
                    return true
                }
            }
            return false
        }
    }
}
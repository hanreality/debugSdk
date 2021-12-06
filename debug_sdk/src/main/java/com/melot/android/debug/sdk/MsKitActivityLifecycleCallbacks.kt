package com.melot.android.debug.sdk

import android.app.Activity
import android.app.Application
import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import com.melot.android.debug.sdk.core.ActivityLifecycleStatusInfo
import com.melot.android.debug.sdk.core.MsKitLifeCycleStatus
import com.melot.android.debug.sdk.core.MsKitManager

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
            if (startedActivityCounts == 0) {
                DokitViewManager.INSTANCE.notifyForeground()
            }
            startedActivityCounts++
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onActivityResumed(activity: Activity) {
        recordActivityLifeCycleStatus(activity, MsKitLifeCycleStatus.RESUME)
    }

    override fun onActivityPaused(activity: Activity) {

    }

    override fun onActivityStopped(activity: Activity) {
        recordActivityLifeCycleStatus(activity, MsKitLifeCycleStatus.STOPPED)
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {

    }

    override fun onActivityDestroyed(activity: Activity) {
        recordActivityLifeCycleStatus(activity, MsKitLifeCycleStatus.DESTROYED)
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
            return false
        }
    }
}
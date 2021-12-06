package com.melot.android.debug.sdk.util

import android.app.Activity

/**
 * Author: han.chen
 * Time: 2021/12/3 17:24
 */
object MsKitSystemUtil {

    fun isMainLaunchActivity(activity: Activity?) :Boolean {
        val packageManager = activity?.application?.packageManager
        val intent = packageManager?.getLaunchIntentForPackage(activity.packageName) ?: return false
        val launchComponentName = intent.component
        val componentName = activity.componentName
        if (launchComponentName != null && componentName.toString() == launchComponentName.toString()) {
            return true
        }
        return false
    }

    fun isOnlyFirstLaunchActivity(activity: Activity?) {
        val isMainActivity = isMainLaunchActivity(activity)
    }
}

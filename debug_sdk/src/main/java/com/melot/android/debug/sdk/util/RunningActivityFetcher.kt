package com.melot.android.debug.sdk.util

import android.app.Activity
import java.lang.ref.WeakReference
import java.lang.reflect.Field

/**
 * Author: han.chen
 * Time: 2021/10/12 10:36
 */
object RunningActivityFetcher {
    private var mActivities: Map<*, *>? = null
    private var activityField: Field? = null

    fun fetch(): ArrayList<WeakReference<Activity>>? {
        takeIf { mActivities == null }?.run {
            getActivities()
        }
        val references: ArrayList<WeakReference<Activity>> = ArrayList()
        mActivities?.values?.forEach { activityRecord ->
            if (activityField == null) {
                getActivityField(activityRecord)
            }
            takeIf { activityField != null }?.let {
                try {
                    val activity = activityField?.get(activityRecord) as Activity
                    references.add(WeakReference(activity))
                } catch (throwable: Throwable) {
                    throwable.printStackTrace()
                    return null
                }
            }
        }
        return references
    }

    private fun getActivityField(activityRecord: Any?) {
        try {
            activityField = activityRecord?.javaClass?.getDeclaredField("activity")
            activityField?.isAccessible = true
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun getActivities() {
        try {
            val activityThreadClazz = Class.forName("android.app.ActivityThread")
            val currentActivityThreadMethod =
                activityThreadClazz.getDeclaredMethod("currentActivityThread")
            currentActivityThreadMethod.isAccessible = true
            val activityThread = currentActivityThreadMethod.invoke(null)
            val activitiesField = activityThreadClazz.getDeclaredField("mActivities")
            activitiesField.isAccessible = true
            mActivities = activitiesField.get(activityThread) as? Map<*, *>
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
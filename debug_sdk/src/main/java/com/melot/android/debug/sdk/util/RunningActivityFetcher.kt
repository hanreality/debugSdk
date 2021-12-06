package com.melot.android.debug.sdk.util

import android.app.Activity
import android.app.Application
import android.os.Build
import android.os.Bundle
import android.view.View
import java.lang.ref.WeakReference
import java.lang.reflect.Field
import java.util.*
import kotlin.collections.ArrayList

/**
 * Author: han.chen
 * Time: 2021/10/12 10:36
 */
object RunningActivityFetcher {
    private var mActivities: Map<*, *>? = null
    private var activityField: Field? = null

    fun getTopActivity(): Activity? {
        val references: ArrayList<WeakReference<Activity>>? = fetch()
        references?.forEach {
            if (isActivityAlive(it.get())) {
                return it.get()
            }
        }
        return null
    }

    fun findActivity(view: View?): Activity? {
        val references: ArrayList<WeakReference<Activity>>? = fetch()

        references?.forEach {
            val activity = it.get()
            if (view == activity?.window?.decorView?.rootView) {
                return activity
            }
        }
        return null
    }

    private fun fetch(): ArrayList<WeakReference<Activity>>? {
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

    private fun getActivities() {
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

    fun isActivityAlive(activity: Activity?): Boolean {
        return activity != null && !activity.isFinishing && !activity.isDestroyed
    }
}
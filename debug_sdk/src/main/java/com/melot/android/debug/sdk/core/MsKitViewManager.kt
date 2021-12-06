package com.melot.android.debug.sdk.core

import android.app.Activity
import android.content.Context
import androidx.collection.ArrayMap
import com.melot.android.debug.sdk.MsKit

/**
 * Author: han.chen
 * Time: 2021/12/3 17:04
 */
internal class MsKitViewManager : MsKitViewManagerInterface {

    companion object {
        private const val MC_DELAY = 100
    }

    private val context: Context get() = MsKit.requireApp()
    /**
     * 每个Activity中MsKitView的集合 用户手动移除和页面销毁时都需要remove
     *
     */
    private val activityMsKitViewMap: MutableMap<Activity, MutableMap<String, AbsMsKitView>> =
        ArrayMap<Activity, MutableMap<String, AbsMsKitView>>()

    /**
     * 只用来记录全局的同步  只有用户手动移除时才会remove
     */
    private val globalSingleDoKitViewInfoMap: MutableMap<String, GlobalSingleMsKitViewInfo> = ArrayMap<String, GlobalSingleMsKitViewInfo>()

    override fun attach(msKitIntent: MsKitIntent) {
        try {
            val currentActivityMsKitViews: MutableMap<String, AbsMsKitView> = when {

            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun detach(msKitView: AbsMsKitView) {
    }

    override fun detach(tag: String) {
    }

    override fun detach(msKitViewClass: Class<out AbsMsKitView>) {
    }

    override fun detachAll() {
    }

    override fun <T : AbsMsKitView> getMsKitView(
        activity: Activity?,
        clazz: Class<T>
    ): AbsMsKitView? {
        return null
    }

    override fun getMsKitViews(activity: Activity?): Map<String, AbsMsKitView>? {
        return null
    }

    override fun notifyBackground() {
        activityMsKitViewMap.forEach{ maps->
            maps.value.forEach {
                it.value.onEnterBackground()
            }
        }
    }

    override fun notifyForeground() {
        activityMsKitViewMap.forEach{ maps->
            maps.value.forEach {
                it.value.onEnterForeground()
            }
        }
    }

    override fun onActivityDestroyed(activity: Activity?) {

    }

    override fun onActivityPaused(activity: Activity?) {

    }

    override fun onActivityStopped(activity: Activity?) {

    }

    override fun dispatchOnActivityResumed(activity: Activity?) {
        if (activity == null) {
            return
        }


    }
}
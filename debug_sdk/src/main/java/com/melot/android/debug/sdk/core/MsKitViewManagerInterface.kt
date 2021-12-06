package com.melot.android.debug.sdk.core

import android.app.Activity

/**
 * Author: han.chen
 * Time: 2021/12/3 17:05
 */
interface MsKitViewManagerInterface {

    fun attach(msKitIntent: MsKitIntent)

    fun detach(msKitView: AbsMsKitView)
    fun detach(tag: String)
    fun detach(msKitViewClass: Class<out AbsMsKitView>)
    fun detachAll()

    fun <T : AbsMsKitView> getMsKitView(activity: Activity?, clazz: Class<T>): AbsMsKitView?
    fun getMsKitViews(activity: Activity?): Map<String, AbsMsKitView>?

    /**
     * 当app进入后台时调用
     */
    fun notifyBackground()

    /**
     * 当app进入前台时调用
     */
    fun notifyForeground()

    /**
     * Activity销毁时调用
     *
     * @param activity
     */
    fun onActivityDestroyed(activity: Activity?)

    /**
     * 页面onPause时调用
     *
     * @param activity
     */
    fun onActivityPaused(activity: Activity?)

    /**
     * 页面onStop时调用
     */
    fun onActivityStopped(activity: Activity?)

    /**
     * Called on [Activity] resumed from activity lifecycle callbacks
     *
     * @param activity resumed activity
     */
    fun dispatchOnActivityResumed(activity: Activity?)
}
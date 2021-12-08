package com.melot.android.debug.sdk.core

import android.app.Activity

/**
 * Author: han.chen
 * Time: 2021/12/6 10:33
 */
abstract class AbsMsKitViewManager : MsKitViewManagerInterface {

    /**
     * 添加主icon
     */
    abstract fun attachMainIcon(activity: Activity?)

    /**
     * 移除主icon
     */
    abstract fun detachMainIcon()

    /**
     * 添加toolPanel
     */
    abstract fun attachToolPanel(activity: Activity?)
    /**
     * 移除toolPanel
     */
    abstract fun detachToolPanel()

    /**
     * main activity 创建时回调
     *
     * @param activity
     */
    abstract fun onMainActivityResume(activity: Activity?)

    /**
     * Activity 创建时回调
     *
     * @param activity
     */
    abstract fun onActivityResume(activity: Activity?)

    /**
     * Activity 页面回退的时候回调
     *
     * @param activity
     */
    abstract fun onActivityBackResume(activity: Activity?)
}
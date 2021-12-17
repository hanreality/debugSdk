package com.melot.android.debug.sdk.core

import android.app.Activity
import android.content.Context
import android.text.TextUtils
import com.melot.android.debug.sdk.MsKit
import com.melot.android.debug.sdk.extension.tagName
import com.melot.android.debug.sdk.kit.fragmentInfo.ViewFragmentDrawMsKitView
import com.melot.android.debug.sdk.kit.toolpanel.ToolPanelMsKitView
import com.melot.android.debug.sdk.main.MainIconMsKitView
import com.melot.android.debug.sdk.util.MsKitSystemUtil

/**
 * Author: han.chen
 * Time: 2021/12/7 10:33
 */
internal class SystemMsKitViewManager : AbsMsKitViewManager() {

    private val context: Context get() = MsKit.requireApp()

    private val windowManager = MsKitViewManager.INSTANCE.windowManager
    private val msKitViews: MutableList<AbsMsKitView> by lazy { mutableListOf<AbsMsKitView>() }
    private val listeners: MutableList<MsKitViewManager.MsKitViewAttachedListener> by lazy { mutableListOf<MsKitViewManager.MsKitViewAttachedListener>() }

    override fun getMsKitViews(activity: Activity?): Map<String, AbsMsKitView> {
        val msKitViewMap: MutableMap<String, AbsMsKitView> = mutableMapOf()
        for (msKitView in msKitViews) {
            msKitViewMap[msKitView.tag] = msKitView
        }
        return msKitViewMap
    }

    override fun notifyBackground() {
        for (msKitView in msKitViews) {
            msKitView.onEnterBackground()
        }
    }

    override fun notifyForeground() {
        for (msKitView in msKitViews) {
            msKitView.onEnterForeground()
        }
    }

    override fun dispatchOnActivityResumed(activity: Activity?) {
        if (activity == null) {
            return
        }
        if (MsKitSystemUtil.isOnlyFirstLaunchActivity(activity)) {
            onMainActivityResume(activity)
        }

        MsKitManager.ACTIVITY_LIFECYCLE_INFO_MAP[activity.javaClass.canonicalName]?.let {
            if (it.lifeCycleStatus == MsKitLifeCycleStatus.RESUME && !it.isInvokeStopMethod) {
                onActivityResume(activity)
                return
            }

            //activity resume
            if (it.lifeCycleStatus == MsKitLifeCycleStatus.RESUME && it.isInvokeStopMethod) {
                onActivityBackResume(activity)
            }
        }

        //需要手动调用 生命周期回调
        val msKitViewMap = getMsKitViews(activity)
        for (absMsKitView in msKitViewMap.values) {
            absMsKitView.onResume()
        }
    }

    override fun attachMainIcon(activity: Activity?) {
        if (activity == null) {
            return
        }
        if (MsKitManager.ALWAYS_SHOW_MAIN_ICON && activity !is UniversalActivity) {
            attach(MsKitIntent(MainIconMsKitView::class.java))
            MsKitManager.MAIN_ICON_HAS_SHOW = true
        } else {
            MsKitManager.MAIN_ICON_HAS_SHOW = false
        }
    }

    override fun detachMainIcon() {
        detach(MainIconMsKitView::class.tagName)
    }

    override fun attachToolPanel(activity: Activity?) {
        attach(MsKitIntent(ToolPanelMsKitView::class.java))
    }

    override fun detachToolPanel() {
        detach(ToolPanelMsKitView::class.tagName)
    }

    override fun onMainActivityResume(activity: Activity?) {
        if (activity == null) {
            return
        }
        if (activity is UniversalActivity) {
            return
        }
        attachMainIcon(activity)
    }

    override fun onActivityResume(activity: Activity?) {
        if (activity == null) {
            return
        }

        //判断是否有MainIcon
        if (MsKitManager.ALWAYS_SHOW_MAIN_ICON && !MsKit.isShow) {
            MsKit.show()
        }
        MsKit.getMsKitView(activity, ViewFragmentDrawMsKitView::class.java)
            ?.updateViewLayout(ViewFragmentDrawMsKitView::class.tagName, false)
    }

    override fun onActivityBackResume(activity: Activity?) {
        if (activity == null) {
            return
        }

        //判断是否存在主入口icon
        val msKitViews = getMsKitViews(activity)
        if (msKitViews[MainIconMsKitView::class.tagName] == null) {
            if (MsKitManager.ALWAYS_SHOW_MAIN_ICON && activity !is UniversalActivity) {
                //添加main icon
                val intent = MsKitIntent(MainIconMsKitView::class.java)
                attach(intent)
                MsKitManager.MAIN_ICON_HAS_SHOW = true
            }
        }
        MsKit.getMsKitView(activity, ViewFragmentDrawMsKitView::class.java)
            ?.updateViewLayout(ViewFragmentDrawMsKitView::class.tagName, false)
    }

    override fun onActivityPaused(activity: Activity?) {
        if (activity == null) {
            return
        }

        val msKitViews = getMsKitViews(activity)
        for (absMsKitView in msKitViews.values) {
            absMsKitView.onPause()
        }
    }

    override fun onActivityStopped(activity: Activity?) {

    }

    override fun attach(msKitIntent: MsKitIntent) {
        try {
            for (msKitView in msKitViews) {
                if (msKitIntent.targetClass.isInstance(msKitView)) {
                    return
                }
            }

            val msKitView = msKitIntent.targetClass.newInstance()
            msKitView.bundle = msKitIntent.bundle
            msKitViews.add(msKitView)
            msKitView.performCreate(context)
            windowManager.addView(
                msKitView.msKitView,
                msKitView.systemLayoutParams
            )
            msKitView.onResume()
            if (!MsKitManager.IS_NORMAL_FLOAT_MODE) {
                for (listener in listeners) {
                    listener.onMsKitViewAdd(msKitView)
                }
            }
        } catch (e:Exception) {
            e.printStackTrace()
        }
    }

    override fun detach(tag: String) {
        if (TextUtils.isEmpty(tag)) {
            return
        }
        val it = msKitViews.iterator()
        while (it.hasNext()) {
            val msKitView = it.next()
            if (tag == msKitView.tag) {
                windowManager.removeView(msKitView.msKitView)
                msKitView.performDestroy()
                it.remove()
                return
            }
        }
    }
    override fun detach(msKitView: AbsMsKitView) {
        detach(msKitView.tagName)
    }

    override fun detach(msKitViewClass: Class<out AbsMsKitView>) {
        detach(msKitViewClass.tagName)
    }

    override fun detachAll() {
        val it = msKitViews.iterator()
        while (it.hasNext()) {
            val msKitView = it.next()
            windowManager.removeView(msKitView.msKitView)
            msKitView.performDestroy()
            it.remove()
        }
    }

    override fun <T : AbsMsKitView> getMsKitView(
        activity: Activity?,
        clazz: Class<T>
    ): AbsMsKitView? {
        if (TextUtils.isEmpty(clazz.tagName)) {
            return null
        }
        for (msKitView in msKitViews) {
            if (clazz.tagName == msKitView.tag) {
                return msKitView
            }
        }
        return null
    }

    /**
     * Activity销毁时调用 不需要实现 为了统一api
     */
    override fun onActivityDestroyed(activity: Activity?) {}

    /**
     * 在每一个float page创建时 添加监听器
     *
     * @param listener
     */
    fun addListener(listener: MsKitViewManager.MsKitViewAttachedListener) {
        listeners.add(listener)
    }

    fun removeListener(listener: MsKitViewManager.MsKitViewAttachedListener) {
        listeners.remove(listener)
    }
}
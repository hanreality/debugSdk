package com.melot.android.debug.sdk.core

import android.app.Activity
import android.content.Context
import android.text.TextUtils
import android.view.Gravity
import android.view.KeyEvent
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.collection.ArrayMap
import com.melot.android.debug.sdk.MsKit
import com.melot.android.debug.sdk.R
import com.melot.android.debug.sdk.extension.tagName
import com.melot.android.debug.sdk.kit.toolpanel.ToolPanelMsKitView
import com.melot.android.debug.sdk.main.MainIconMsKitView
import com.melot.android.debug.sdk.util.DevelopUtil
import com.melot.android.debug.sdk.util.MsKitSystemUtil

/**
 * Author: han.chen
 * Time: 2021/12/6 10:35
 */
internal class NormalMsKitViewManager : AbsMsKitViewManager() {

    companion object {
        private const val MC_DELAY = 100
    }

    private val context: Context get() = MsKit.requireApp()

    /**
     * 每个Activity中MsKitView的集合 用户手动移除和页面销毁时都需要remove
     *
     */
    private val activityMsKitViewMap: MutableMap<Activity?, MutableMap<String, AbsMsKitView>> by lazy {
        LinkedHashMap<Activity?, MutableMap<String, AbsMsKitView>>()
    }

    /**
     * 只用来记录全局的同步  只有用户手动移除时才会remove
     */
    private val globalSingleMsKitViewInfoMap: MutableMap<String, GlobalSingleMsKitViewInfo> by lazy {
        LinkedHashMap<String, GlobalSingleMsKitViewInfo>()
    }

    override fun notifyBackground() {
        activityMsKitViewMap.forEach { maps ->
            maps.value.forEach {
                it.value.onEnterBackground()
            }
        }
    }

    override fun notifyForeground() {
        activityMsKitViewMap.forEach { maps ->
            maps.value.forEach {
                it.value.onEnterForeground()
            }
        }
    }

    /**
     * 添加activity关联的所有msKitView activity resume的时候回调
     *
     * @param activity
     */
    override fun dispatchOnActivityResumed(activity: Activity?) {
        if (activity == null) {
            return
        }

        //app启动
        if (MsKitSystemUtil.isOnlyFirstLaunchActivity(activity)) {
            onMainActivityResume(activity)
            return
        }
        MsKitManager.ACTIVITY_LIFECYCLE_INFO_MAP[activity.javaClass.canonicalName]?.let {
            //新建Activity
            if (it.lifeCycleStatus == MsKitLifeCycleStatus.RESUME && !it.isInvokeStopMethod) {
                onActivityResume(activity)
            }

            //activity resume
            if (it.lifeCycleStatus == MsKitLifeCycleStatus.RESUME && it.isInvokeStopMethod) {
                onActivityBackResume(activity)
            }
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
        for (msKitViewInfo: GlobalSingleMsKitViewInfo in globalSingleMsKitViewInfoMap.values) {
            if (!MsKitManager.ALWAYS_SHOW_MAIN_ICON && msKitViewInfo.absMsKitViewClass == MainIconMsKitView::class.java) {
                MsKitManager.MAIN_ICON_HAS_SHOW = false
                continue
            }
            if (msKitViewInfo.absMsKitViewClass == MainIconMsKitView::class.java) {
                MsKitManager.MAIN_ICON_HAS_SHOW = true
            }
            val msKitIntent = MsKitIntent(msKitViewInfo.absMsKitViewClass)
            msKitIntent.activity = activity
            msKitIntent.bundle = msKitViewInfo.bundle
            msKitIntent.mode = msKitViewInfo.mode
            msKitIntent.tag = msKitViewInfo.tag
            attach(msKitIntent)
        }
        attachMainIcon(activity)

    }

    override fun onActivityBackResume(activity: Activity?) {
        if (activity == null) {
            return
        }

        val existMsKitViews: Map<String, AbsMsKitView>? = activityMsKitViewMap[activity]
        if (globalSingleMsKitViewInfoMap.isNotEmpty()) {
            for (gMsKitViewInfo in globalSingleMsKitViewInfoMap.values) {
                if (!MsKitManager.ALWAYS_SHOW_MAIN_ICON && gMsKitViewInfo.absMsKitViewClass == MainIconMsKitView::class.java) {
                    MsKitManager.MAIN_ICON_HAS_SHOW = false
                    continue
                }
                if (gMsKitViewInfo.absMsKitViewClass == MainIconMsKitView::class.java) {
                    MsKitManager.MAIN_ICON_HAS_SHOW = true
                }
                //判断resume Activity 中时候存在指定的msKitView
                var existMsKitView: AbsMsKitView? = null
                if (existMsKitViews != null && existMsKitViews.isNotEmpty()) {
                    existMsKitView = existMsKitViews[gMsKitViewInfo.tag]
                }

                //当前页面已存在msKitView
                if (existMsKitView?.msKitView != null) {
                    existMsKitView.msKitView?.visibility = View.VISIBLE
                    //更新位置
                    existMsKitView.updateViewLayout(existMsKitView.tag, true)
                    existMsKitView.onResume()
                } else {
                    //添加相应的
                    val msKitIntent = MsKitIntent(gMsKitViewInfo.absMsKitViewClass)
                    msKitIntent.mode = gMsKitViewInfo.mode
                    msKitIntent.bundle = gMsKitViewInfo.bundle
                    msKitIntent.tag = gMsKitViewInfo.tag
                    attach(msKitIntent)
                }
            }
        }
        attachMainIcon(activity)
    }

    override fun onActivityPaused(activity: Activity?) {
        if (activity == null) {
            return
        }

        val msKitViews = getMsKitViews(activity)
        msKitViews.let {
            for (doKitView: AbsMsKitView in it.values) {
                doKitView.onPause()
            }
        }
    }

    override fun onActivityStopped(activity: Activity?) {

    }

    override fun onActivityDestroyed(activity: Activity?) {
        if (activity == null) {
            return
        }

        //移除msKit根布局
        val msKitRootView = activity.findViewById<View>(R.id.msKit_contentview_id)
        if (msKitRootView != null) {
            getDecorView(activity).removeView(msKitRootView)
        }
        val msKitViewMap: Map<String, AbsMsKitView> = getMsKitViews(activity)
        for (msKitView in msKitViewMap.values) {
            msKitView.performDestroy()
        }
        activityMsKitViewMap.remove(activity)
    }

    override fun attach(msKitIntent: MsKitIntent) {
        try {
            //判断当前Activity是否存在msKitView map
            val currentActivityMsKitViews: MutableMap<String, AbsMsKitView> = when {
                (activityMsKitViewMap[msKitIntent.activity] == null) -> {
                    val msKitViewMap = mutableMapOf<String, AbsMsKitView>()
                    activityMsKitViewMap[msKitIntent.activity] = msKitViewMap
                    msKitViewMap
                }
                else -> {
                    activityMsKitViewMap[msKitIntent.activity]!!
                }
            }
            //判断该msKitView是否已经显示在页面上 同一个类型的msKitView在页面上只显示一个
            if (currentActivityMsKitViews[msKitIntent.tag] != null) {
                currentActivityMsKitViews[msKitIntent.tag]?.updateViewLayout(msKitIntent.tag, true)
                return
            }
            val msKitView = msKitIntent.targetClass.newInstance()
            //在当前Activity中保存msKitView
            //设置msKitView的属性
            msKitView.mode = msKitIntent.mode
            msKitView.bundle = msKitIntent.bundle
            msKitView.tag = msKitIntent.tag
            msKitView.setActivity(msKitIntent.activity)
            msKitView.performCreate(context)
            //在全局msKitViews中保存该类型的
            globalSingleMsKitViewInfoMap[msKitView.tag] =
                createGlobalSingleMsKitViewInfo(msKitView)
            //往DecorView的子RootView中添加msKitView
            if (msKitView.normalLayoutParams != null && msKitView.msKitView != null) {
                getMsKitRootContentView(msKitIntent.activity)?.addView(
                    msKitView.msKitView,
                    msKitView.normalLayoutParams
                )
                msKitView.postDelayed(Runnable {
                    msKitView.onResume()
                    msKitView.dealDecorRootView(getMsKitRootContentView(msKitIntent.activity))
                }, MC_DELAY.toLong())
            }
            currentActivityMsKitViews[msKitView.tag] = msKitView
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun detach(msKitView: AbsMsKitView) {
        detach(msKitView.tag)
    }

    override fun detach(tag: String) {
        realDetach(tag)
    }

    private fun realDetach(tag: String) {
        for (activityKey in activityMsKitViewMap.keys) {
            val msKitViewMap = activityMsKitViewMap[activityKey]
            val msKitView = msKitViewMap?.get(tag) ?: continue
            if (msKitView.msKitView != null) {
                msKitView.msKitView?.visibility = View.GONE
                getMsKitRootContentView(msKitView.activity)?.removeView(msKitView.msKitView)
            }
            activityKey?.let {
                getDecorView(it).requestLayout()
            }
            //执行msKitView的销毁
            msKitView.performDestroy()
            //移除map中的数据
            msKitViewMap.remove(tag)
        }
        //同步移除全局指定类型的msKitView
        if (globalSingleMsKitViewInfoMap.containsKey(tag)) {
            globalSingleMsKitViewInfoMap.remove(tag)
        }
    }

    override fun detach(msKitViewClass: Class<out AbsMsKitView>) {
        detach(msKitViewClass.tagName)
    }

    override fun detachAll() {
        //移除每个activity中所有的msKitView
        for (activityKey in activityMsKitViewMap.keys) {
            val msKitViewMap = activityMsKitViewMap[activityKey]
            //移除指定UI
            getMsKitRootContentView(activityKey)?.removeAllViews()
            //移除map中的数据
            msKitViewMap?.clear()
        }
        globalSingleMsKitViewInfoMap.clear()
    }

    private fun getMsKitRootContentView(activity: Activity?): FrameLayout? {
        activity?.let {
            val decorView = getDecorView(it)
            var msKitRootView = decorView.findViewById<FrameLayout>(R.id.msKit_contentview_id)
            if (msKitRootView != null) {
                return msKitRootView
            }
            msKitRootView = MsKitFrameLayout(context, MsKitFrameLayout.MsKitFrameLayoutFlag_ROOT)
            //普通模式的返回按键监听
            msKitRootView.setOnKeyListener(View.OnKeyListener { _, keyCode, _ ->
                //监听返回键
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    val doKitViewMap: Map<String, AbsMsKitView> = getMsKitViews(activity)
                    if (doKitViewMap.isEmpty()) {
                        return@OnKeyListener false
                    }
                    for (doKitView in doKitViewMap.values) {
                        if (doKitView.shouldDealBackKey()) {
                            return@OnKeyListener doKitView.onBackPressed()
                        }
                    }
                    return@OnKeyListener false
                }
                false
            })
            msKitRootView.clipChildren = false
            msKitRootView.clipToPadding = false

            //解决无法获取返回按键的问题
            msKitRootView.setFocusable(true)
            msKitRootView.setFocusableInTouchMode(true)
//            msKitRootView.requestFocus()
            msKitRootView.setId(R.id.msKit_contentview_id)
            val msKitParams = FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            try {
                //解决由于项目集成SwipeBackLayout而出现的msKit入口不显示
                if (DevelopUtil.isStatusBarVisible((activity))) {
                    msKitParams.topMargin = DevelopUtil.getStatusBarHeight()
                }
                if (DevelopUtil.isSupportNavBar()) {
                    if (DevelopUtil.isNavBarVisible((activity))) {
                        msKitParams.bottomMargin = DevelopUtil.getNavBarHeight()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            msKitParams.gravity = Gravity.BOTTOM
            msKitRootView.setLayoutParams(msKitParams)
            //添加到DecorView中 为了不和用户自己往根布局中添加view干扰
            decorView.addView(msKitRootView)
            return msKitRootView
        }
        return null
    }

    override fun getMsKitViews(activity: Activity?): Map<String, AbsMsKitView> {
        return activityMsKitViewMap[activity] ?: emptyMap()
    }

    override fun <T : AbsMsKitView> getMsKitView(
        activity: Activity?,
        clazz: Class<T>
    ): AbsMsKitView? {
        if (TextUtils.isEmpty(clazz.tagName)) {
            return null
        }

        return activityMsKitViewMap[activity]?.get(clazz.tagName)
    }

    /**
     * 获取页面根布局
     *
     * @param activity
     * @return
     */
    private fun getDecorView(activity: Activity): ViewGroup {
        return activity.window.decorView as ViewGroup
    }

    private fun createGlobalSingleMsKitViewInfo(absMsKitView: AbsMsKitView): GlobalSingleMsKitViewInfo {
        return GlobalSingleMsKitViewInfo(
            absMsKitView.javaClass,
            absMsKitView.tag,
            absMsKitView.mode,
            absMsKitView.bundle
        )
    }
}
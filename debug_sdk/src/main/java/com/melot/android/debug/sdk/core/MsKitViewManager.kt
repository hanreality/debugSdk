package com.melot.android.debug.sdk.core

import android.app.Activity
import android.content.Context
import android.content.res.Configuration
import android.graphics.Point
import android.view.WindowManager
import androidx.collection.ArrayMap
import com.melot.android.debug.sdk.MsKit
import com.melot.android.debug.sdk.util.DevelopUtil

/**
 * Author: han.chen
 * Time: 2021/12/3 17:04
 */
internal class MsKitViewManager : MsKitViewManagerInterface {

    companion object {
        private const val TAG = "MsKitViewManagerProxy"

        @JvmStatic
        val INSTANCE: MsKitViewManager by lazy { MsKitViewManager() }

        /**
         * 每个类型在页面中的位置 只保存marginLeft 和marginTop
         */
        private val msKitViewPos: MutableMap<String, MsKitViewInfo> =
            ArrayMap<String, MsKitViewInfo>()
    }

    val windowManager: WindowManager
        get() = MsKit.requireApp().getSystemService(Context.WINDOW_SERVICE) as WindowManager

    private val lastMsKitViewPosInfoMaps: MutableMap<String, LastMsKitViewPosInfo> =
        ArrayMap<String, LastMsKitViewPosInfo>()

    private var _msKitViewManager: AbsMsKitViewManager? = null

    fun init() {}

    override fun attach(msKitIntent: MsKitIntent) {
        ensureViewManager().attach(msKitIntent)
    }

    override fun detach(msKitView: AbsMsKitView) {
        ensureViewManager().detach(msKitView)
    }

    override fun detach(tag: String) {
        ensureViewManager().detach(tag)
    }

    override fun detach(msKitViewClass: Class<out AbsMsKitView>) {
        ensureViewManager().detach(msKitViewClass)
    }

    override fun detachAll() {
        ensureViewManager().detachAll()
    }

    override fun <T : AbsMsKitView> getMsKitView(
        activity: Activity?,
        clazz: Class<T>
    ): AbsMsKitView? {
        return activity?.let { ensureViewManager().getMsKitView(it, clazz) }
    }

    override fun getMsKitViews(activity: Activity?): Map<String, AbsMsKitView>? {
        return activity?.let { ensureViewManager().getMsKitViews(it) }
    }

    override fun notifyBackground() {
        ensureViewManager().notifyBackground()
    }

    override fun notifyForeground() {
        ensureViewManager().notifyForeground()
    }

    fun saveMsKitViewPos(tag: String, marginLeft: Int, marginTop: Int) {
        var orientation = -1
        val portraitPoint = Point()
        val landscapePoint = Point()
        if (DevelopUtil.isPortrait()) {
            orientation = Configuration.ORIENTATION_PORTRAIT
            portraitPoint.x = marginLeft
            portraitPoint.y = marginTop
        } else {
            orientation = Configuration.ORIENTATION_LANDSCAPE
            landscapePoint.x = marginLeft
            landscapePoint.y = marginTop
        }
        if (msKitViewPos[tag] == null) {
            val doKitViewInfo = MsKitViewInfo(orientation, portraitPoint, landscapePoint)
            msKitViewPos[tag] =
                doKitViewInfo
        } else {
            val msKitViewInfo = msKitViewPos[tag]
            if (msKitViewInfo != null) {
                msKitViewInfo.orientation = orientation
                msKitViewInfo.portraitPoint = portraitPoint
                msKitViewInfo.landscapePoint = landscapePoint
            }
        }
    }

    fun getMsKitViewPos(tag: String): MsKitViewInfo? = msKitViewPos[tag]

    override fun onActivityDestroyed(activity: Activity?) {
        activity?.also { ensureViewManager().onActivityDestroyed(it) }
    }

    override fun onActivityPaused(activity: Activity?) {
        activity?.also { ensureViewManager().onActivityPaused(it) }
    }

    override fun onActivityStopped(activity: Activity?) {
        activity?.also { ensureViewManager().onActivityStopped(it) }
    }

    override fun dispatchOnActivityResumed(activity: Activity?) {
        activity?.also { ensureViewManager().dispatchOnActivityResumed(it) }
    }

    /**
     * 隐藏工具列表msKitView
     */
    fun detachToolPanel() {
        ensureViewManager().detachToolPanel()
    }

    /**
     * 显示工具列表msKitView
     */
    fun attachToolPanel(activity: Activity?) {
        ensureViewManager().attachToolPanel(activity)
    }

    /**
     * 显示主图标 msKitView
     */
    fun attachMainIcon(activity: Activity?) {
        ensureViewManager().attachMainIcon(activity)
    }

    /**
     * 隐藏首页图标
     */
    fun detachMainIcon() {
        ensureViewManager().detachMainIcon()
    }

    @Synchronized
    private fun ensureViewManager(): AbsMsKitViewManager {
        return _msKitViewManager
            ?: run {
                if (MsKitManager.IS_NORMAL_FLOAT_MODE) NormalMsKitViewManager() else SystemMsKitViewManager()
            }.also {
                _msKitViewManager = it
            }
    }

    /**
     * 系统悬浮窗需要调用
     */
    interface MsKitViewAttachedListener {
        fun onMsKitViewAdd(absMsKitView: AbsMsKitView?)
    }

    fun addMsKitViewAttachedListener(listener: MsKitViewAttachedListener?) {
        listener?.takeIf {
            !MsKitManager.IS_NORMAL_FLOAT_MODE && _msKitViewManager is SystemMsKitViewManager
        }?.also {
            (_msKitViewManager as? SystemMsKitViewManager)?.addListener(it)
        }
    }

    fun removeMsKitViewAttachedListener(listener: MsKitViewAttachedListener?) {
        listener?.takeIf { !MsKitManager.IS_NORMAL_FLOAT_MODE && _msKitViewManager is SystemMsKitViewManager }
            ?.also {
                (_msKitViewManager as SystemMsKitViewManager).removeListener(it)
            }
    }

    fun saveLastMsKitViewPosInfo(key: String, lastMsKitViewPosInfo: LastMsKitViewPosInfo) {
        lastMsKitViewPosInfoMaps[key] = lastMsKitViewPosInfo
    }

    fun getLastMsKitViewPosInfo(key: String): LastMsKitViewPosInfo? = lastMsKitViewPosInfoMaps[key]

    fun removeLastMsKitViewPosInfo(key: String) {
        lastMsKitViewPosInfoMaps.remove(key)
    }
}
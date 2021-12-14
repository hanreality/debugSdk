package com.melot.android.debug.sdk

import android.app.Activity
import android.app.Application
import android.graphics.Point
import android.os.Bundle
import com.google.gson.reflect.TypeToken
import com.melot.android.debug.sdk.constant.SharedPrefsKey
import com.melot.android.debug.sdk.core.*
import com.melot.android.debug.sdk.core.MsKitViewManager
import com.melot.android.debug.sdk.core.SimpleMsKitLauncher
import com.melot.android.debug.sdk.extension.doKitGlobalScope
import com.melot.android.debug.sdk.kit.AbstractKit
import com.melot.android.debug.sdk.kit.toolpanel.KitGroupBean
import com.melot.android.debug.sdk.kit.toolpanel.KitWrapItem
import com.melot.android.debug.sdk.proxy.IDebugProxy
import com.melot.android.debug.sdk.util.ActivityUtils
import com.melot.android.debug.sdk.util.DebugGsonUtil
import com.melot.android.debug.sdk.util.MMKVUtil
import com.melot.android.debug.sdk.util.MsKitSystemUtil

import kotlinx.coroutines.launch
import java.util.*

/**
 * Author: han.chen
 * Time: 2021/12/3 15:45
 */
object MsKit {

    const val TAG = "MsKit"

    @Volatile
    var app: Application? = null

    private var debugProxy: IDebugProxy? =null

    val windowSize: Point = Point()

    val isShow: Boolean
        get() = MsKitManager.MAIN_ICON_HAS_SHOW

    @JvmStatic
    fun requireApp(): Application {
        return app ?: throw IllegalStateException("Dokit app no set")
    }

    fun getProxy() : IDebugProxy? {
        return debugProxy
    }

    @JvmStatic
    fun show() {
        MsKitManager.ALWAYS_SHOW_MAIN_ICON = true
        if (!isShow) {
            MsKitViewManager.INSTANCE.attachMainIcon(ActivityUtils.getTopActivity())
        }
    }

    @JvmStatic
    fun hide() {
        MsKitManager.MAIN_ICON_HAS_SHOW = false
        MsKitManager.ALWAYS_SHOW_MAIN_ICON = false
        MsKitViewManager.INSTANCE.detachMainIcon()
    }

    @JvmStatic
    fun showToolPanel() {
        MsKitViewManager.INSTANCE.attachToolPanel(ActivityUtils.getTopActivity())
    }

    @JvmStatic
    fun hideToolPanel() {
        MsKitViewManager.INSTANCE.detachToolPanel()
    }

    @JvmStatic
    @JvmOverloads
    fun launchFloating(
        targetClass: Class<out AbsMsKitView>,
        mode: MsKitViewLaunchMode = MsKitViewLaunchMode.SINGLE_INSTANCE,
        bundle: Bundle? = null
    ) {
        SimpleMsKitLauncher.launchFloating(targetClass, mode, bundle)
    }

    /**
     * 启动悬浮窗
     */
    inline fun <reified T : AbsMsKitView> launchFloating(mode: MsKitViewLaunchMode = MsKitViewLaunchMode.SINGLE_INSTANCE, bundle: Bundle? = null) {
        launchFloating(T::class.java, mode, bundle)
    }

    @JvmStatic
    fun removeFloating(targetClass: Class<out AbsMsKitView>) {
        SimpleMsKitLauncher.removeFloating(targetClass)
    }

    @JvmStatic
    fun <T : AbsMsKitView> getMsKitView(
        activity: Activity?,
        clazz: Class<out T>
    ): T? {
        return MsKitViewManager.INSTANCE.getMsKitView(activity, clazz) as? T
    }

    fun install(app: Application, debugProxy: IDebugProxy?) {
        this.app = app
        this.debugProxy = debugProxy
        val strMsKitMode = MMKVUtil.getString(SharedPrefsKey.FLOAT_START_MODE, "normal")
        MsKitManager.IS_NORMAL_FLOAT_MODE = strMsKitMode == "normal"
        app.registerActivityLifecycleCallbacks(MsKitActivityLifecycleCallbacks())
        MsKitManager.GLOBAL_KITS.clear()
        doKitGlobalScope.launch {
            addInnerKit(app)
        }
    }

    private fun addInnerKit(app: Application) {
        val msKitInnerKits =
            ServiceLoader.load(AbstractKit::class.java, javaClass.classLoader).toList()

        val msKitInnerKitMaps = mutableMapOf<String, AbstractKit>()

        msKitInnerKits.forEach {
            msKitInnerKitMaps[it.innerKitId()] = it
        }

        val json = MsKitSystemUtil.getAssetsJson(app, "mskit_kits.json")
        val localConfigs: MutableList<KitGroupBean> =
            DebugGsonUtil.GsonToBean(json, object : TypeToken<MutableList<KitGroupBean>>() {}.type)
        localConfigs.forEach { group ->
            MsKitManager.GLOBAL_KITS[group.groupId] = mutableListOf()
            group.kits.forEach { kitBean ->
                val kit: AbstractKit? = msKitInnerKitMaps[kitBean.innerKitId]
                kit?.let {
                    val kitWrapItem = KitWrapItem(
                        KitWrapItem.TYPE_KIT,
                        app.getString(kit.name),
                        kitBean.checked,
                        group.groupId,
                        kit
                    )
                    MsKitManager.GLOBAL_KITS[group.groupId]?.add(kitWrapItem)
                }
            }
        }
        //悬浮窗模式
        MsKitManager.GLOBAL_KITS[app.getString(R.string.debug_category_mode)] =
            mutableListOf()
        //添加退出项
        MsKitManager.GLOBAL_KITS[app.getString(R.string.debug_category_exit)] =
            mutableListOf()
        //遍历初始化
        MsKitManager.GLOBAL_KITS.forEach { map ->
            map.value.forEach { kitWrap ->
                kitWrap.kit?.onAppInit(app)
            }
        }
    }

}
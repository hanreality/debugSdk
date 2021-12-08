package com.melot.android.debug.sdk.core

import com.melot.android.debug.sdk.kit.toolpanel.KitWrapItem

/**
 * Author: han.chen
 * Time: 2021/12/3 17:46
 */
object MsKitManager {
    const val TAG = "MsKitConstant"
    const val GROUP_ID_COMMON = "ms_category_common"
    const val GROUP_ID_UI = "ms_category_ui"


    val ACTIVITY_LIFECYCLE_INFO_MAP: MutableMap<String, ActivityLifecycleStatusInfo?> by lazy {
        mutableMapOf<String, ActivityLifecycleStatusInfo?>()
    }

    /**
     * 是否是普通的浮标模式
     */
    @JvmField
    var IS_NORMAL_FLOAT_MODE = true

    /**
     * 是否显示icon主入口
     */
    @JvmField
    var ALWAYS_SHOW_MAIN_ICON = true

    /**
     * icon主入口是否处于显示状态
     */
    @JvmField
    var MAIN_ICON_HAS_SHOW = false

    /**
     * fragment信息是否开启
     */
    @JvmField
    var FRAGMENT_INFO_CHECKED = false

    @JvmField
    val GLOBAL_KITS: LinkedHashMap<String, MutableList<KitWrapItem>> = LinkedHashMap()
}
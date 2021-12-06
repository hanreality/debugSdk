package com.melot.android.debug.sdk.core

/**
 * Author: han.chen
 * Time: 2021/12/3 17:46
 */
object MsKitManager {
    const val TAG = "DoKitConstant"
    const val GROUP_ID_PLATFORM = "dk_category_platform"
    const val GROUP_ID_COMM = "dk_category_comms"
    const val GROUP_ID_WEEX = "dk_category_weex"
    const val GROUP_ID_PERFORMANCE = "dk_category_performance"
    const val GROUP_ID_UI = "dk_category_ui"
    const val GROUP_ID_LBS = "dk_category_lbs"


    val ACTIVITY_LIFECYCLE_INFO_MAP: MutableMap<String, ActivityLifecycleStatusInfo?> by lazy {
        mutableMapOf<String, ActivityLifecycleStatusInfo?>()
    }

}
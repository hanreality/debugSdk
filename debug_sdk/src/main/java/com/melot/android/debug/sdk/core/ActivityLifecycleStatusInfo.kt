package com.melot.android.debug.sdk.core

/**
 * Author: han.chen
 * Time: 2021/12/3 17:43
 */
data class ActivityLifecycleStatusInfo(
    var isInvokeStopMethod: Boolean? = false,
    var lifeCycleStatus: MsKitLifeCycleStatus? = MsKitLifeCycleStatus.CREATED,
    var activityName: String? = ""
)

enum class MsKitLifeCycleStatus {
    /**
     * Activity 创建
     */
    CREATED,

    /**
     * Activity resume
     */
    RESUME,

    /**
     * Activity stop
     */
    STOPPED,

    /**
     * Activity destroy
     */
    DESTROYED
}

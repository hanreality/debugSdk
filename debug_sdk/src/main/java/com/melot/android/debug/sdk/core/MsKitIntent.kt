package com.melot.android.debug.sdk.core

import android.app.Activity
import android.os.Bundle
import com.melot.android.debug.sdk.extension.tagName
import com.melot.android.debug.sdk.util.RunningActivityFetcher

/**
 * Author: han.chen
 * Time: 2021/12/3 16:02
 */
class MsKitIntent (var targetClass: Class<out AbsMsKitView>,
                   var activity: Activity? = RunningActivityFetcher.getTopActivity(),
                   var bundle: Bundle? = null,
                   var tag: String = targetClass.tagName,
                   var mode: MsKitViewLaunchMode = MsKitViewLaunchMode.SINGLE_INSTANCE
)


enum class MsKitViewLaunchMode {
    SINGLE_INSTANCE,
    /**
     * 倒计时
     */
    COUNTDOWN
}
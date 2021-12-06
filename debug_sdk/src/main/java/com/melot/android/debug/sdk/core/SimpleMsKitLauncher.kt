package com.melot.android.debug.sdk.core

import android.os.Bundle
import com.melot.android.debug.sdk.DebugManager

/**
 * Author: han.chen
 * Time: 2021/12/3 17:00
 */
internal object SimpleMsKitLauncher {
    fun launchFloating(
        targetClass: Class<out AbsMsKitView>,
        mode:MsKitViewLaunchMode = MsKitViewLaunchMode.SINGLE_INSTANCE,
        bundle: Bundle? = null
    ) {
        val msKitIntent = MsKitIntent(targetClass)
        msKitIntent.mode = mode
        msKitIntent.bundle = bundle

    }
}
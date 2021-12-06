package com.melot.android.debug.sdk.core

import android.os.Bundle

/**
 * Author: han.chen
 * Time: 2021/12/3 17:16
 */
data class GlobalSingleMsKitViewInfo(
    val absMsKitViewClass: Class<out AbsMsKitView?>,
    val tag: String,
    val mode: MsKitViewLaunchMode,
    val bundle: Bundle?
)

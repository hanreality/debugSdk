package com.melot.android.debug.sdk.proxy

import androidx.annotation.Keep

/**
 * Author: han.chen
 * Time: 2021/9/13 10:37
 */
@Keep
data class DebugConfig(
    var channel: String?,
    var serverDebug: Boolean,
    var logDebug: Boolean,
    var forceNotCDN: Boolean,
    var appName: String
)
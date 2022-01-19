package com.melot.android.debug.sdk.model

import androidx.annotation.Keep

/**
 * Author: han.chen
 * Time: 2021/11/23 16:30
 */
@Keep
data class DebugTestAccountData(
    var accountId: String,
    var password: String,
    var info:String?,
    var detail:String?
)

package com.melot.android.debug.sdk.kit.server

import android.app.Activity
import android.content.Context
import com.google.auto.service.AutoService
import com.melot.android.debug.sdk.MsKit
import com.melot.android.debug.sdk.R
import com.melot.android.debug.sdk.kit.AbstractKit
import com.melot.android.debug.sdk.util.ActivityUtils

/**
 * Author: han.chen
 * Time: 2021/12/7 17:59
 */
@AutoService(AbstractKit::class)
class ChangeServerKit : AbstractKit() {
    override val name: Int
        get() = if (
            MsKit.getProxy()?.debugConfig()?.serverDebug == true
        ) R.string.ms_change_server_debug else R.string.ms_change_server_release
    override val icon: Int
        get() = R.drawable.ms_ic_change_server

    override fun onAppInit(context: Context?) {

    }

    override val isInnerKit: Boolean
        get() = true

    override fun innerKitId(): String {
        return "mskit_sdk_common_change_server"
    }

    override fun onClick(activity: Activity?): Boolean {
        MsKit.getProxy()?.changeServer()
        ActivityUtils.unInit()
        return true
    }
}
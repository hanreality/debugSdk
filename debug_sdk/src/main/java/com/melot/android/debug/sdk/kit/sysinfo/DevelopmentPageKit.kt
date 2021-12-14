package com.melot.android.debug.sdk.kit.sysinfo

import android.app.Activity
import android.content.Context
import com.google.auto.service.AutoService
import com.melot.android.debug.sdk.R
import com.melot.android.debug.sdk.kit.AbstractKit
import com.melot.android.debug.sdk.util.MsKitSystemUtil

/**
 * Author: han.chen
 * Time: 2021/12/14 11:23
 */
@AutoService(AbstractKit::class)
class DevelopmentPageKit : AbstractKit() {
    override val name: Int
        get() = R.string.ms_development
    override val icon: Int
        get() = R.drawable.ms_ic_development

    override fun onClick(activity: Activity?): Boolean {
        MsKitSystemUtil.startDevelopmentActivity(activity)
        return true
    }

    override fun onAppInit(context: Context?) {}
    override val isInnerKit: Boolean
        get() = true

    override fun innerKitId(): String {
        return "mskit_sdk_common_ck_devpage"
    }
}
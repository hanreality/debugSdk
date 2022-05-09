package com.melot.android.debug.sdk.kit.hint

import android.app.Activity
import android.content.Context
import com.google.auto.service.AutoService
import com.melot.android.debug.sdk.MsKit
import com.melot.android.debug.sdk.R
import com.melot.android.debug.sdk.kit.AbstractKit
import com.melot.android.debug.sdk.kit.quicklogin.QuickLoginInfoMsKitView

/**
 * Author: han.chen
 * Time: 2022/1/7 15:58
 */
@AutoService(AbstractKit::class)
class HintManagerKit: AbstractKit() {
    override val name: Int
        get() = R.string.ms_hint_manager
    override val icon: Int
        get() = R.drawable.ms_ic_hint_manager

    override fun onClick(activity: Activity?): Boolean {
        MsKit.launchFloating<HintManagerPageMsKitView>()
        return true
    }

    override fun onAppInit(context: Context?) {}
    override val isInnerKit: Boolean
        get() = true

    override fun innerKitId(): String {
        return "mskit_sdk_common_hint_manager"
    }
}
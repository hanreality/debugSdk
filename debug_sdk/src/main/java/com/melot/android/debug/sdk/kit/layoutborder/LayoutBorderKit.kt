package com.melot.android.debug.sdk.kit.layoutborder

import android.app.Activity
import android.content.Context
import com.google.auto.service.AutoService
import com.melot.android.debug.sdk.MsKit
import com.melot.android.debug.sdk.R
import com.melot.android.debug.sdk.config.LayoutBorderConfig
import com.melot.android.debug.sdk.kit.AbstractKit

/**
 * Author: han.chen
 * Time: 2021/12/22 15:48
 */
@AutoService(AbstractKit::class)
class LayoutBorderKit :AbstractKit() {
    override val name: Int
        get() = R.string.ms_layout_border
    override val icon: Int
        get() = R.drawable.ms_ic_layout_border

    override fun onAppInit(context: Context?) {
        LayoutBorderConfig.sLayoutBorderOpen = false
        LayoutBorderConfig.sLayoutLevelOpen = false
    }

    override fun onClick(activity: Activity?): Boolean {
        MsKit.launchFloating<LayoutBorderMsKitView>()
        return true
    }

    override val isInnerKit: Boolean
        get() = true

    override fun innerKitId(): String {
        return "mskit_sdk_ui_layout_border"
    }
}
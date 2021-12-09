package com.melot.android.debug.sdk.kit.viewcheck

import android.app.Activity
import android.content.Context
import com.google.auto.service.AutoService
import com.melot.android.debug.sdk.MsKit
import com.melot.android.debug.sdk.R
import com.melot.android.debug.sdk.kit.AbstractKit

/**
 * Author: han.chen
 * Time: 2021/12/8 19:10
 */
@AutoService(AbstractKit::class)
class ViewCheckerKit:AbstractKit() {
    override val name: Int
        get() = R.string.ms_view_checker
    override val icon: Int
        get() = R.drawable.ms_ic_view_checker

    override fun onAppInit(context: Context?) {

    }

    override fun onClick(activity: Activity?): Boolean {
        MsKit.launchFloating<ViewCheckMsKitView>()
        MsKit.launchFloating<ViewCheckDrawMsKitView>()
        MsKit.launchFloating<ViewCheckerInfoMsKitView>()
        return true
    }

    override val isInnerKit: Boolean
        get() = true

    override fun innerKitId(): String {
        return "mskit_sdk_ui_view_checker"
    }
}
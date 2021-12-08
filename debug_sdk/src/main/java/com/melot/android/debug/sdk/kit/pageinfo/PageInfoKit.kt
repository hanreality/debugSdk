package com.melot.android.debug.sdk.kit.pageinfo

import android.app.Activity
import android.content.Context
import com.google.auto.service.AutoService
import com.melot.android.debug.sdk.MsKit
import com.melot.android.debug.sdk.R
import com.melot.android.debug.sdk.kit.AbstractKit

/**
 * Author: han.chen
 * Time: 2021/12/7 17:35
 */
@AutoService(AbstractKit::class)
class PageInfoKit : AbstractKit() {
    override val name: Int
        get() = R.string.ms_page_info
    override val icon: Int
        get() = R.drawable.ms_ic_page_info

    override fun onAppInit(context: Context?) {

    }

    override val isInnerKit: Boolean
        get() = true

    override fun innerKitId(): String {
        return "mskit_sdk_common_page_info"
    }

    override fun onClick(activity: Activity?): Boolean {
        MsKit.launchFloating<ViewPageInfoMsKitView>()
        return true
    }
}
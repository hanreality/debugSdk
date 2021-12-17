package com.melot.android.debug.sdk.kit.fragmentInfo

import android.app.Activity
import android.content.Context
import com.google.auto.service.AutoService
import com.melot.android.debug.sdk.MsKit
import com.melot.android.debug.sdk.R
import com.melot.android.debug.sdk.kit.AbstractKit

/**
 * Author: han.chen
 * Time: 2021/12/7 17:54
 */
@AutoService(AbstractKit::class)
class FragmentInfoKit : AbstractKit() {
    override val name: Int
        get() = R.string.ms_fragment_info
    override val icon: Int
        get() = R.drawable.ms_ic_fragment_info

    override fun onAppInit(context: Context?) {

    }

    override val isInnerKit: Boolean
        get() = true

    override fun innerKitId(): String {
        return "mskit_sdk_common_fragment_info"
    }

    override fun onClick(activity: Activity?): Boolean {
        MsKit.launchFloating<ViewFragmentDrawMsKitView>()
        MsKit.launchFloating<ViewFragmentInfoMsKitView>()
        return true
    }
}
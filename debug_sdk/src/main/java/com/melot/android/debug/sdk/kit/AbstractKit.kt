package com.melot.android.debug.sdk.kit

import android.app.Activity
import android.content.Context
import android.os.Bundle
import com.melot.android.debug.sdk.base.BaseFragment
import com.melot.android.debug.sdk.util.ActivityUtils

/**
 * Author: han.chen
 * Time: 2021/12/3 12:43
 */
abstract class AbstractKit : IKit {

    fun startUniversalActivity(
        fragmentClass:Class<out BaseFragment>,
        context: Context?,
        bundle: Bundle? = null,
        isSystemFragment: Boolean = false
    ) {

    }

    fun currentActivity(): Activity? {
        return ActivityUtils.getTopActivity()
    }

    override val category: Int
        get() = Category.DEFAULT

    open val isInnerKit: Boolean
        get() = false

    /**
     * 是否可以显示在工具面板上
     */
    var canShow: Boolean = true

    open fun innerKitId(): String {
        return ""
    }
}
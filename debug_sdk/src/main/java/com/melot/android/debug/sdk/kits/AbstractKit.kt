package com.melot.android.debug.sdk.kits

import android.app.Activity
import android.content.Context
import android.os.Bundle
import com.melot.android.debug.sdk.base.BaseFragment
import com.melot.android.debug.sdk.util.RunningActivityFetcher

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
        return RunningActivityFetcher.getTopActivity()
    }

    override val category: Int
        get() = Category.DEFAULT
}
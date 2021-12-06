package com.melot.android.debug.sdk

import android.app.Application
import android.graphics.Point
import android.os.Bundle
import com.melot.android.debug.sdk.core.AbsMsKitView
import com.melot.android.debug.sdk.core.MsKitViewLaunchMode

/**
 * Author: han.chen
 * Time: 2021/12/3 15:45
 */
object MsKit {

    const val TAG = "MsKit"

    @Volatile
    var app: Application? = null

    val windowSize: Point = Point()

    @JvmStatic
    fun requireApp(): Application {
        return app ?: throw IllegalStateException("Dokit app no set")
    }

    @JvmStatic
    fun show() {}

    @JvmStatic
    fun hide() {}

    @JvmStatic
    fun showToolPanel() {}

    @JvmStatic
    fun hideToolPanel() {}

    @JvmStatic
    @JvmOverloads
    fun launchFloating(targetClass: Class<out AbsMsKitView>, mode: MsKitViewLaunchMode = MsKitViewLaunchMode.SINGLE_INSTANCE, bundle: Bundle? = null) {

    }

}
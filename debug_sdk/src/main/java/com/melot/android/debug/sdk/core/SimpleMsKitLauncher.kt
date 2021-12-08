package com.melot.android.debug.sdk.core

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.melot.android.debug.sdk.MsKit
import com.melot.android.debug.sdk.constant.BundleKey
import com.melot.android.debug.sdk.constant.FragmentIndex
import com.melot.android.debug.sdk.extension.tagName

/**
 * Author: han.chen
 * Time: 2021/12/3 17:00
 */
internal object SimpleMsKitLauncher {
    fun launchFloating(
        targetClass: Class<out AbsMsKitView>,
        mode: MsKitViewLaunchMode = MsKitViewLaunchMode.SINGLE_INSTANCE,
        bundle: Bundle? = null
    ) {
        val msKitIntent = MsKitIntent(targetClass)
        msKitIntent.mode = mode
        msKitIntent.bundle = bundle
        MsKitViewManager.INSTANCE.attach(msKitIntent)
    }

    fun removeFloating(
        targetClass: Class<out AbsMsKitView>
    ) {
        MsKitViewManager.INSTANCE.detach(targetClass.tagName)
    }

    fun removeFloating(
        absMsKitView: AbsMsKitView
    ) {
        MsKitViewManager.INSTANCE.detach(absMsKitView)
    }

    fun launchFullScreen(
        targetClass: Class<out AbsMsKitView>,
        context: Context? = null,
        bundle: Bundle? = null,
        isSystemFragment: Boolean = false
    ) {
        val ctx = context?: MsKit.requireApp().applicationContext
        ctx.startActivity(Intent(ctx, UniversalActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
            if (isSystemFragment) {
                putExtra(BundleKey.FRAGMENT_INDEX, FragmentIndex.FRAGMENT_SYSTEM)
                putExtra(BundleKey.SYSTEM_FRAGMENT_CLASS, targetClass)
            } else {
                putExtra(BundleKey.FRAGMENT_INDEX, FragmentIndex.FRAGMENT_CUSTOM)
                putExtra(BundleKey.CUSTOM_FRAGMENT_CLASS, targetClass)
            }
            if (bundle != null) {
                putExtras(bundle)
            }
        })
    }
}
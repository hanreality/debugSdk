package com.melot.android.lib.debugsdk

import android.app.Application
import com.didichuxing.doraemonkit.DoKit
import com.melot.android.KKSp
import com.melot.android.debug.sdk.MsKit
import com.tencent.mmkv.MMKV

/**
 * Author: han.chen
 * Time: 2021/12/6 17:15
 */
class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()
        MMKV.initialize(this)
        DoKit.Builder(this)
            .build()
        MsKit.app = this
        MsKit.setProxy(SampleDebugProxy())
        MsKit.install()
    }
}
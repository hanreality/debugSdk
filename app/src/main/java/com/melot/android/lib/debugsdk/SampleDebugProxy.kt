package com.melot.android.lib.debugsdk

import com.melot.android.debug.sdk.proxy.DebugConfig
import com.melot.android.debug.sdk.proxy.IDebugProxy

/**
 * Author: han.chen
 * Time: 2021/12/3 10:49
 */
class SampleDebugProxy :IDebugProxy {
    override fun changeServer() {

    }

    override fun disable() {

    }

    override fun debugConfig(): DebugConfig? {
        return null
    }

    override fun quickLogin(id: String, pwd: String) {

    }

    override fun getIcon(): Int {
        return R.mipmap.ic_launcher
    }
}
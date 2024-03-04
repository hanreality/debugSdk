package com.melot.android.lib.debugsdk

import com.melot.android.debug.sdk.kit.hint.*
import com.melot.android.debug.sdk.proxy.DebugConfig
import com.melot.android.debug.sdk.proxy.IDebugProxy
import com.tencent.mmkv.MMKV

/**
 * Author: han.chen
 * Time: 2021/12/3 10:49
 */
class SampleDebugProxy :IDebugProxy {
    override fun changeServer() {

    }

    override fun disable() {

    }

    override fun debugConfig(): DebugConfig {
        return DebugConfig("", true, true, false, false, "kk")
    }

    override fun quickLogin(id: String, pwd: String) {

    }

    override fun getIcon(): Int {
        return R.mipmap.ic_launcher
    }

    override fun checkHint(key: String) {

    }

    override fun getMMKV(): MMKV? {
        return MMKV.defaultMMKV()
    }

    override fun hitKitConfig(): ArrayList<String> {
        return ArrayList<String>().run {
            add(KK_STATISTIC)
            add(KK_CHANNEL)
            add(KK_SHOW_LOG)
            add(KK_CDN)
            add(KK_URL)
            add(KK_AGORA)
            add(KK_URTC)
            add(KK_MINI)
            add(KK_TEST)
            add(KK_SWITCH)
            add(KK_EXAMINE)
            add(KK_SCAN)
            add(MS_ROOM)
            this
        }
    }

}
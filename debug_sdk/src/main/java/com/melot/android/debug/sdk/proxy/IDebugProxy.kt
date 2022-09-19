package com.melot.android.debug.sdk.proxy

/**
 * Author: han.chen
 * Time: 2021/9/9 17:10
 */
interface IDebugProxy {
    fun changeServer()

    fun disable()

    fun debugConfig(): DebugConfig?

    fun quickLogin(id: String, pwd: String)

    fun getIcon(): Int

    fun checkHint(key: String)

    fun mmkvSpKey() :String

    fun hitKitConfig(): ArrayList<String>
}
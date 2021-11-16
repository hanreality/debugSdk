package com.melot.android.debug.sdk.proxy

import com.melot.android.debug.sdk.model.DebugLoginModel

/**
 * Author: han.chen
 * Time: 2021/9/9 17:10
 */
interface IDebugProxy {
    fun changeServer()

    fun disable()

    fun debugConfig(): DebugConfig?

    fun quickLogin(id: String, pwd: String)

    fun getTestAccounts() :ArrayList<DebugLoginModel>

    fun getIcon(): Int
}
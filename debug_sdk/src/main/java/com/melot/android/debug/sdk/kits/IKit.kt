package com.melot.android.debug.sdk.kits

import android.content.Context
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

/**
 * Author: han.chen
 * Time: 2021/12/3 12:33
 */
interface IKit {
    /**
     * 分类
     */
    val category: Int

    @get:StringRes
    var name: Int

    @get:DrawableRes
    val icon:Int

    /**
     * 初始化
     */
    fun onAppInit(context: Context?)
}
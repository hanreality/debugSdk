package com.melot.android.debug.sdk.kit

import android.app.Activity
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
    val name: Int

    @get:DrawableRes
    val icon:Int

    /**
     * 初始化
     */
    fun onAppInit(context: Context?)

    /**
     * 点击回调 带返回值
     * @return true 隐藏面板 false 不隐藏面板
     */
    fun onClick(activity: Activity?): Boolean {
        return true
    }
}
package com.melot.android.debug.sdk.core

import android.view.ViewGroup
import android.view.WindowManager

/**
 * Author: han.chen
 * Time: 2021/12/3 15:57
 */
object MsKitViewLayoutParams {

    /**
     * 悬浮窗不能获取焦点
     */
    @JvmStatic
    var FLAG_NOT_FOCUSABLE = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE

    @JvmStatic
    var FLAG_NOT_TOUCHABLE = WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE

    /**
     * wiki:https://blog.csdn.net/hnlgzb/article/details/108520716
     * 是否允许超出屏幕
     */
    @JvmStatic
    var FLAG_LAYOUT_NO_LIMITS = WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS

    /**
     * 悬浮窗不能获取焦点并且不相应触摸
     */
    @JvmStatic
    var FLAG_NOT_FOCUSABLE_AND_NOT_TOUCHABLE =
        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE

    @JvmStatic
    var MATCH_PARENT = ViewGroup.LayoutParams.MATCH_PARENT

    @JvmStatic
    var WRAP_CONTENT = ViewGroup.LayoutParams.WRAP_CONTENT


    /**
     * 只针对系统悬浮窗起作用 值基本上为以上2个
     */
    var flags = 0

    /**
     * 只针对系统悬浮窗起作用 值基本上为Gravity
     */
    var gravity = 0
    var x = 0
    var y = 0
    var width = 0
    var height = 0

    override fun toString(): String {
        return "MsKitViewLayoutParams{" +
                "flags=" + flags +
                ", gravity=" + gravity +
                ", x=" + x +
                ", y=" + y +
                ", width=" + width +
                ", height=" + height +
                '}'
    }
}
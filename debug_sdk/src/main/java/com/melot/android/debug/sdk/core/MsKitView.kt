package com.melot.android.debug.sdk.core

import android.content.Context
import android.view.View
import android.widget.FrameLayout

/**
 * Author: han.chen
 * Time: 2021/12/3 15:54
 */
interface MsKitView {

    fun onCreate(context: Context)
    fun onCreateView(context: Context?, rootView: FrameLayout?): View?
    fun onViewCreated(rootView: FrameLayout?)
    fun onResume()
    fun onPause()
    /**
     * 确定系统悬浮窗浮标的初始位置
     * LayoutParams创建完以后调用
     *
     * @param params
     */
    fun initMsKitViewLayoutParams(params: MsKitViewLayoutParams)
    fun onEnterBackground()
    fun onEnterForeground()
    /**
     * 浮标控件是否可以拖动
     *
     * @return
     */
    fun canDrag(): Boolean
    /**
     * 是否需要自己处理返回键
     *
     * @return
     */
    fun shouldDealBackKey(): Boolean
    /**
     * shouldDealBackKey == true 时调用
     */
    fun onBackPressed(): Boolean
    /**
     * 悬浮窗主动销毁时调用 不能在当前生命周期回调函数中调用 detach自己 否则会出现死循环
     */
    fun onDestroy()
}
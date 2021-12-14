package com.melot.android.debug.sdk.model

import android.view.Window

/**
 * Author: han.chen
 * Time: 2021/12/14 09:58
 */
class ViewWindow(var window: Window)  {

    override fun toString(): String {
        return window.javaClass.simpleName + "@" + Integer.toHexString(hashCode())
    }
}
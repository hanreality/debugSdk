package com.melot.android.debug.sdk.model

import android.view.View

/**
 * Author: han.chen
 * Time: 2021/12/14 09:58
 */
class ViewWindow(var decorView: View)  {

    override fun toString(): String {
        return decorView.javaClass.simpleName + "@" + Integer.toHexString(hashCode())
    }
}
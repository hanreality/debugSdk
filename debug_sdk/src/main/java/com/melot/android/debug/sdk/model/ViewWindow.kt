package com.melot.android.debug.sdk.model

import android.view.View

/**
 * Author: han.chen
 * Time: 2021/12/14 09:58
 */
class ViewWindow(var decorView: View) {
    var selected = false
    override fun toString(): String {
        return (if (selected) "*" else "") + decorView.toString()
    }
}
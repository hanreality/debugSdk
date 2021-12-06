package com.melot.android.debug.sdk.util

import android.app.Activity
import android.view.View
import java.lang.ref.WeakReference

/**
 * Author: han.chen
 * Time: 2021/10/12 10:32
 */
object DevelopUtil {
    fun isNull(any: Any?, what: String) {
        assert(any != null) {
            throw NullPointerException("$what cannot be null!")
        }
    }
}
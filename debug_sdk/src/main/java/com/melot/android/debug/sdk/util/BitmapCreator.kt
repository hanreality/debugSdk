package com.melot.android.debug.sdk.util

import android.graphics.Bitmap

/**
 * Author: han.chen
 * Time: 2021/10/12 14:43
 */
object BitmapCreator {
    fun create(w: Int, h: Int, config: Bitmap.Config?): Bitmap? {
        DevelopUtil.isNull(config, "config")
        if (w <= 0) {
            IllegalArgumentException("w can not be <= 0").printStackTrace()
            return null
        }
        if (h <= 0) {
            IllegalArgumentException("h can not be <= 0").printStackTrace()
            return null
        }
        for (i in 0..2) {
            try {
                return Bitmap.createBitmap(w, h, config!!)
            } catch (e: Throwable) {
                Runtime.getRuntime().gc()
            }
        }
        return null
    }
}

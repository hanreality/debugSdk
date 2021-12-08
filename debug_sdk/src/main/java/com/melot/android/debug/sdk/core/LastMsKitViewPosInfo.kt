package com.melot.android.debug.sdk.core

import com.melot.android.debug.sdk.util.DevelopUtil
import com.melot.android.debug.sdk.util.DevelopUtil.getAppScreenHeight
import com.melot.android.debug.sdk.util.DevelopUtil.getAppScreenWidth


/**
 * Author: han.chen
 * Time: 2021/12/6 11:16
 */

class LastMsKitViewPosInfo {
    var isPortrait = DevelopUtil.isPortrait()
    var msKitViewWidth = 0
    var msKitViewHeight = 0
    var leftMarginPercent = 0f
        set(value) {
            field = value / getAppScreenWidth().toFloat()
        }
    var topMarginPercent = 0f
        set(value) {
            field = value / getAppScreenHeight().toFloat()
        }

    override fun toString(): String {
        return "LastMsKitViewPosInfo{" +
                "isPortrait=" + isPortrait +
                ", leftMarginPercent=" + leftMarginPercent +
                ", topMarginPercent=" + topMarginPercent +
                '}'
    }
}
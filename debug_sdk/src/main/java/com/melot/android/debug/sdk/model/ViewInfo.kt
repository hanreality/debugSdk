package com.melot.android.debug.sdk.model

import android.graphics.Rect
import android.view.View
import com.melot.android.debug.sdk.util.DevelopUtil

/**
 * Author: han.chen
 * Time: 2021/12/8 19:32
 */
class ViewInfo(view: View) {
    private val DRAW_TIME_LEVEL_NUM = 4
    private val DRAW_TIME_LEVEL_GAP = 5
    var id: String
    var viewRect: Rect
    var drawTime = 0f
    var layerNum = 0

    init {
        viewRect = getViewRect(view)
        id = DevelopUtil.getIdText(view)
    }

    fun getDrawTimeLevel(): Int {
        return drawTime.toInt() / DRAW_TIME_LEVEL_GAP * 255 / DRAW_TIME_LEVEL_NUM
    }

    private fun getViewRect(view: View): Rect {
        val rect = Rect()
        val locations = IntArray(2)
        view.getLocationOnScreen(locations)
        rect.left = locations[0]
        rect.top = locations[1]
        if (DevelopUtil.isStatusBarVisible(view.context)) {
            rect.top -= DevelopUtil.getStatusBarHeight()
        }
        rect.right = rect.left + view.width
        rect.bottom = rect.top + view.height
        return rect
    }


}
package com.melot.android.debug.sdk.config

import com.melot.android.debug.sdk.util.MMKVUtil

/**
 * Author: han.chen
 * Time: 2021/12/6 15:47
 */
object FloatIconConfig {
    const val FLOAT_ICON_POS_X = "float_icon_pos_x"
    const val FLOAT_ICON_POS_Y = "float_icon_pos_y"

    @JvmStatic
    fun getLastPosX(): Int {
        return MMKVUtil.getInt(FLOAT_ICON_POS_X)
    }
    @JvmStatic
    fun getLastPosY(): Int {
        return MMKVUtil.getInt(FLOAT_ICON_POS_Y)
    }
    @JvmStatic
    fun saveLastPosY(y: Int) {
        MMKVUtil.setInt(FLOAT_ICON_POS_Y, y)
    }
    @JvmStatic
    fun saveLastPosX(x: Int) {
        MMKVUtil.setInt(FLOAT_ICON_POS_X, x)
    }
}
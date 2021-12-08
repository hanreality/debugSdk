package com.melot.android.debug.sdk.kit.toolpanel

import com.melot.android.debug.sdk.kit.AbstractKit

/**
 * Author: han.chen
 * Time: 2021/12/7 15:11
 */
data class KitWrapItem(
    var itemType: Int,
    var name: String,
    var checked: Boolean = false,
    var groupName: String = "",
    var kit: AbstractKit?
) : Cloneable {
    companion object {
        const val TYPE_TITLE = 999
        const val TYPE_KIT = 201
        const val TYPE_MODE = 202
        const val TYPE_EXIT = 203
        const val TYPE_VERSION = 204
    }

    override fun clone(): KitWrapItem {
        val item = super.clone() as KitWrapItem
        item.checked = this.checked
        item.groupName = this.groupName
        return item
    }
}

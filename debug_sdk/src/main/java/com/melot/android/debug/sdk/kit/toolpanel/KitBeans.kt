package com.melot.android.debug.sdk.kit.toolpanel

data class KitGroupBean(var groupId: String, var kits: MutableList<KitBean>)
data class KitBean(var allClassName: String, var checked: Boolean, var innerKitId: String = "")

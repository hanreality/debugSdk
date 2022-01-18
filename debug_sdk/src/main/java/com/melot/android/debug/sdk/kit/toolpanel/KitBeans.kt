package com.melot.android.debug.sdk.kit.toolpanel

import androidx.annotation.Keep

@Keep
data class KitGroupBean(var groupId: String, var kits: MutableList<KitBean>)
@Keep
data class KitBean(var allClassName: String, var checked: Boolean, var innerKitId: String = "")

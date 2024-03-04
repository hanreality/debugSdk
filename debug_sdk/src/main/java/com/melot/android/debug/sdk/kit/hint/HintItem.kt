package com.melot.android.debug.sdk.kit.hint

import androidx.annotation.DrawableRes
import androidx.annotation.Keep


const val KK_STATISTIC = "kk-statistic"
const val KK_CHANNEL = "kk-channel"
const val KK_SHOW_LOG = "kk-showlog"
const val KK_CDN = "kk-cdn"
const val KK_URL = "kk-url"
const val KK_AGORA = "kk-agora"
const val KK_URTC = "kk-urtc"
const val KK_MINI = "kk-mini"
const val KK_TEST = "kk-test"
const val KK_SWITCH = "kk-switch"
const val KK_EXAMINE = "kk-examine"
const val KK_SCAN = "kk-scan"
const val MS_MORE = "ms-more"
const val MS_ROOM = "ms-room"

@Keep
data class HintItem(val name:String, @DrawableRes val resourceId: Int, val hintKey:String)
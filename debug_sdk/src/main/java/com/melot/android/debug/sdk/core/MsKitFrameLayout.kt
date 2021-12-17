package com.melot.android.debug.sdk.core

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import com.melot.android.debug.sdk.util.DevelopUtil

/**
 * Author: han.chen
 * Time: 2021/12/6 11:40
 */
open class MsKitFrameLayout @JvmOverloads constructor(
    context: Context,
    flag: Int,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr), MsKitViewInterface {
    companion object {
        val MsKitFrameLayoutFlag_ROOT = 100
        val MsKitFrameLayoutFlag_CHILD = 200
    }

    private var mFlag = MsKitFrameLayoutFlag_ROOT

    init {
        mFlag = flag
    }
}
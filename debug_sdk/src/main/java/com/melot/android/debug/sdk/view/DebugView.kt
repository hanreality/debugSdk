package com.melot.android.debug.sdk.view

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.RelativeLayout
import androidx.fragment.app.FragmentActivity
import com.melot.android.debug.sdk.DebugManager
import com.melot.android.debug.sdk.R

/**
 * Author: han.chen
 * Time: 2021/9/9 16:18
 */
class DebugView @JvmOverloads constructor(
    context: Context?,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : RelativeLayout(context, attrs, defStyleAttr) {

    private val debugMenuButton: DebugMenuButton

    init {
        View.inflate(context, R.layout.layout_debug_view, this)
        debugMenuButton = findViewById(R.id.iv_debug_button)
        debugMenuButton.setOnClickListener {
            showDebugDialog()
        }
    }

    private fun showDebugDialog() {
        (DebugManager.INSTANCE.currentActivity as? FragmentActivity)?.let {
            val debugDialog = DebugActionView()
            debugDialog.show(it.supportFragmentManager, "debugActionView")
        }
    }
}
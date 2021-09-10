package com.melot.android.debug.sdk.view

import android.app.ActivityManager
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.util.AttributeSet
import android.view.View
import android.widget.RelativeLayout
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
    private var debugDialog: AlertDialog? = null

    init {
        View.inflate(context, R.layout.layout_debug_view, this)
        debugMenuButton = findViewById(R.id.iv_debug_button)
        debugMenuButton.setOnClickListener {
            showDebugDialog()
        }
    }

    private fun showDebugDialog() {
        val items = arrayOf("切换服务器", "切换语言", "取消")
        debugDialog = AlertDialog.Builder(DebugManager.INSTANCE.currentActivity)
            .setTitle("调试工具")
            .setItems(items) { dialog, which ->
                when (which) {
                    0 -> {
                        DebugManager.INSTANCE.debugProxy?.changeServer()
                    }
                    1 -> {
                        switchLanguage()
                    }
                }
            }.create()
        debugDialog?.show()
    }

    private fun switchLanguage() {
        DebugManager.INSTANCE.currentActivity?.startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
    }
}
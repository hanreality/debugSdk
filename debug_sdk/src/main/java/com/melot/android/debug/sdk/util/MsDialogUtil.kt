package com.melot.android.debug.sdk.util

import android.app.AlertDialog
import android.content.Context
import android.text.InputType
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.melot.android.debug.sdk.MsKit
import com.melot.android.debug.sdk.MsKit.install
import com.melot.android.debug.sdk.MsKit.installed
import com.melot.android.debug.sdk.MsKit.isShow
import com.melot.android.debug.sdk.MsKit.show
import com.melot.android.debug.sdk.R

typealias Funtionx = (view:View) -> Unit
object MsDialogUtil {

    @JvmStatic
    fun getInputDialog(
        context: Context,
        title: String,
        message: String?,
        hint: String?,
        inputType: Int,
        positiveText: String?,
        positiveListener: Funtionx,
        negativeText: String?,
        negativeListener: Funtionx,
        closeDialogListener:Funtionx
    ): View {
        val view = LayoutInflater.from(context).inflate(R.layout.ms_input_dialog_layout, null)
        val titleView = view.findViewById<TextView>(R.id.dialog_title)
        val messageView = view.findViewById<TextView>(R.id.dialog_message)
        val editView = view.findViewById<TextView>(R.id.dialog_edit)
        val okButton = view.findViewById<TextView>(R.id.ok_button)
        val cancelButton = view.findViewById<TextView>(R.id.cancel_button)
        val closeDialog = view.findViewById<View>(R.id.close_dialog)

        titleView.text = title
        messageView.text = message
        editView.inputType = inputType
        editView.hint = hint
        okButton.text = positiveText
        okButton.setOnClickListener {
            it.tag = if (editView.text == null) "" else editView.text.toString()
            positiveListener.invoke(it)
        }
        cancelButton.text = negativeText
        cancelButton.setOnClickListener {
            negativeListener.invoke(it)
        }
        closeDialog.setOnClickListener {
            closeDialogListener.invoke(it)
        }
        return view
    }

    @JvmStatic
    fun showDebugToolDialog(context: Context) {
        ActivityUtils.getTopActivity()?.run {
            val dialog = AlertDialog.Builder(this).create()
            val view = getInputDialog(
                this,
                "开启调试小圆球",
                "来尝试一下小圆球的功能吧", "请输入密码",
                InputType.TYPE_TEXT_VARIATION_PASSWORD,
                "确认",
                {
                    val content = it.tag as String
                    val encrypt = AppUtils.shaEncrypt(content)
                    if (TextUtils.equals(encrypt, MsKit.encrypt)) {
                        if (isShow) {
                            return@getInputDialog
                        }
                        MMKVUtil.setBoolean("kk_debug_tool_enable", true)
                        if (!installed) {
                            install()
                        }
                        show()
                        Toast.makeText(context, "小圆球已开启", Toast.LENGTH_LONG).show()
                        dialog.dismiss()
                    } else {
                        Toast.makeText(context, "密码错误", Toast.LENGTH_LONG).show()
                    }
                },
                context.resources.getString(
                    R.string.ms_dont_open_debug_tool,
                    if (MsKit.getProxy()?.debugConfig()?.serverDebug == true) "正式" else "测试"
                ),
                {
                    MsKit.getProxy()?.changeServer()
                    dialog.dismiss()
                },{
                    dialog.dismiss()
                })
            dialog.setView(view)
            dialog.setCanceledOnTouchOutside(false)
            dialog.show()
        }
    }
}
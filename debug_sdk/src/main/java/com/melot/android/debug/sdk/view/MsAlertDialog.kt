package com.melot.android.debug.sdk.view

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import com.melot.android.debug.sdk.R

class MsAlertDialog(context: Context) : Dialog(context) {

    private val view = LayoutInflater.from(context).inflate(R.layout.ms_input_dialog_layout, null)
    private val titleView = view.findViewById<TextView>(R.id.dialog_title)
    private val messageView = view.findViewById<TextView>(R.id.dialog_message)
    private val editView = view.findViewById<TextView>(R.id.dialog_edit)
    private val okButton = view.findViewById<TextView>(R.id.ok_button)
    private val cancelButton = view.findViewById<TextView>(R.id.cancel_button)
    private val closeDialog = view.findViewById<View>(R.id.close_dialog)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.ms_input_dialog_layout)
        initView()
    }

    fun initView() {

    }
}
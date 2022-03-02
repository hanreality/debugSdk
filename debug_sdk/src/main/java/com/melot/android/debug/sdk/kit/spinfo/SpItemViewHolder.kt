package com.melot.android.debug.sdk.kit.spinfo

import android.content.Context
import android.text.InputType
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.dialog.MaterialDialogs
import com.melot.android.debug.sdk.R
import com.melot.android.debug.sdk.util.MMKVUtil

/**
 * Author: han.chen
 * Time: 2022/1/7 17:03
 */
class SpItemViewHolder(var context: Context?, parent: ViewGroup?) : RecyclerView.ViewHolder(
    LayoutInflater.from(context).inflate(R.layout.ms_sp_item_view, parent, false)
) {

    private val spKeyValue = itemView.findViewById<TextView>(R.id.sp_key_value)
    private val spType = itemView.findViewById<TextView>(R.id.sp_type)
    private val spInput = itemView.findViewById<EditText>(R.id.sp_value_input)
    private val spDelete = itemView.findViewById<TextView>(R.id.sp_delete)
    private val spModify = itemView.findViewById<TextView>(R.id.sp_modify)

    private var isInEditMode = false

    fun bindData(item: SpItem, position: Int, listener: SpManagerPageMsKitView.SpClickListener?) {
        spKeyValue.text = "[${item.key}，${item.value}]"
        spType.text = item.type
        spDelete.setOnClickListener {
            listener?.onDelete(item, position)
        }
        spModify.setOnClickListener {
            if (!isInEditMode) {
                isInEditMode = true
                spInput.visibility = View.VISIBLE
                spModify.text = "确定"
            } else {
                if (!TextUtils.isEmpty(spInput.text)) {
                    item.value = spInput.text
                    listener?.onModify(item, position)
                }
                isInEditMode = false
                spInput.visibility = View.GONE
                spModify.text = "修改"
            }
        }
        spInput.visibility = if (isInEditMode) View.VISIBLE else View.GONE
        when(item.type) {
            "String"-> {
                spInput.inputType = InputType.TYPE_CLASS_TEXT
            }
            "int","long"-> {
                spInput.inputType = InputType.TYPE_CLASS_NUMBER
            }
            "double", "float"->{
                spInput.inputType = InputType.TYPE_NUMBER_FLAG_DECIMAL
            }
        }
    }
}
package com.melot.android.debug.sdk.kit.toolpanel

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.RadioGroup
import com.melot.android.debug.sdk.R
import com.melot.android.debug.sdk.base.BaseViewHolder
import com.melot.android.debug.sdk.constant.SharedPrefsKey
import com.melot.android.debug.sdk.util.AppUtils
import com.melot.android.debug.sdk.util.MMKVUtil

/**
 * Author: han.chen
 * Time: 2021/12/7 16:04
 */
class ToolPanelModeViewHolder(context: Context?, container: ViewGroup) : BaseViewHolder<KitWrapItem>(
    LayoutInflater.from(context).inflate(R.layout.ms_item_group_mode, container, false)
) {
    private val radioGroup = itemView.findViewById<RadioGroup>(R.id.rb_group)
    private val rbNormal = itemView.findViewById<RadioButton>(R.id.rb_normal)
    private val rbSystem = itemView.findViewById<RadioButton>(R.id.rb_system)

    override fun bindData(data: KitWrapItem) {
        radioGroup.setOnCheckedChangeListener { _, checkedId ->
            if (checkedId == R.id.rb_normal) {
                //选中normal
                MMKVUtil.setString(SharedPrefsKey.FLOAT_START_MODE, "normal")
            } else {
                //选中系统
                MMKVUtil.setString(SharedPrefsKey.FLOAT_START_MODE, "system")
            }
        }

        rbNormal.setOnClickListener {
            rbNormal.postDelayed({
                AppUtils.relaunchApp(true)
            }, 500)
        }

        rbSystem.setOnClickListener {
            rbSystem.postDelayed({
                AppUtils.relaunchApp(true)
            }, 500)
        }

        val floatMode = MMKVUtil.getString(SharedPrefsKey.FLOAT_START_MODE, "normal")
        if (floatMode == "normal") {
            rbNormal.isChecked = true
        } else {
            rbSystem.isChecked = true
        }
    }
}
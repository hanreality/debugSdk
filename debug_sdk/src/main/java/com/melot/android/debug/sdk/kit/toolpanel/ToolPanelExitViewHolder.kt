package com.melot.android.debug.sdk.kit.toolpanel

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.melot.android.debug.sdk.MsKit
import com.melot.android.debug.sdk.R
import com.melot.android.debug.sdk.base.BaseViewHolder

/**
 * Author: han.chen
 * Time: 2021/12/7 16:04
 */
class ToolPanelExitViewHolder(context: Context?, container: ViewGroup) : BaseViewHolder<KitWrapItem>(
    LayoutInflater.from(context).inflate(R.layout.ms_item_group_exit, container, false)
) {
    private val close: TextView = itemView.findViewById(R.id.close)

    override fun bindData(data: KitWrapItem) {
        close.setOnClickListener {
            MsKit.hideToolPanel()
            MsKit.hide()
        }
    }
}
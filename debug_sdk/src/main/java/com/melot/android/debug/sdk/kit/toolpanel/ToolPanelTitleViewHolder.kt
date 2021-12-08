package com.melot.android.debug.sdk.kit.toolpanel

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import com.melot.android.debug.sdk.R
import com.melot.android.debug.sdk.base.BaseViewHolder

/**
 * Author: han.chen
 * Time: 2021/12/7 16:04
 */
class ToolPanelTitleViewHolder(context: Context?, container: ViewGroup) : BaseViewHolder<KitWrapItem>(
    LayoutInflater.from(context).inflate(R.layout.ms_item_group_title, container, false)
) {
    private val titleView: TextView = itemView.findViewById(R.id.tv_title_name)

    override fun bindData(data: KitWrapItem) {
        titleView.text = data.name
    }
}
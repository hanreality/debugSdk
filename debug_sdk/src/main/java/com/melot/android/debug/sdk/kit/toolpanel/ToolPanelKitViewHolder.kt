package com.melot.android.debug.sdk.kit.toolpanel

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.melot.android.debug.sdk.R
import com.melot.android.debug.sdk.base.BaseViewHolder

/**
 * Author: han.chen
 * Time: 2021/12/7 16:04
 */
class ToolPanelKitViewHolder(var context: Context?, container: ViewGroup) : BaseViewHolder<KitWrapItem>(
    LayoutInflater.from(context).inflate(R.layout.ms_item_kit, container, false)
) {
    private val icon: ImageView = itemView.findViewById(R.id.icon)
    private val name: TextView = itemView.findViewById(R.id.name)

    override fun bindData(data: KitWrapItem) {
        data.kit?.let {
            icon.setImageResource(it.icon)
            name.text = context?.getString(it.name)
        }

    }
}
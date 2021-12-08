package com.melot.android.debug.sdk.kit.toolpanel

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.melot.android.debug.sdk.base.BaseViewHolder

/**
 * Author: han.chen
 * Time: 2021/12/7 16:02
 */
class ToolPanelAdapter(var context: Context?, var kitViews: MutableList<KitWrapItem>?) :
    RecyclerView.Adapter<BaseViewHolder<KitWrapItem>>() {
    var mItemClickListener: OnItemClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<KitWrapItem> {
        return when (viewType) {
            KitWrapItem.TYPE_EXIT -> ToolPanelExitViewHolder(context, parent)
            KitWrapItem.TYPE_TITLE -> ToolPanelTitleViewHolder(context, parent)
            KitWrapItem.TYPE_KIT -> ToolPanelKitViewHolder(context, parent)
            else -> ToolPanelTitleViewHolder(context, parent)
        }
    }

    override fun onBindViewHolder(holder: BaseViewHolder<KitWrapItem>, position: Int) {
        kitViews?.get(position)?.let {
            holder.bindData(it)
            holder.itemView.setOnClickListener { _ ->
                mItemClickListener?.onItemClickListener(it)
            }
        }
    }

    override fun getItemCount(): Int {
        return kitViews?.size ?: 0
    }

    override fun getItemViewType(position: Int): Int {
        val kitView = kitViews?.get(position)
        return kitView?.itemType ?: super.getItemViewType(position)
    }

    interface OnItemClickListener {
        fun onItemClickListener(item: KitWrapItem)
    }
}
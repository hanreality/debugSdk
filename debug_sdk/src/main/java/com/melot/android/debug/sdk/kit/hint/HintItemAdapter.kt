package com.melot.android.debug.sdk.kit.hint

import android.content.Context
import android.text.InputType
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.melot.android.debug.sdk.R
import com.melot.android.debug.sdk.util.MMKVUtil

/**
 * Author: han.chen
 * Time: 2022/1/7 17:02
 */
class HintItemAdapter(var context: Context?, var items: ArrayList<HintItem>) :
    RecyclerView.Adapter<HintItemViewHolder>() {

    var listener: HintManagerPageMsKitView.HintClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HintItemViewHolder {
        return HintItemViewHolder(context, parent)
    }

    override fun onBindViewHolder(holder: HintItemViewHolder, position: Int) {
        holder.bindData(item = items[position], position = position, listener = listener)
    }

    override fun getItemCount(): Int {
        return items.size
    }


}
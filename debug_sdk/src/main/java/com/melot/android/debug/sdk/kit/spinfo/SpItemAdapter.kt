package com.melot.android.debug.sdk.kit.spinfo

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
class SpItemAdapter(var context: Context?, var items: ArrayList<SpItem>) :
    RecyclerView.Adapter<SpItemViewHolder>() {

    private val listener: SpManagerPageMsKitView.SpClickListener =
        object : SpManagerPageMsKitView.SpClickListener {
            override fun onDelete(item: SpItem, position: Int) {
                MMKVUtil.removeData(item.key)
                items.removeAt(position)
                notifyItemRemoved(position)
                notifyItemRangeChanged(position, items.size)
            }

            override fun onModify(item: SpItem, position: Int) {
                try {
                    when (item.type) {
                        "String" -> {
                            MMKVUtil.setString(item.key, item.value.toString())
                        }
                        "int" -> {
                            MMKVUtil.setInt(item.key, item.value as Int)
                        }
                        "long" -> {
                            MMKVUtil.setLong(item.key, item.value as Long)
                        }
                        "double", "float" -> {
                            MMKVUtil.setFloat(item.key, item.value as Float)
                        }
                    }
                    notifyItemChanged(position)
                } catch (e: Exception) {
                    Toast.makeText(
                        context,
                        context?.getText(R.string.ms_sp_error),
                        Toast.LENGTH_SHORT
                    ).show()
                    e.printStackTrace()
                }
            }

        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SpItemViewHolder {
        return SpItemViewHolder(context, parent)
    }

    override fun onBindViewHolder(holder: SpItemViewHolder, position: Int) {
        holder.bindData(item = items[position], position = position, listener = listener)
    }

    override fun getItemCount(): Int {
        return items.size
    }


}
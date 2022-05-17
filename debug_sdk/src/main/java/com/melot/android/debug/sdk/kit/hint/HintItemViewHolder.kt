package com.melot.android.debug.sdk.kit.hint

import android.content.Context
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.melot.android.debug.sdk.MsKit
import com.melot.android.debug.sdk.R
import com.melot.android.debug.sdk.util.MMKVUtil


/**
 * Author: han.chen
 * Time: 2022/1/7 17:03
 */
class HintItemViewHolder(var context: Context?, parent: ViewGroup?) : RecyclerView.ViewHolder(
    LayoutInflater.from(context).inflate(R.layout.ms_hint_item_view, parent, false)
) {

    private val hintName = itemView.findViewById<TextView>(R.id.hint_name)
    private val hintIcon = itemView.findViewById<ImageView>(R.id.hint_icon)

    fun bindData(item: HintItem, position: Int, listener: HintManagerPageMsKitView.HintClickListener?) {
        hintName.text = item.name
        hintIcon.setImageResource(item.resourceId)
        when(item.hintKey) {
            KK_AGORA, KK_URTC -> {
                val key = if (TextUtils.equals(item.hintKey, KK_AGORA)) "forceAgora" else "forceUrtc"
                val force = MMKVUtil.getBoolean(key, false)
                val matrix = ColorMatrix()
                matrix.setSaturation(if (force) 1f else 0f)
                val filter = ColorMatrixColorFilter(matrix)
                hintIcon.colorFilter = filter
                hintName.text = "${item.name}(${force})"
            }
            KK_CDN-> {
                val force = !MMKVUtil.getBoolean(
                    "force_not_cdn",
                    MsKit.getProxy()?.debugConfig()?.forceNotCDN ?: true
                )
                val matrix = ColorMatrix()
                matrix.setSaturation(if (force) 1f else 0f)
                val filter = ColorMatrixColorFilter(matrix)
                hintIcon.colorFilter = filter
                hintName.text = "${item.name}(${force})"
            }
            KK_SHOW_LOG -> {
                val opened = MsKit.getProxy()?.debugConfig()?.logDebug?: true
                val matrix = ColorMatrix()
                matrix.setSaturation(if (opened) 1f else 0f)
                val filter = ColorMatrixColorFilter(matrix)
                hintIcon.colorFilter = filter
                hintName.text = "${item.name}(${opened})"
            }
            KK_EXAMINE -> {
                val isExamine = MsKit.getProxy()?.debugConfig()?.isExamine ?: false
                val matrix = ColorMatrix()
                matrix.setSaturation(if (isExamine) 1f else 0f)
                val filter = ColorMatrixColorFilter(matrix)
                hintIcon.colorFilter = filter
                hintName.text = "${item.name}(${isExamine})"
            }
        }
        itemView.setOnClickListener {
           listener?.onClick(item, position)
        }
    }
}
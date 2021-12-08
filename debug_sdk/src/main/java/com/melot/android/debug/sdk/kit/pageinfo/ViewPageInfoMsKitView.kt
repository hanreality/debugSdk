package com.melot.android.debug.sdk.kit.pageinfo

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import com.melot.android.debug.sdk.MsKit
import com.melot.android.debug.sdk.R
import com.melot.android.debug.sdk.core.AbsMsKitView
import com.melot.android.debug.sdk.core.MsKitViewLayoutParams
import com.melot.android.debug.sdk.util.DevelopUtil
import com.melot.android.debug.sdk.util.ActivityUtils

/**
 * Author: han.chen
 * Time: 2021/12/7 19:57
 */
class ViewPageInfoMsKitView : AbsMsKitView(), View.OnClickListener {
    private var info: TextView? = null
    private var close: ImageView? = null
    override fun onCreate(context: Context) {

    }

    override fun onCreateView(context: Context?, rootView: FrameLayout?): View? {
        return LayoutInflater.from(context).inflate(R.layout.ms_float_view_page_info, null)
    }

    override fun onViewCreated(rootView: FrameLayout?) {
        info = findViewById(R.id.info)
        close = findViewById(R.id.close)
        close?.setOnClickListener(this)
    }

    override fun initMsKitViewLayoutParams(params: MsKitViewLayoutParams) {
        params.flags = MsKitViewLayoutParams.FLAG_NOT_FOCUSABLE
        params.x = 0
        params.y = DevelopUtil.getAppScreenHeight() - DevelopUtil.dp2px(185f)
        params.width = screenShortSideLength
        params.height = MsKitViewLayoutParams.WRAP_CONTENT
    }

    override fun updateViewLayout(tag: String, isActivityBackResume: Boolean) {
        super.updateViewLayout(tag, isActivityBackResume)
        // 由于父类在此方法限制了高度无法自适应，所以重新设成wrap_content以自适应
        val params = normalLayoutParams
        params?.height = ViewGroup.LayoutParams.WRAP_CONTENT
        msKitView?.layoutParams = params
    }

    override fun onClick(v: View?) {
        if (v == close) {
            MsKit.removeFloating(ViewPageInfoMsKitView::class.java)
        }
    }

    override fun onResume() {
        super.onResume()
        ActivityUtils.getTopActivity()?.let {
            val stringBuilder = StringBuilder()
                .append("Activity: ")
                .append(it::class.java.name)

            it.intent.extras?.let { bundle ->
                val extras = StringBuilder()
                bundle.keySet().forEach {
                    extras.append(it).append("=").append(bundle[it]).append(",")
                }
                extras.replace(extras.lastIndex, extras.length, "")
                stringBuilder.append("{Bundle[${extras}]}")
            }
            info?.text = stringBuilder.toString()
        }

    }
}
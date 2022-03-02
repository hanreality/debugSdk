package com.melot.android.debug.sdk.kit.pageinfo

import android.app.Activity
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.melot.android.debug.sdk.MsKit
import com.melot.android.debug.sdk.R
import com.melot.android.debug.sdk.core.AbsMsKitView
import com.melot.android.debug.sdk.core.MsKitViewLayoutParams
import com.melot.android.debug.sdk.util.DevelopUtil
import com.melot.android.debug.sdk.util.ActivityUtils
import com.melot.android.debug.sdk.util.ColorUtil
import com.melot.android.debug.sdk.util.LifecycleListenerUtil

/**
 * Author: han.chen
 * Time: 2021/12/7 19:57
 */
class ViewPageInfoMsKitView : AbsMsKitView(), View.OnClickListener, LifecycleListenerUtil.LifecycleListener {
    private var close: ImageView? = null
    private var activityName : TextView? = null
    private var bundleContainer :ViewGroup? = null
    override fun onCreate(context: Context) {
        LifecycleListenerUtil.registerListener(this)
    }

    override fun onCreateView(context: Context?, rootView: FrameLayout?): View? {
        return LayoutInflater.from(context).inflate(R.layout.ms_float_view_page_info, null)
    }

    override fun onViewCreated(rootView: FrameLayout?) {
        close = findViewById(R.id.close)
        close?.setOnClickListener(this)
        activityName = findViewById(R.id.activity_name)
        activityName?.setBackgroundColor(ColorUtil.getRandomColor())
        activityName?.isSelected = true
        bundleContainer = findViewById(R.id.bundle_container)
        updatePageInfo(ActivityUtils.getTopActivity())
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

    private fun updatePageInfo(activity: Activity?) {
        activity?.run {
            val stringBuilder = StringBuilder()
                .append("Activity: ")
                .append(this::class.java.name)
                .append("\n")
            activityName?.text = this::class.java.name
            getRoute(activity)?.run {
                stringBuilder.append("Route:")
                    .append(this)
                    .append("\n")
            }
            bundleContainer?.removeAllViews()
            this.intent.extras?.also {bundle->
                bundle.keySet().forEach {
                    val item : TextView = LayoutInflater.from(context).inflate(R.layout.ms_page_info_bundle_item, bundleContainer, false) as TextView
                    item.setBackgroundColor(ColorUtil.getRandomColor())
                    item.text = "${it}=${bundle[it]}"
                    bundleContainer?.addView(item)
                }
            }
            if (bundleContainer?.childCount == 0) {
                val item : TextView = LayoutInflater.from(context).inflate(R.layout.ms_page_info_bundle_item, bundleContainer, false) as TextView
                item.setBackgroundColor(ColorUtil.getRandomColor())
                item.text = "no extras"
                bundleContainer?.addView(item)
            }

        }
    }

    private fun getRoute(activity: Activity?) :String? {
        return null
    }

    override fun onActivityResumed(activity: Activity?) {
        if (!isNormalMode) {
            updatePageInfo(activity)
        }
    }

    override fun onActivityPaused(activity: Activity?) {
        
    }

    override fun onFragmentAttached(f: Fragment?) {
        
    }

    override fun onFragmentDetached(f: Fragment?) {
        
    }

    override fun onDestroy() {
        super.onDestroy()
        LifecycleListenerUtil.unregisterListener(this)
    }
}
package com.melot.android.debug.sdk.kit.layoutborder

import android.app.Activity
import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.fragment.app.Fragment
import com.melot.android.debug.sdk.MsKit
import com.melot.android.debug.sdk.R
import com.melot.android.debug.sdk.config.LayoutBorderConfig
import com.melot.android.debug.sdk.core.AbsMsKitView
import com.melot.android.debug.sdk.core.MsKitViewLayoutParams
import com.melot.android.debug.sdk.core.UniversalActivity
import com.melot.android.debug.sdk.util.ActivityUtils
import com.melot.android.debug.sdk.util.DevelopUtil
import com.melot.android.debug.sdk.util.LifecycleListenerUtil

/**
 * Author: han.chen
 * Time: 2021/12/22 15:52
 */
class LayoutBorderMsKitView : AbsMsKitView() {

    private var switchButton: CheckBox? = null
    private var close: View? = null
    private var descContainer: ViewGroup? = null

    override fun onCreate(context: Context) {
        resolveActivity(ActivityUtils.getTopActivity())
        LifecycleListenerUtil.registerListener(lifecycleListener)
    }

    override fun onCreateView(context: Context?, rootView: FrameLayout?): View? {
        return LayoutInflater.from(context)
            .inflate(R.layout.ms_float_layout_border, rootView, false)
    }

    override fun onViewCreated(rootView: FrameLayout?) {
        switchButton = rootView?.findViewById(R.id.switch_btn)
        switchButton?.setOnCheckedChangeListener { buttonView, isChecked ->
            LayoutBorderConfig.sLayoutBorderOpen = isChecked
            if (isChecked) {
                LayoutBorderManager.instance.start()
            } else {
                LayoutBorderManager.instance.stop()
            }
        }
        close = rootView?.findViewById(R.id.close)
        close?.setOnClickListener {
            LayoutBorderConfig.sLayoutBorderOpen = false
            LayoutBorderConfig.sLayoutLevelOpen = false
            MsKit.removeFloating(LayoutBorderMsKitView::class.java)
            LayoutBorderManager.instance.stop()
        }
        descContainer = rootView?.findViewById(R.id.desc_container)
        initDesc()
    }

    override fun initMsKitViewLayoutParams(params: MsKitViewLayoutParams) {
        params.flags = MsKitViewLayoutParams.FLAG_NOT_FOCUSABLE
        params.x = 0
        params.y = DevelopUtil.getAppScreenHeight() - DevelopUtil.dp2px(185f)
        params.width = screenShortSideLength
        params.height = MsKitViewLayoutParams.WRAP_CONTENT
    }

    private fun resolveActivity(activity: Activity?) {
        if (activity is UniversalActivity) {
            return
        }
        val appContentView: ViewGroup? = if (isNormalMode) {
            DevelopUtil.getMsKitAppContentView(activity) as? ViewGroup
        } else {
            activity?.window?.decorView as? ViewGroup
        }
        if (appContentView == null) {
            return
        }

    }

    private val lifecycleListener = object : LifecycleListenerUtil.LifecycleListener {
        override fun onActivityResumed(activity: Activity?) {
            resolveActivity(activity)
        }

        override fun onActivityPaused(activity: Activity?) {

        }

        override fun onFragmentAttached(f: Fragment?) {

        }

        override fun onFragmentDetached(f: Fragment?) {

        }

    }

    private fun initDesc() {
        descContainer?.removeAllViews()
        descContainer?.addView(
            DescItemView(context, descContainer).updateView(
                R.color.ms_color_1,
                "ViewGroup"
            ).itemView
        )
        descContainer?.addView(
            DescItemView(context, descContainer).updateView(
                R.color.ms_color_6,
                "Button"
            ).itemView
        )
        descContainer?.addView(
            DescItemView(context, descContainer).updateView(
                R.color.ms_color_9,
                "TextView"
            ).itemView
        )
        descContainer?.addView(
            DescItemView(context, descContainer).updateView(
                R.color.ms_color_10,
                "Other"
            ).itemView
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        LifecycleListenerUtil.unregisterListener(lifecycleListener)
    }

    class DescItemView(var context: Context?, parent: ViewGroup?) {
        var itemView: View = LayoutInflater.from(context).inflate(
            R.layout.ms_float_layout_border_desc_item, parent, false
        )
        private val descIcon = itemView.findViewById<ImageView>(R.id.desc_color)
        private val descType = itemView.findViewById<TextView>(R.id.desc_type)

        fun updateView(color: Int, type: String): DescItemView {
            context?.resources?.getColor(color)?.let { descIcon.setBackgroundColor(it) }
            descType.text = type
            return this
        }
    }
}
package com.melot.android.debug.sdk.kit.fragmentInfo

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.CheckBox
import android.widget.FrameLayout
import android.widget.ImageView
import com.melot.android.debug.sdk.MsKit
import com.melot.android.debug.sdk.R
import com.melot.android.debug.sdk.core.AbsMsKitView
import com.melot.android.debug.sdk.core.MsKitManager
import com.melot.android.debug.sdk.core.MsKitViewLayoutParams
import com.melot.android.debug.sdk.core.MsKitViewManager
import com.melot.android.debug.sdk.kit.viewcheck.ViewCheckMsKitView
import com.melot.android.debug.sdk.util.ActivityUtils
import com.melot.android.debug.sdk.util.DevelopUtil

/**
 * Author: han.chen
 * Time: 2021/12/8 11:39
 */
class ViewFragmentInfoMsKitView : AbsMsKitView(), View.OnClickListener {

    private var switch: CheckBox? = null
    private var close: ImageView? = null
    private var fragmentInfoView: FragmentInfoView? = null
    private var decorRootView: FrameLayout? = null

    override fun onCreate(context: Context) {
//        fragmentInfoView = FragmentInfoView(context)
    }

    override fun onCreateView(context: Context?, rootView: FrameLayout?): View? {
        return LayoutInflater.from(context).inflate(R.layout.ms_float_fragment_info, null)
    }

    override fun onViewCreated(rootView: FrameLayout?) {
        close = findViewById(R.id.close)
        close?.setOnClickListener(this)
        switch = findViewById(R.id.switch_btn)
        switch?.setOnCheckedChangeListener { _, isChecked ->
            MsKitManager.FRAGMENT_INFO_CHECKED = isChecked
//            fragmentInfoView?.generateSwitch(isChecked)
            MsKit.getMsKitView(activity, ViewFragmentDrawMsKitView::class.java)?.generateSwitch(isChecked)
        }
        switch?.isChecked = MsKitManager.FRAGMENT_INFO_CHECKED
    }

    override fun initMsKitViewLayoutParams(params: MsKitViewLayoutParams) {
        params.flags = MsKitViewLayoutParams.FLAG_NOT_FOCUSABLE
        params.x = 0
        params.y = DevelopUtil.getAppScreenHeight() - DevelopUtil.dp2px(185f)
        params.width = screenShortSideLength
        params.height = MsKitViewLayoutParams.WRAP_CONTENT
    }

    override fun onClick(v: View?) {
        if (v == close) {
            MsKit.removeFloating(ViewFragmentInfoMsKitView::class.java)
            MsKit.removeFloating(ViewFragmentDrawMsKitView::class.java)
        }
    }

    override fun dealDecorRootView(decorRootView: FrameLayout?) {
        super.dealDecorRootView(decorRootView)
//        this.decorRootView = decorRootView
//        decorRootView?.addView(fragmentInfoView, 0)
    }

    override fun onDestroy() {
        MsKitManager.FRAGMENT_INFO_CHECKED = false
//        decorRootView?.removeView(fragmentInfoView)
//        fragmentInfoView = null
        super.onDestroy()
    }

    override fun onResume() {
        super.onResume()
        switch?.isChecked = MsKitManager.FRAGMENT_INFO_CHECKED
    }
}
package com.melot.android.debug.sdk.main

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import com.melot.android.debug.sdk.MsKit
import com.melot.android.debug.sdk.R
import com.melot.android.debug.sdk.config.FloatIconConfig
import com.melot.android.debug.sdk.core.AbsMsKitView
import com.melot.android.debug.sdk.core.MsKitViewLayoutParams

/**
 * Author: han.chen
 * Time: 2021/12/6 15:39
 */
class MainIconMsKitView : AbsMsKitView() {

    init {
        viewProps.edgePinned = false
    }

    private var mainIconView : ImageView? =null

    override fun onCreate(context: Context) {

    }

    override fun onCreateView(context: Context?, rootView: FrameLayout?): View? {
        return LayoutInflater.from(context).inflate(R.layout.ms_main_launch_icon, rootView, false)
    }

    override fun onViewCreated(rootView: FrameLayout?) {
        msKitView?.id = R.id.float_icon_id
        msKitView?.setOnClickListener {
            MsKit.showToolPanel()
        }
        mainIconView = findViewById(R.id.main_icon_view)
        mainIconView?.setImageResource(MsKit.getProxy()?.getIcon() ?: R.drawable.debug_icon)
    }

    override fun initMsKitViewLayoutParams(params: MsKitViewLayoutParams) {
        params.x = FloatIconConfig.getLastPosX()
        params.y = FloatIconConfig.getLastPosY()
        params.width = MsKitViewLayoutParams.WRAP_CONTENT
        params.height = MsKitViewLayoutParams.WRAP_CONTENT
    }

    override fun onResume() {
        super.onResume()
        if (isNormalMode) {
            immInvalidate()
        }
    }
}
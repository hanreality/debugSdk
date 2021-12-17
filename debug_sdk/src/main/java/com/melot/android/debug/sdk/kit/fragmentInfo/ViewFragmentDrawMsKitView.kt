package com.melot.android.debug.sdk.kit.fragmentInfo

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import com.melot.android.debug.sdk.R
import com.melot.android.debug.sdk.core.AbsMsKitView
import com.melot.android.debug.sdk.core.MsKitManager
import com.melot.android.debug.sdk.core.MsKitViewLayoutParams


/**
 * Author: han.chen
 * Time: 2021/12/14 20:34
 */
class ViewFragmentDrawMsKitView : AbsMsKitView() {
    private var mFragmentInfoView: FragmentInfoView? = null
    override fun onCreate(context: Context) {
        mFragmentInfoView = FragmentInfoView(context)
    }

    override fun onCreateView(context: Context?, rootView: FrameLayout?): View? {
        return LayoutInflater.from(context).inflate(R.layout.ms_float_fragment_info_draw, null)
    }

    override fun onViewCreated(rootView: FrameLayout?) {
        setMsKitViewNotResponseTouchEvent(msKitView)
        mFragmentInfoView = rootView?.findViewById(R.id.fragment_info_view)
    }

    override fun initMsKitViewLayoutParams(params: MsKitViewLayoutParams) {
        params.flags = MsKitViewLayoutParams.FLAG_NOT_FOCUSABLE_AND_NOT_TOUCHABLE
        params.width = MsKitViewLayoutParams.MATCH_PARENT
        params.height = MsKitViewLayoutParams.MATCH_PARENT
    }

    fun generateSwitch(isChecked: Boolean) {
        mFragmentInfoView?.generateSwitch(isChecked)
    }

    override fun onDestroy() {
        mFragmentInfoView?.generateSwitch(false)
        super.onDestroy()
    }

    override fun updateViewLayout(tag: String, isActivityBackResume: Boolean) {
        super.updateViewLayout(tag, isActivityBackResume)
        if (!MsKitManager.IS_NORMAL_FLOAT_MODE) {
            postDelayed(Runnable {
                mFragmentInfoView?.updateViewLayout()
            }, 200)

        }
    }
}
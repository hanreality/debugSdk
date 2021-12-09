package com.melot.android.debug.sdk.kit.viewcheck

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import com.melot.android.debug.sdk.MsKit
import com.melot.android.debug.sdk.R
import com.melot.android.debug.sdk.core.AbsMsKitView
import com.melot.android.debug.sdk.core.MsKitViewLayoutParams
import com.melot.android.debug.sdk.model.ViewInfo
import com.melot.android.debug.sdk.util.ActivityUtils

/**
 * Author: han.chen
 * Time: 2021/12/9 10:29
 */
class ViewCheckDrawMsKitView : AbsMsKitView(), ViewCheckMsKitView.OnViewSelectListener {

    private var layoutBorderView: LayoutBorderView? = null

    override fun onCreate(context: Context) {

    }

    override fun onDestroy() {
        super.onDestroy()
        val page =
            MsKit.getMsKitView(ActivityUtils.getTopActivity(), ViewCheckMsKitView::class.java)
        page?.removeViewSelectListener(this)
    }

    override fun onCreateView(context: Context?, rootView: FrameLayout?): View? {
        return LayoutInflater.from(context).inflate(R.layout.ms_float_view_check_draw, null)
    }

    override fun onViewCreated(rootView: FrameLayout?) {
        layoutBorderView = findViewById(R.id.rect_view)
        setMsKitViewNotResponseTouchEvent(msKitView)
        postDelayed(Runnable {
            val msKitView: ViewCheckMsKitView? = MsKit.getMsKitView(
                ActivityUtils.getTopActivity(),
                ViewCheckMsKitView::class.java
            )
            msKitView?.setViewSelectListener(this)
        }, 200)
    }

    override fun initMsKitViewLayoutParams(params: MsKitViewLayoutParams) {
        params.flags = MsKitViewLayoutParams.FLAG_NOT_FOCUSABLE_AND_NOT_TOUCHABLE
        params.width = MsKitViewLayoutParams.MATCH_PARENT
        params.height = MsKitViewLayoutParams.MATCH_PARENT
    }

    override fun onViewSelected(current: View?, checkViewList: List<View>) {
        if (current != null) {
            layoutBorderView?.showViewLayoutBorder(
                ViewInfo(current)
            )
        } else {
            layoutBorderView?.showViewLayoutBorder(null as ViewInfo?)
        }
    }

    override fun onResume() {
        super.onResume()
        val params = normalLayoutParams
        params?.let {
            it.setMargins(0, 0, 0, 0)
            it.width = FrameLayout.LayoutParams.MATCH_PARENT
            it.height = FrameLayout.LayoutParams.MATCH_PARENT
            immInvalidate()
        }
    }

    override fun canDrag(): Boolean {
        return false
    }
}
package com.melot.android.debug.sdk.kit.viewcheck

import android.app.Activity
import android.content.Context
import android.os.Handler
import android.os.HandlerThread
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import com.melot.android.debug.sdk.R
import com.melot.android.debug.sdk.core.AbsMsKitView
import com.melot.android.debug.sdk.core.MsKitViewLayoutParams
import com.melot.android.debug.sdk.util.ActivityUtils
import com.melot.android.debug.sdk.util.DevelopUtil
import com.melot.android.debug.sdk.util.LifecycleListenerUtil

/**
 * Author: han.chen
 * Time: 2021/12/8 20:05
 */
class ViewCheckMsKitView : AbsMsKitView(), LifecycleListenerUtil.LifecycleListener {

    private var mFindCheckViewRunnable: FindCheckViewRunnable = FindCheckViewRunnable()
    private lateinit var mTraverHandlerThread: HandlerThread
    private lateinit var mTraverHandler: Handler
    private var mResumedActivity: Activity? = null
    private val mViewSelectListeners: ArrayList<OnViewSelectListener> = ArrayList()


    override fun onCreate(context: Context) {
        mTraverHandlerThread = HandlerThread(TAG)
        mTraverHandlerThread.start()
        mTraverHandler = Handler(mTraverHandlerThread.looper)
        mResumedActivity = ActivityUtils.getTopActivity()
        LifecycleListenerUtil.registerListener(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        mTraverHandler.removeCallbacks(mFindCheckViewRunnable)
        mTraverHandlerThread.quit()
        LifecycleListenerUtil.unregisterListener(this)
        mResumedActivity = null
    }

    override fun onCreateView(context: Context?, rootView: FrameLayout?): View? {
        return LayoutInflater.from(context).inflate(R.layout.ms_float_view_check, null)
    }

    override fun onViewCreated(rootView: FrameLayout?) {
    }

    override fun initMsKitViewLayoutParams(params: MsKitViewLayoutParams) {
        params.x = DevelopUtil.getAppScreenWidth() / 2
        params.y = DevelopUtil.getAppScreenHeight() / 2
        params.height = MsKitViewLayoutParams.WRAP_CONTENT
        params.width = MsKitViewLayoutParams.WRAP_CONTENT
    }

    override fun onActivityResumed(activity: Activity?) {
        mResumedActivity = activity
        preformFindCheckView()
    }

    override fun onActivityPaused(activity: Activity?) {
    }

    override fun onFragmentAttached(f: Fragment?) {
    }

    override fun onFragmentDetached(f: Fragment?) {
    }

    override fun onUp(x: Int, y: Int) {
        super.onUp(x, y)
        preformFindCheckView()
    }

    override fun onDown(x: Int, y: Int) {

    }



    fun setViewSelectListener(viewSelectListener: OnViewSelectListener?) {
        viewSelectListener?.let {
            mViewSelectListeners.add(it)
            preformFindCheckView()
        }

    }

    fun removeViewSelectListener(viewSelectListener: OnViewSelectListener?) {
        viewSelectListener?.let {
            mViewSelectListeners.remove(it)
        }
    }

    fun preformPreCheckView() {
        mFindCheckViewRunnable.mIndex--
        if (mFindCheckViewRunnable.mIndex < 0) {
            mFindCheckViewRunnable.mIndex += mFindCheckViewRunnable.mCheckViewList!!.size
        }
        mFindCheckViewRunnable.dispatchOnViewSelected()
    }

    fun preformNextCheckView() {
        mFindCheckViewRunnable.mIndex++
        if (mFindCheckViewRunnable.mIndex >= mFindCheckViewRunnable.mCheckViewList!!.size) {
            mFindCheckViewRunnable.mIndex -= mFindCheckViewRunnable.mCheckViewList!!.size
        }
        mFindCheckViewRunnable.dispatchOnViewSelected()
    }

    private fun preformFindCheckView() {
        val x: Int
        val y: Int
        if (isNormalMode) {
            x = normalLayoutParams!!.leftMargin + msKitView!!.width / 2
            y = normalLayoutParams!!.topMargin + msKitView!!.height / 2 + if (DevelopUtil.isStatusBarVisible(activity)) DevelopUtil.getStatusBarHeight() else 0
        } else {
            x = systemLayoutParams!!.x + msKitView!!.getWidth() / 2
            y = systemLayoutParams!!.y + msKitView!!.getHeight() / 2
        }
        mTraverHandler.removeCallbacks(mFindCheckViewRunnable)
        mFindCheckViewRunnable.mX = x
        mFindCheckViewRunnable.mY = y
        mTraverHandler.post(mFindCheckViewRunnable)
    }

    private fun traverseViews(viewList: MutableList<View>, view: View?, x: Int, y: Int) {
        if (view == null) {
            return
        }
        val location = IntArray(2)
        view.getLocationInWindow(location)
        val left = location[0]
        val top = location[1]
        val right = left + view.width
        val bottom = top + view.height

        // 深度优先遍历
        if (view is ViewGroup) {
            val childCount = view.childCount
            if (childCount != 0) {
                for (index in childCount - 1 downTo 0) {
                    traverseViews(viewList, view.getChildAt(index), x, y)
                }
            }
            if (x in (left + 1) until right && top < y && y < bottom) {
                viewList.add(view)
            }
        } else {
            if (x in (left + 1) until right && top < y && y < bottom) {
                viewList.add(view)
            }
        }
    }

    private fun onViewSelected(current: View?, checkViewList: List<View>) {
        for (listener in mViewSelectListeners) {
            listener.onViewSelected(current, checkViewList)
        }
    }

    interface OnViewSelectListener {
        fun onViewSelected(current: View?, checkViewList: List<View>)
    }

    inner class FindCheckViewRunnable : Runnable {
        var mX = 0
        var mY = 0
        var mIndex = 0
        lateinit var mCheckViewList: List<View>
        override fun run() {
            val viewList = ArrayList<View>(20)
            if (mResumedActivity?.window != null) {
                if (isNormalMode) {
                    traverseViews(
                        viewList,
                        DevelopUtil.getMsKitAppContentView(mResumedActivity),
                        mX,
                        mY
                    )
                } else {
                    traverseViews(viewList, mResumedActivity?.window?.decorView, mX, mY)
                }
            }
            mIndex = 0
            mCheckViewList = viewList
            dispatchOnViewSelected()
        }

        fun dispatchOnViewSelected() {
            post(Runnable { onViewSelected(getCurrentCheckView(), mCheckViewList) })
        }

        private fun getCurrentCheckView(): View? {
            val size = mCheckViewList.size
            return if (size == 0) {
                null
            } else mCheckViewList[mIndex]
        }
    }
}
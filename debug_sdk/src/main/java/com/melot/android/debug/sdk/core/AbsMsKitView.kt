package com.melot.android.debug.sdk.core

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.res.Configuration
import android.content.res.Resources
import android.graphics.PixelFormat
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.*
import android.widget.FrameLayout
import android.widget.TextView
import androidx.annotation.IdRes
import androidx.annotation.StringRes
import androidx.core.view.GravityCompat
import com.melot.android.debug.sdk.MsKit
import com.melot.android.debug.sdk.config.FloatIconConfig
import com.melot.android.debug.sdk.extension.tagName
import com.melot.android.debug.sdk.main.MainIconMsKitView
import com.melot.android.debug.sdk.util.ActivityUtils
import com.melot.android.debug.sdk.util.DevelopUtil
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.plus
import java.lang.ref.WeakReference

/**
 * Author: han.chen
 * Time: 2021/12/3 15:53
 */
abstract class AbsMsKitView : MsKitView, MsKitViewManager.MsKitViewAttachedListener,
    TouchProxy.OnTouchEventListener {

    class ViewArgs {
        var mode: MsKitViewLaunchMode = MsKitViewLaunchMode.SINGLE_INSTANCE
        var normalMode = MsKitManager.IS_NORMAL_FLOAT_MODE
        var edgePinned = false
    }

    val msKitViewScope = MainScope() + CoroutineName(this.toString())

    val TAG = this.tagName

    /**
     * 页面启动模式
     */
    var mode: MsKitViewLaunchMode
        get() = viewProps.mode
        set(value) {
            viewProps.mode = value
        }

    val isNormalMode get() = viewProps.normalMode

    /**
     * 手势代理
     */
    @JvmField
    var mTouchProxy = TouchProxy(this)

    protected val viewProps = ViewArgs()

    @JvmField
    protected var mWindowManager = MsKitViewManager.INSTANCE.windowManager

    /**
     * 创建FrameLayout#LayoutParams 内置悬浮窗调用
     */
    var normalLayoutParams: FrameLayout.LayoutParams? = null
        private set

    /**
     * 创建FrameLayout#LayoutParams 系统悬浮窗调用
     */
    var systemLayoutParams: WindowManager.LayoutParams? = null
        private set

    private var mHandler: Handler? = Handler(Looper.myLooper())

    private val mInnerReceiver = InnerReceiver()

    /**
     * 当前msKitViewName 用来当做map的key 和msKitViewIntent的tag一致
     */
    var tag = this.tagName
    var bundle: Bundle? = null

    /**
     * weakActivity attach activity
     */
    private var mAttachActivity: WeakReference<Activity?>? = null

    val activity: Activity?
        get() = if (mAttachActivity != null) {
            mAttachActivity!!.get()!!
        } else ActivityUtils.getTopActivity()

    fun setActivity(activity: Activity?) {
        mAttachActivity = WeakReference(activity)
    }

    /**
     * 整个悬浮窗的View
     */
    private var mRootView: FrameLayout? = null

    val msKitView: View?
        get() = mRootView

    /**
     * rootView的直接子View 一般是用户的xml布局 被添加到mRootView中
     */
    private var mChildView: View? = null

    val parentView: MsKitFrameLayout?
        get() = if (isNormalMode && mRootView != null) {
            mRootView!!.parent as MsKitFrameLayout
        } else null

    private lateinit var mMsKitViewLayoutParams: MsKitViewLayoutParams

    private val mLastMsKitViewPosInfo: LastMsKitViewPosInfo by lazy {
        if (MsKitViewManager.INSTANCE.getLastMsKitViewPosInfo(tag) == null) {
            val posInfo = LastMsKitViewPosInfo()
            MsKitViewManager.INSTANCE.saveLastMsKitViewPosInfo(tag, posInfo)
            posInfo
        } else
            MsKitViewManager.INSTANCE.getLastMsKitViewPosInfo(tag)!!
    }

    /**
     * 根布局的实际宽
     */
    private var mMsKitViewWidth = 0

    /**
     * 根布局的实际高
     */
    private var mMsKitViewHeight = 0
    private var mViewTreeObserver: ViewTreeObserver? = null

    private val mOnGlobalLayoutListener: ViewTreeObserver.OnGlobalLayoutListener =
        ViewTreeObserver.OnGlobalLayoutListener {
            //每次布局发生变动的时候重新赋值
            mRootView?.let {
                mMsKitViewWidth = it.measuredWidth
                mMsKitViewHeight = it.measuredHeight
                mLastMsKitViewPosInfo.msKitViewWidth = mMsKitViewWidth
                mLastMsKitViewPosInfo.msKitViewHeight = mMsKitViewHeight
            }
        }

    fun performCreate(context: Context) {
        try {
            onCreate(context)
            if (!isNormalMode) {
                MsKitViewManager.INSTANCE.addMsKitViewAttachedListener(this)
            }

            mRootView = if (isNormalMode) {
                MsKitFrameLayout(context, MsKitFrameLayout.MsKitFrameLayoutFlag_CHILD)
            } else {
                object : MsKitFrameLayout(context, MsKitFrameLayoutFlag_CHILD) {
                    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
                        if (event.action == KeyEvent.ACTION_UP && shouldDealBackKey()) {
                            //监听返回键
                            if (event.keyCode == KeyEvent.KEYCODE_BACK || event.keyCode == KeyEvent.KEYCODE_HOME) {
                                return onBackPressed()
                            }
                        }
                        return super.dispatchKeyEvent(event)
                    }
                }
            }
            //添加根布局的layout回调
            addViewTreeObserverListener()

            //调用onCreateView抽象方法
            mChildView = onCreateView(context, mRootView)
            mRootView?.addView(mChildView)
            //设置根布局的手势拦截
            mRootView?.setOnTouchListener { v, event ->
                mTouchProxy.onTouchEvent(v, event)
            }
            //调用onViewCreated回调
            onViewCreated(mRootView)
            mMsKitViewLayoutParams = MsKitViewLayoutParams()
            if (isNormalMode) {
                normalLayoutParams = FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.WRAP_CONTENT,
                    FrameLayout.LayoutParams.WRAP_CONTENT
                )
                    .apply {
                        gravity = GravityCompat.START or Gravity.TOP
                    }
                mMsKitViewLayoutParams.gravity = GravityCompat.START or Gravity.TOP
            } else {
                systemLayoutParams = WindowManager.LayoutParams()
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    //android 8.0
                    systemLayoutParams?.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
                } else {
                    systemLayoutParams?.type = WindowManager.LayoutParams.TYPE_PHONE
                }

                //shouldDealBackKey : fasle 不自己收返回事件处理
                if (shouldDealBackKey()) {
                    //自己处理返回按键
                    systemLayoutParams?.flags =
                        WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
                    mMsKitViewLayoutParams.flags =
                        WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or MsKitViewLayoutParams.FLAG_LAYOUT_NO_LIMITS
                } else {
                    //参考：http://www.shirlman.com/tec/20160426/362
                    //设置WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE会导致RootView监听不到返回按键的监听失效 系统处理返回按键
                    systemLayoutParams?.flags =
                        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
                    mMsKitViewLayoutParams.flags =
                        MsKitViewLayoutParams.FLAG_NOT_FOCUSABLE or MsKitViewLayoutParams.FLAG_LAYOUT_NO_LIMITS
                }
                systemLayoutParams?.apply {
                    format = PixelFormat.TRANSPARENT
                    gravity = GravityCompat.START or Gravity.TOP
                }
                mMsKitViewLayoutParams.gravity = GravityCompat.START or Gravity.TOP
                //动态注册关闭系统弹窗的广播
                val intentFilter = IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)
                context.registerReceiver(mInnerReceiver, intentFilter)
            }
            initMsKitViewLayoutParams(mMsKitViewLayoutParams)
            if (isNormalMode) {
                normalLayoutParams?.let {
                    onNormalLayoutParamsCreated()
                }
            } else {
                systemLayoutParams?.let {
                    onSystemLayoutParamsCreated()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun performDestroy() {
        if (!isNormalMode) {
            context?.unregisterReceiver(mInnerReceiver)
        }
        //移除布局监听
        removeViewTreeObserverListener()
        mHandler = null
        mRootView = null
        mAttachActivity = null
        onDestroy()
    }

    private fun addViewTreeObserverListener() {
        if (mViewTreeObserver == null && mRootView != null) {
            mViewTreeObserver = mRootView?.viewTreeObserver
            mViewTreeObserver?.addOnGlobalLayoutListener(mOnGlobalLayoutListener)

        }
    }

    private fun removeViewTreeObserverListener() {
        mViewTreeObserver?.let {
            if (it.isAlive) {
                it.removeOnGlobalLayoutListener(mOnGlobalLayoutListener)
            }
        }
    }

    private fun onNormalLayoutParamsCreated() {
        //如果有上一个页面的位置记录 这更新位置
        normalLayoutParams?.apply {
            width = mMsKitViewLayoutParams.width
            height = mMsKitViewLayoutParams.height
            gravity = mMsKitViewLayoutParams.gravity
        }
        val msKitViewInfo = MsKitViewManager.INSTANCE.getMsKitViewPos(tag)
        if (msKitViewInfo != null) {
            //竖向
            if (msKitViewInfo.orientation == Configuration.ORIENTATION_PORTRAIT) {
                normalLayoutParams?.apply {
                    leftMargin = msKitViewInfo.portraitPoint.x
                    topMargin = msKitViewInfo.portraitPoint.y
                }
            } else if (msKitViewInfo.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                normalLayoutParams?.apply {
                    leftMargin = msKitViewInfo.landscapePoint.x
                    topMargin = msKitViewInfo.landscapePoint.y
                }
            }
        } else {
            normalLayoutParams?.apply {
                leftMargin = mMsKitViewLayoutParams.x
                topMargin = mMsKitViewLayoutParams.y
            }
        }
        portraitOrLandscape()
    }

    private fun portraitOrLandscape() {
        MsKitViewManager.INSTANCE.getMsKitViewPos(tag)
            ?.also { msKitViewInfo ->
                if (DevelopUtil.isPortrait()) {
                    if (mLastMsKitViewPosInfo.isPortrait) {
                        normalLayoutParams?.apply {
                            leftMargin = msKitViewInfo.portraitPoint.x
                            topMargin = msKitViewInfo.portraitPoint.y
                        }
                    } else {
                        normalLayoutParams?.apply {
                            leftMargin =
                                (msKitViewInfo.landscapePoint.x * mLastMsKitViewPosInfo.leftMarginPercent).toInt()
                            topMargin =
                                (msKitViewInfo.landscapePoint.y * mLastMsKitViewPosInfo.topMarginPercent).toInt()
                        }
                    }
                } else {
                    if (mLastMsKitViewPosInfo.isPortrait) {
                        normalLayoutParams?.apply {
                            leftMargin =
                                (msKitViewInfo.portraitPoint.x * mLastMsKitViewPosInfo.leftMarginPercent).toInt()
                            topMargin =
                                (msKitViewInfo.portraitPoint.y * mLastMsKitViewPosInfo.topMarginPercent).toInt()
                        }
                    } else {
                        normalLayoutParams?.apply {
                            leftMargin = msKitViewInfo.landscapePoint.x
                            topMargin = msKitViewInfo.landscapePoint.y
                        }
                    }
                }
            } ?: kotlin.run {
            if (DevelopUtil.isPortrait()) {
                if (mLastMsKitViewPosInfo.isPortrait) {
                    normalLayoutParams?.apply {
                        leftMargin = mMsKitViewLayoutParams.x
                        topMargin = mMsKitViewLayoutParams.y
                    }
                } else {
                    normalLayoutParams?.apply {
                        leftMargin =
                            (mMsKitViewLayoutParams.x * mLastMsKitViewPosInfo.leftMarginPercent).toInt()
                        topMargin =
                            (mMsKitViewLayoutParams.y * mLastMsKitViewPosInfo.topMarginPercent).toInt()
                    }
                }
            } else {
                if (mLastMsKitViewPosInfo.isPortrait) {
                    normalLayoutParams?.apply {
                        leftMargin =
                            (mMsKitViewLayoutParams.x * mLastMsKitViewPosInfo.leftMarginPercent).toInt()
                        topMargin =
                            (mMsKitViewLayoutParams.y * mLastMsKitViewPosInfo.topMarginPercent).toInt()
                    }
                } else {
                    normalLayoutParams?.apply {
                        leftMargin = mMsKitViewLayoutParams.x
                        topMargin = mMsKitViewLayoutParams.y
                    }
                }
            }
        }
        mLastMsKitViewPosInfo.isPortrait = DevelopUtil.isPortrait()
        normalLayoutParams?.also {
            mLastMsKitViewPosInfo.leftMarginPercent = it.leftMargin.toFloat()
            mLastMsKitViewPosInfo.topMarginPercent = it.topMargin.toFloat()
        }
        if (tag == MainIconMsKitView::class.tagName) {
            if (isNormalMode) {
                normalLayoutParams?.also {
                    FloatIconConfig.saveLastPosX(it.leftMargin)
                    FloatIconConfig.saveLastPosY(it.topMargin)
                }
            } else {
                systemLayoutParams?.also {
                    FloatIconConfig.saveLastPosX(it.x)
                    FloatIconConfig.saveLastPosY(it.y)
                }
            }
        }

        MsKitViewManager.INSTANCE.saveMsKitViewPos(
            tag,
            normalLayoutParams?.leftMargin ?: 0,
            normalLayoutParams?.topMargin ?: 0
        )
    }

    private fun onSystemLayoutParamsCreated() {
        //如果有上一个页面的位置记录 这更新位置
        systemLayoutParams?.flags = mMsKitViewLayoutParams.flags
        systemLayoutParams?.gravity = mMsKitViewLayoutParams.gravity
        systemLayoutParams?.width = mMsKitViewLayoutParams.width
        systemLayoutParams?.height = mMsKitViewLayoutParams.height
        val msKitViewInfo = MsKitViewManager.INSTANCE.getMsKitViewPos(
            tag
        )
        if (msKitViewInfo != null) {
            if (DevelopUtil.isPortrait()) {
                systemLayoutParams?.x = msKitViewInfo.portraitPoint.x
                systemLayoutParams?.y = msKitViewInfo.portraitPoint.y
            } else if (DevelopUtil.isLandscape()) {
                systemLayoutParams?.x = msKitViewInfo.landscapePoint.x
                systemLayoutParams?.y = msKitViewInfo.landscapePoint.y
            }
        } else {
            systemLayoutParams?.x = mMsKitViewLayoutParams.x
            systemLayoutParams?.y = mMsKitViewLayoutParams.y
        }
        systemLayoutParams?.let {
            MsKitViewManager.INSTANCE.saveMsKitViewPos(tag, it.x, it.y)
        }
    }

    override fun onDestroy() {
        if (!isNormalMode) {
            MsKitViewManager.INSTANCE.removeMsKitViewAttachedListener(this)
        }
        MsKitViewManager.INSTANCE.removeLastMsKitViewPosInfo(tag)
        mAttachActivity = null
        msKitViewScope.cancel()
    }

    /**
     * 默认实现为true
     *
     * @return
     */
    override fun canDrag(): Boolean {
        return true
    }

    /**
     * 搭配shouldDealBackKey使用 自定义处理完以后需要返回true
     * 默认模式的onBackPressed 拦截在NormalmsKitViewManager#getmsKitRootContentView中被处理
     * 系统模式下的onBackPressed 在当前类的performCreate 初始话msKitView时被处理
     * 返回false 表示交由系统处理
     * 返回 true 表示当前的返回事件已由自己处理 并拦截了改返回事件
     */
    override fun onBackPressed(): Boolean {
        return false
    }

    /**
     * 默认不自己处理返回按键
     *
     * @return
     */
    override fun shouldDealBackKey(): Boolean {
        return false
    }

    override fun onEnterBackground() {
        mRootView?.let {
            if (!isNormalMode) {
                it.visibility = View.GONE
            }
        }
    }

    override fun onEnterForeground() {
        mRootView?.let {
            if (!isNormalMode) {
                it.visibility = View.VISIBLE
            }
        }
    }

    override fun onMove(x: Int, y: Int, dx: Int, dy: Int) {
        if (!canDrag()) {
            return
        }
        if (isNormalMode) {
            normalLayoutParams?.apply {
                this.leftMargin += dx
                this.topMargin += dy
            }

            //更新图标位置
            updateViewLayout(tag, false)
        } else {
            systemLayoutParams?.apply {
                this.x += dx
                this.y += dy
            }
            //限制布局边界
            resetBorderline(normalLayoutParams, systemLayoutParams)
            mWindowManager.updateViewLayout(mRootView, systemLayoutParams)
        }
    }

    /**
     * 手指弹起时保存当前浮标位置
     *
     * @param x
     * @param y
     */
    override fun onUp(x: Int, y: Int) {
        if (!canDrag()) {
            return
        }
        if (!viewProps.edgePinned) {
            endMoveAndRecord()
            return
        }
        animatedMoveToEdge()
    }

    /**
     * 手指按下时的操作
     *
     * @param x
     * @param y
     */
    override fun onDown(x: Int, y: Int) {
        if (!canDrag()) {
            return
        }
    }

    /**
     * 广播接收器 系统悬浮窗需要调用
     */
    private inner class InnerReceiver : BroadcastReceiver() {
        val SYSTEM_DIALOG_REASON_KEY = "reason"
        val SYSTEM_DIALOG_REASON_RECENT_APPS = "recentapps"
        val SYSTEM_DIALOG_REASON_HOME_KEY = "homekey"
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            if (Intent.ACTION_CLOSE_SYSTEM_DIALOGS == action) {
                val reason = intent.getStringExtra(SYSTEM_DIALOG_REASON_KEY)
                if (reason != null) {
                    if (reason == SYSTEM_DIALOG_REASON_HOME_KEY) {
                        //点击home键
                        onHomeKeyPress()
                    } else if (reason == SYSTEM_DIALOG_REASON_RECENT_APPS) {
                        //点击menu按钮
                        onRecentAppKeyPress()
                    }
                }
            }
        }
    }

    /**
     * home键被点击 只有系统悬浮窗控件才会被调用
     */
    open fun onHomeKeyPress() {}

    /**
     * 菜单键被点击 只有系统悬浮窗控件才会被调用
     */
    open fun onRecentAppKeyPress() {}

    override fun onMsKitViewAdd(absMsKitView: AbsMsKitView?) {}

    override fun onResume() {
        mRootView?.requestLayout()
    }

    override fun onPause() {}

    /**
     * 系统悬浮窗需要调用
     *
     * @return
     */
    val context: Context?
        get() = mRootView?.context

    val resources: Resources?
        get() = context?.resources

    fun getString(@StringRes resId: Int): String? {
        return context?.getString(resId)
    }

    val isShow: Boolean
        get() = mRootView?.isShown ?: false

    protected fun <T : View> findViewById(@IdRes id: Int): T? {
        if (mRootView == null) {
            return null
        }
        return mRootView?.findViewById(id)
    }

    /**
     * 将当前msKitView于activity解绑
     */
    fun detach() {
        MsKit.removeFloating(this.javaClass)
    }

    /**
     * 操作DecorView的直接子布局
     */
    open fun dealDecorRootView(decorRootView: FrameLayout?) {
        if (isNormalMode) {
            if (decorRootView == null) {
                return
            }
        }
    }

    open fun updateViewLayout(tag: String, isActivityBackResume: Boolean) {
        if (mRootView == null || mChildView == null || normalLayoutParams == null || !isNormalMode) {
            return
        }
        normalLayoutParams?.apply {
            if (isActivityBackResume) {
                if (tag == MainIconMsKitView::class.tagName) {
                    this.leftMargin = FloatIconConfig.getLastPosX()
                    this.topMargin = FloatIconConfig.getLastPosY()
                } else {
                    val msKitViewInfo = MsKitViewManager.INSTANCE.getMsKitViewPos(tag)
                    msKitViewInfo?.let {
                        if (it.orientation == Configuration.ORIENTATION_PORTRAIT) {
                            this.leftMargin = msKitViewInfo.portraitPoint.x
                            this.topMargin = msKitViewInfo.portraitPoint.y
                        } else {
                            this.leftMargin = msKitViewInfo.landscapePoint.x
                            this.topMargin = msKitViewInfo.landscapePoint.y
                        }
                    }
                }
            } else {
                mLastMsKitViewPosInfo.isPortrait = DevelopUtil.isPortrait()
                mLastMsKitViewPosInfo.leftMarginPercent = this.leftMargin.toFloat()
                mLastMsKitViewPosInfo.topMarginPercent = this.topMargin.toFloat()
            }
            if (tag == MainIconMsKitView::class.tagName) {
                this.width = MsKitViewLayoutParams.WRAP_CONTENT
                this.height = MsKitViewLayoutParams.WRAP_CONTENT
            } else {
                if (mMsKitViewWidth != 0) {
                    this.width = mMsKitViewWidth
                }
                if (mMsKitViewHeight != 0) {
                    this.height = mMsKitViewHeight
                }
            }
            resetBorderline(this, systemLayoutParams)
            mRootView?.layoutParams = this
        }
    }

    private fun resetBorderline(
        normalFrameLayoutParams: FrameLayout.LayoutParams?,
        windowLayoutParams: WindowManager.LayoutParams?
    ) {
        //如果是系统模式或者手动关闭动态限制边界
        if (!restrictBorderline()) {
            return
        }
        //普通模式
        if (isNormalMode) {
            if (normalFrameLayoutParams != null) {
                if (DevelopUtil.isPortrait()) {
                    if (normalFrameLayoutParams.topMargin >= screenLongSideLength - mMsKitViewHeight) {
                        normalFrameLayoutParams.topMargin = screenLongSideLength - mMsKitViewHeight
                    }
                } else {
                    if (normalFrameLayoutParams.topMargin >= screenShortSideLength - mMsKitViewHeight) {
                        normalFrameLayoutParams.topMargin = screenShortSideLength - mMsKitViewHeight
                    }
                }

                if (DevelopUtil.isPortrait()) {
                    if (normalFrameLayoutParams.leftMargin >= screenShortSideLength - mMsKitViewWidth) {
                        normalFrameLayoutParams.leftMargin = screenShortSideLength - mMsKitViewWidth
                    }
                } else {
                    if (normalFrameLayoutParams.leftMargin >= screenLongSideLength - mMsKitViewWidth) {
                        normalFrameLayoutParams.leftMargin = screenLongSideLength - mMsKitViewWidth
                    }
                }

                if (normalFrameLayoutParams.topMargin <= 0) {
                    normalFrameLayoutParams.topMargin = 0
                }

                if (normalFrameLayoutParams.leftMargin <= 0) {
                    normalFrameLayoutParams.leftMargin = 0
                }
            }

        } else {
            if (windowLayoutParams != null) {
                if (DevelopUtil.isPortrait()) {
                    if (windowLayoutParams.y >= screenLongSideLength - mMsKitViewHeight) {
                        windowLayoutParams.y = screenLongSideLength - mMsKitViewHeight
                    }
                } else {
                    if (windowLayoutParams.y >= screenShortSideLength - mMsKitViewHeight) {
                        windowLayoutParams.y = screenShortSideLength - mMsKitViewHeight
                    }
                }

                if (DevelopUtil.isPortrait()) {
                    if (windowLayoutParams.x >= screenShortSideLength - mMsKitViewWidth) {
                        windowLayoutParams.x = screenShortSideLength - mMsKitViewWidth
                    }
                } else {
                    if (windowLayoutParams.x >= screenLongSideLength - mMsKitViewWidth) {
                        windowLayoutParams.x = screenLongSideLength - mMsKitViewWidth
                    }
                }

                //系统模式
                if (windowLayoutParams.y <= 0) {
                    windowLayoutParams.y = 0
                }

                if (windowLayoutParams.x <= 0) {
                    windowLayoutParams.x = 0
                }
            }
        }
    }

    /**
     * 是否限制布局边界
     *
     * @return
     */
    open fun restrictBorderline(): Boolean {
        return true
    }

    fun post(run: Runnable) {
        mHandler?.post(run)
    }

    fun postDelayed(run: Runnable, delayMillis: Long) {
        mHandler?.postDelayed(run, delayMillis)
    }

    /**
     * 设置当前 kitView 不响应触摸事件
     * 控件默认响应触摸事件
     * 需要在子 view 的 onViewCreated 中调用
     */
    fun setMsKitViewNotResponseTouchEvent(view: View?) {
        if (isNormalMode) {
            view?.setOnTouchListener { _, _ -> false }
        } else {
            view?.setOnTouchListener(null)
        }
    }

    /**
     * 获取屏幕短边的长度 不包含statusBar
     *
     * @return
     */
    val screenShortSideLength: Int
        get() = if (DevelopUtil.isPortrait()) {
            DevelopUtil.getAppScreenWidth()
        } else {
            DevelopUtil.getAppScreenHeight()
        }

    /**
     * 获取屏幕长边的长度 不包含statusBar
     *
     * @return
     */
    val screenLongSideLength: Int
        get() = if (DevelopUtil.isPortrait()) {
            DevelopUtil.getAppScreenHeight()
        } else {
            DevelopUtil.getAppScreenWidth()
        }

    open fun immInvalidate() {
        mRootView?.requestLayout()
    }

    private fun animatedMoveToEdge() {
        val viewSize = mRootView?.width ?: return
        if (isNormalMode) {
            val parent = (mRootView?.parent as? ViewGroup) ?: return
            normalLayoutParams?.also { layoutAttrs ->
                makeAnimator(layoutAttrs.leftMargin, viewSize, parent.width) {
                    addUpdateListener { v ->
                        layoutAttrs.leftMargin = v.animatedValue as Int
                        updateViewLayout(tag, false)
                    }
                    addListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationEnd(animation: Animator?) {
                            endMoveAndRecord()
                        }
                    })
                }
            }
            return
        }
        systemLayoutParams?.also { layoutAttrs ->
            makeAnimator(layoutAttrs.x, viewSize, MsKit.windowSize.x) {
                addUpdateListener { v ->
                    layoutAttrs.x = v.animatedValue as Int
                    mWindowManager.updateViewLayout(mRootView, layoutAttrs)
                }
                addListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator?) {
                        endMoveAndRecord()
                    }
                })
            }
        }
    }

    private fun endMoveAndRecord() {
        if (tag == MainIconMsKitView::class.tagName) {
            if (isNormalMode) {
                normalLayoutParams?.also {
                    FloatIconConfig.saveLastPosX(it.leftMargin)
                    FloatIconConfig.saveLastPosY(it.topMargin)
                }
            } else {
                systemLayoutParams?.also {
                    FloatIconConfig.saveLastPosX(it.x)
                    FloatIconConfig.saveLastPosY(it.y)
                }
            }
        }
        // 保存在内存中
        if (isNormalMode) {
            normalLayoutParams?.also {
                MsKitViewManager.INSTANCE.saveMsKitViewPos(
                    tag,
                    it.leftMargin,
                    it.topMargin
                )
            }
        } else {
            systemLayoutParams?.also { MsKitViewManager.INSTANCE.saveMsKitViewPos(tag, it.x, it.y) }
        }
    }

    private inline fun makeAnimator(
        from: Int,
        size: Int,
        containerSize: Int,
        setup: ValueAnimator.() -> Unit
    ) {
        if (size <= 0 || containerSize <= 0) return
        ValueAnimator.ofInt(
            from,
            if (from <= (containerSize - size) / 2) 0 else (containerSize - size)
        )
            .apply {
                duration = 150L
                setup()
            }
            .start()
    }
}


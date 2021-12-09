package com.melot.android.debug.sdk.kit.viewcheck

import android.app.Activity
import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.MarginLayoutParams
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.melot.android.debug.sdk.MsKit
import com.melot.android.debug.sdk.R
import com.melot.android.debug.sdk.core.AbsMsKitView
import com.melot.android.debug.sdk.core.MsKitViewLayoutParams
import com.melot.android.debug.sdk.util.ActivityUtils
import com.melot.android.debug.sdk.util.ColorUtil
import com.melot.android.debug.sdk.util.DevelopUtil
import java.lang.StringBuilder

/**
 * Author: han.chen
 * Time: 2021/12/9 12:39
 */
class ViewCheckerInfoMsKitView : AbsMsKitView(), ViewCheckMsKitView.OnViewSelectListener,
    View.OnClickListener {

    private var mName: TextView? = null
    private var mId: TextView? = null
    private var mPosition: TextView? = null
    private var mDesc: TextView? = null
    private var mActivityInfo: TextView? = null
    private var mFragmentInfo: TextView? = null

    private var mPre: ImageView? = null
    private var mNext: ImageView? = null
    private var mClose: ImageView? = null

    override fun onCreate(context: Context) {

    }

    override fun onDestroy() {
        super.onDestroy()
        MsKit.getMsKitView(activity, ViewCheckMsKitView::class.java)?.removeViewSelectListener(this)
    }

    override fun onCreateView(context: Context?, rootView: FrameLayout?): View? {
        return LayoutInflater.from(context).inflate(R.layout.ms_float_view_check_info, null)
    }

    override fun onViewCreated(rootView: FrameLayout?) {
        mId = findViewById(R.id.id)
        mName = findViewById(R.id.name)
        mPosition = findViewById(R.id.position)
        mDesc = findViewById(R.id.desc)
        mActivityInfo = findViewById(R.id.activity)
        mFragmentInfo = findViewById(R.id.fragment)
        mClose = findViewById(R.id.close)
        mClose?.setOnClickListener(this)
        mPre = findViewById(R.id.pre)
        mPre?.setOnClickListener(this)
        mNext = findViewById(R.id.next)
        mNext?.setOnClickListener(this)
        postDelayed(Runnable {
            MsKit.getMsKitView(activity, ViewCheckMsKitView::class.java)?.setViewSelectListener(this)
        }, 200)
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

    override fun onViewSelected(current: View?, checkViewList: List<View>) {
        mNext?.visibility = if (checkViewList.size > 1) View.VISIBLE else View.GONE
        mPre?.visibility = if (checkViewList.size > 1) View.VISIBLE else View.GONE
        if (current == null) {
            mName?.text = ""
            mId?.text = ""
            mPosition?.text = ""
            mDesc?.text = ""
        } else {
            mName?.text = resources?.getString(
                R.string.dk_view_check_info_class,
                current.javaClass.canonicalName
            )
            val idText =
                resources?.getString(R.string.dk_view_check_info_id, DevelopUtil.getIdText(current))
            mId?.text = idText
            val positionText = resources?.getString(
                R.string.dk_view_check_info_size,
                current.width,
                current.height
            )
            mPosition?.text = positionText
            val descText: String = getViewExtraInfo(current)
            if (TextUtils.isEmpty(descText)) {
                mDesc?.visibility = View.GONE
            } else {
                mDesc?.text = descText
                mDesc?.visibility = View.VISIBLE
            }
            val activity: Activity? = ActivityUtils.getTopActivity()
            if (activity != null) {
                val activityText = activity.javaClass.simpleName
                setTextAndVisible(
                    mActivityInfo,
                    resources?.getString(R.string.dk_view_check_info_activity, activityText)
                )
                val fragmentText: String? = getVisibleFragment(activity)
                if (!TextUtils.isEmpty(fragmentText)) {
                    setTextAndVisible(
                        mFragmentInfo,
                        resources?.getString(R.string.dk_view_check_info_fragment, fragmentText)
                    )
                } else {
                    setTextAndVisible(mFragmentInfo, "")
                }
            } else {
                setTextAndVisible(mActivityInfo, "")
                setTextAndVisible(mFragmentInfo, "")
            }
        }
    }

    override fun onClick(v: View?) {
        when (v) {
            mClose -> {
                MsKit.removeFloating(ViewCheckDrawMsKitView::class.java)
                MsKit.removeFloating(ViewCheckerInfoMsKitView::class.java)
                MsKit.removeFloating(ViewCheckMsKitView::class.java)
            }
            mNext -> {
                val msKitView: ViewCheckMsKitView? = MsKit.getMsKitView(
                    activity,
                    ViewCheckMsKitView::class.java
                )
                msKitView?.preformNextCheckView()
            }
            mPre -> {
                val msKitView: ViewCheckMsKitView? = MsKit.getMsKitView(
                    activity,
                    ViewCheckMsKitView::class.java
                )
                msKitView?.preformPreCheckView()
            }
        }
    }

    private fun getViewExtraInfo(v: View): String {
        val info = StringBuilder()
        // 背景色
        val drawable = v.background
        if (drawable != null) {
            if (drawable is ColorDrawable) {
                val colorInt = drawable.color
                val backgroundColor: String =
                    ColorUtil.parseColorInt(colorInt)
                info.append(
                    resources?.getString(
                        R.string.dk_view_check_info_desc,
                        backgroundColor
                    )
                )
                info.append("\n")
            }
        }
        // padding
        if (v.paddingLeft != 0 && v.paddingTop != 0 && v.paddingRight != 0 && v.paddingBottom != 0) {
            info.append(
                resources?.getString(
                    R.string.dk_view_check_info_padding,
                    v.paddingLeft,
                    v.paddingTop,
                    v.paddingRight,
                    v.paddingBottom
                )
            )
            info.append("\n")
        }
        // margin
        val layoutParams = v.layoutParams
        if (layoutParams is MarginLayoutParams) {
            val mp = layoutParams
            if (mp.leftMargin != 0 && mp.topMargin != 0 && mp.rightMargin != 0 && mp.bottomMargin != 0) {
                info.append(
                    resources?.getString(
                        R.string.dk_view_check_info_margin,
                        mp.leftMargin,
                        mp.topMargin,
                        mp.rightMargin,
                        mp.bottomMargin
                    )
                )
                info.append("\n")
            }
        }
        // TextView信息
        if (v is TextView) {
            val tv = v
            val textColor: String =
                ColorUtil.parseColorInt(tv.currentTextColor)
            info.append(resources?.getString(R.string.dk_view_check_info_text_color, textColor))
            info.append("\n")
            info.append(
                resources?.getString(
                    R.string.dk_view_check_info_text_size,
                    tv.textSize.toInt()
                )
            )
            info.append("\n")
        }
        // 删除最后一个换行
        if (!TextUtils.isEmpty(info)) {
            info.deleteCharAt(info.length - 1)
        }
        return info.toString()
    }

    private fun setTextAndVisible(textView: TextView?, text: String?) {
        if (TextUtils.isEmpty(text)) {
            textView?.visibility = View.GONE
            textView?.text = ""
        } else {
            textView?.visibility = View.VISIBLE
            textView?.text = text
        }
    }

    private fun getVisibleFragment(activity: Activity?): String? {
        if (activity == null) {
            return null
        }
        val builder = StringBuilder()
        return if (activity is AppCompatActivity) {
            val fragmentManager = activity.supportFragmentManager
            val fragments = fragmentManager.fragments
            if (fragments.size != 0) {
                for (i in fragments.indices) {
                    val fragment = fragments[i]
                    if (fragment != null && fragment.isVisible) {
                        builder.append(fragment.javaClass.simpleName + "#" + fragment.id)
                        if (i < fragments.size - 1) {
                            builder.append(";")
                        }
                    }
                }
                builder.toString()
            } else {
                getFragmentForActivity(activity)
            }
        } else {
            getFragmentForActivity(activity)
        }
    }

    private fun getFragmentForActivity(activity: Activity): String {
        val builder = StringBuilder()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val manager = activity.fragmentManager
            val list = manager.fragments
            if (list != null && list.size > 0) {
                for (i in list.indices) {
                    val fragment = list[i]
                    if (fragment != null && fragment.isVisible) {
                        builder.append(fragment.javaClass.simpleName + "#" + fragment.id)
                        if (i < list.size - 1) {
                            builder.append(";")
                        }
                    }
                }
            }
        }
        return builder.toString()
    }
}
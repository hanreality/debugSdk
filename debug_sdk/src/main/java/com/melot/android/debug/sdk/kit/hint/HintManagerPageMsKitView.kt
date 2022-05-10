package com.melot.android.debug.sdk.kit.hint

import android.app.AlertDialog
import android.content.Context
import android.text.InputType
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView

import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.melot.android.debug.sdk.MsKit
import com.melot.android.debug.sdk.R
import com.melot.android.debug.sdk.core.AbsMsKitView
import com.melot.android.debug.sdk.core.MsKitViewLayoutParams
import com.melot.android.debug.sdk.util.ActivityUtils
import com.melot.android.debug.sdk.util.DevelopUtil
import com.melot.android.debug.sdk.util.MsDialogUtil
import org.w3c.dom.Text

/**
 * Author: han.chen
 * Time: 2022/1/7 16:26
 */
class HintManagerPageMsKitView : AbsMsKitView() {
    private var recyclerView: RecyclerView? = null
    private var adapter: HintItemAdapter? = null
    private var hintItems: ArrayList<HintItem> = ArrayList()

    override fun onCreate(context: Context) {

    }

    override fun onCreateView(context: Context?, rootView: FrameLayout?): View? {
        return LayoutInflater.from(context).inflate(R.layout.ms_hint_page_view, rootView, false)
    }

    override fun onViewCreated(rootView: FrameLayout?) {
        initView()
        initData()
    }

    private fun initData() {
        hintItems.add(HintItem("埋点悬浮框", R.drawable.ms_ic_watch, KK_STATISTIC))
        hintItems.add(HintItem("修改渠道号", R.drawable.ms_ic_channel, KK_CHANNEL))
        hintItems.add(HintItem("日志开关", R.drawable.ms_ic_log, KK_SHOW_LOG))
        hintItems.add(HintItem("强制CDN", R.drawable.ms_ic_cdn, KK_CDN))
        hintItems.add(HintItem("剪切板链接跳转", R.drawable.ms_ic_jump, KK_URL))
        hintItems.add(HintItem("强制声网", R.drawable.ms_ic_force_agora, KK_AGORA))
        hintItems.add(HintItem("强制urtc", R.drawable.ms_ic_force_urtc, KK_URTC))
        hintItems.add(HintItem("跳转小程序", R.drawable.ms_ic_mini, KK_MINI))
        hintItems.add(HintItem("测试页面", R.drawable.ms_ic_test, KK_TEST))
        hintItems.add(HintItem("配置信息", R.drawable.ms_ic_switch, KK_SWITCH))
        hintItems.add(HintItem("更多操作", R.drawable.ms_ic_more, MS_MORE))

        adapter?.notifyDataSetChanged()
    }

    private fun initView() {
        val backIcon = findViewById<View>(R.id.back_icon)
        backIcon?.setOnClickListener {
            detach()
        }
        recyclerView = findViewById(R.id.hint_recycler)
        recyclerView?.layoutManager = GridLayoutManager(context, 4)
        adapter = HintItemAdapter(context, hintItems)
        adapter?.listener = object : HintClickListener {
            override fun onClick(item: HintItem, position: Int) {
                when (item.hintKey) {
                    KK_STATISTIC,
                    KK_SHOW_LOG,
                    KK_CDN,
                    KK_URL,
                    KK_AGORA,
                    KK_URTC,
                    KK_TEST,
                    KK_SWITCH
                    -> {
                        MsKit.getProxy()?.checkHint(item.hintKey)
                    }
                    KK_CHANNEL -> {
                        showSetChannelDialog()
                    }
                    KK_MINI -> {
                        showJumpMiniProgramDialog()
                    }
                    MS_MORE -> {
                        showMoreDialog()
                    }
                }
                detach()
            }

        }
        recyclerView?.adapter = adapter
    }

    override fun initMsKitViewLayoutParams(params: MsKitViewLayoutParams) {
        params.x = 0
        params.y = 0
        params.width = MsKitViewLayoutParams.MATCH_PARENT
        params.height = MsKitViewLayoutParams.MATCH_PARENT
    }

    override fun onBackPressed(): Boolean {
        detach()
        return true
    }

    override fun shouldDealBackKey(): Boolean {
        return true
    }

    override fun onHomeKeyPress() {
        detach()
    }

    override fun onRecentAppKeyPress() {
        detach()
    }

    override fun restrictBorderline(): Boolean {
        return false
    }

    override fun canDrag(): Boolean {
        return false
    }

    interface HintClickListener {
        fun onClick(item: HintItem, position: Int)
    }

    private fun showSetChannelDialog() {
        ActivityUtils.getTopActivity()?.run {
            val dialog = AlertDialog.Builder(this).create()
            val view = MsDialogUtil.getInputDialog(
                this,
                "请输入要设置的渠道号",
                "当前渠道号：${MsKit.getProxy()?.debugConfig()?.channel}",
                "渠道号",
                InputType.TYPE_CLASS_NUMBER,
                "确认",
                object :View.OnClickListener{
                    override fun onClick(v: View) {
                        val content = v.tag as? String
                        val key = if (TextUtils.isEmpty(content)) {
                            KK_CHANNEL
                        } else {
                            "$KK_CHANNEL=$content"
                        }
                        MsKit.getProxy()?.checkHint(key)
                        dialog.dismiss()
                    }

                },
                "取消",
                object :View.OnClickListener{
                    override fun onClick(v: View?) {
                        dialog.dismiss()

                    }
                }, object :View.OnClickListener{
                    override fun onClick(v: View?) {
                        dialog.dismiss()
                    }
                })
            dialog.setView(view)
            dialog.setCanceledOnTouchOutside(false)
            dialog.show()
        }
    }

    private fun showJumpMiniProgramDialog() {
        ActivityUtils.getTopActivity()?.run {
            val dialog = AlertDialog.Builder(this).create()
            val view = MsDialogUtil.getInputDialog(
                this,
                "请输入小程序相关参数",
                "小程序path和pid以,分隔", "eg:path,pid",
                InputType.TYPE_CLASS_TEXT,
                "确认",
                object :View.OnClickListener{
                    override fun onClick(v: View) {
                        val content = v.tag as? String
                        DevelopUtil.copyText(content)
                        MsKit.getProxy()?.checkHint(KK_MINI)
                        dialog.dismiss()
                    }

                },
                "取消",
                object :View.OnClickListener{
                    override fun onClick(v: View?) {
                        dialog.dismiss()

                    }
                }, object :View.OnClickListener{
                    override fun onClick(v: View?) {
                        dialog.dismiss()
                    }
                })
            dialog.setView(view)
            dialog.setCanceledOnTouchOutside(false)
            dialog.show()
        }
    }

    private fun showMoreDialog() {
        ActivityUtils.getTopActivity()?.run {
            val dialog = AlertDialog.Builder(this).create()
            val view = MsDialogUtil.getInputDialog(
                this,
                "更多操作",
                "输入操作类型",
                "key",
                InputType.TYPE_CLASS_TEXT,
                "确定",
                object :View.OnClickListener{
                    override fun onClick(v: View) {
                        val content = v.tag as? String
                        content?.run {
                            MsKit.getProxy()?.checkHint(this)
                        }
                        dialog.dismiss()
                    }

                },
                "取消",
                object :View.OnClickListener{
                    override fun onClick(v: View?) {
                        dialog.dismiss()
                    }

                }, object :View.OnClickListener{
                    override fun onClick(v: View?) {
                        dialog.dismiss()
                    }

                })
            dialog.setView(view)
            dialog.setCanceledOnTouchOutside(false)
            dialog.show()
        }
    }
}
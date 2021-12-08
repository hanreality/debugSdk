package com.melot.android.debug.sdk.kit.toolpanel

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import android.widget.FrameLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.melot.android.debug.sdk.MsKit
import com.melot.android.debug.sdk.R
import com.melot.android.debug.sdk.core.AbsMsKitView
import com.melot.android.debug.sdk.core.MsKitManager
import com.melot.android.debug.sdk.core.MsKitViewLayoutParams
import com.melot.android.debug.sdk.core.MsKitViewManager
import com.melot.android.debug.sdk.util.MsKitSystemUtil
import com.melot.android.debug.sdk.util.ActivityUtils

/**
 * Author: han.chen
 * Time: 2021/12/7 13:49
 */
class ToolPanelMsKitView : AbsMsKitView() {
    private lateinit var adapter: ToolPanelAdapter
    private var mKits: MutableList<KitWrapItem> = mutableListOf()

    override fun onCreate(context: Context) {

    }

    override fun onCreateView(context: Context?, rootView: FrameLayout?): View? {
        return LayoutInflater.from(context).inflate(R.layout.ms_tool_panel, rootView, false)
    }

    override fun onViewCreated(rootView: FrameLayout?) {
        generateKits()
        initView()
    }

    private fun initView() {
        val backIcon = findViewById<View>(R.id.back_icon)
        backIcon?.setOnClickListener {
            detach()
        }
        adapter = ToolPanelAdapter(context, mKits)
        val gridLayoutManager = GridLayoutManager(activity, 4)
        gridLayoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                val viewType = mKits[position].itemType
                return if (viewType == KitWrapItem.TYPE_KIT) {
                    1
                } else {
                    4
                }
            }
        }
        adapter.mItemClickListener = object :ToolPanelAdapter.OnItemClickListener{
            override fun onItemClickListener(item: KitWrapItem) {
                try {
                    if (item.itemType == KitWrapItem.TYPE_KIT) {
                        item.kit?.let {
                            if (it.onClick(ActivityUtils.getTopActivity())) {
                                MsKitViewManager.INSTANCE.detachToolPanel()
                            }
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
        val rvKits = findViewById<RecyclerView>(R.id.rv_kits)
        rvKits?.layoutManager = gridLayoutManager
        rvKits?.adapter = adapter
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

    override fun onResume() {
        super.onResume()
        resumeData()
    }

    private fun resumeData() {
        mKits.clear()
        generateKits()
    }

    override fun restrictBorderline(): Boolean {
        return false
    }

    private fun generateKits() {
        MsKitManager.GLOBAL_KITS.forEach { group ->
            when (group.key) {
                MsKit.requireApp().getString(R.string.debug_category_exit) -> {
                    mKits.add(KitWrapItem(KitWrapItem.TYPE_EXIT, name = group.key, kit = null))
                }
                MsKitManager.GROUP_ID_COMMON,
                MsKitManager.GROUP_ID_UI -> {
                    if (group.value.size != 0) {
                        mKits.add(
                            KitWrapItem(
                                KitWrapItem.TYPE_TITLE,
                                name = MsKit.requireApp()
                                    .getString(MsKitSystemUtil.getStringId(group.key)),
                                kit = null
                            )
                        )
                    }
                    group.value.forEach { kitWrap ->
                        if (kitWrap.checked) {
                            mKits.add(kitWrap)
                        }
                    }
                }
                else -> {
                    if (group.value.size != 0) {
                        mKits.add(KitWrapItem(KitWrapItem.TYPE_TITLE, name = group.key, kit = null))
                        group.value.forEach { kitWrap ->
                            if (kitWrap.checked) {
                                mKits.add(kitWrap)
                            }
                        }
                    }
                }
            }
        }
    }

    override fun canDrag(): Boolean {
        return false
    }

}
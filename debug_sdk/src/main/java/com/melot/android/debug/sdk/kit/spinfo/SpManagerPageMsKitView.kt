package com.melot.android.debug.sdk.kit.spinfo

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.melot.android.debug.sdk.R
import com.melot.android.debug.sdk.core.AbsMsKitView
import com.melot.android.debug.sdk.core.MsKitViewLayoutParams
import com.melot.android.debug.sdk.util.MMKVUtil

/**
 * Author: han.chen
 * Time: 2022/1/7 16:26
 */
class SpManagerPageMsKitView : AbsMsKitView() {
    private var spRecyclerView: RecyclerView? = null
    private var adapter: SpItemAdapter? = null
    private var spItems: ArrayList<SpItem> = ArrayList()

    override fun onCreate(context: Context) {

    }

    override fun onCreateView(context: Context?, rootView: FrameLayout?): View? {
        return LayoutInflater.from(context).inflate(R.layout.ms_sp_page_view, rootView, false)
    }

    override fun onViewCreated(rootView: FrameLayout?) {
        initData()
        initView()
    }

    private fun initData() {
        spItems.clear()
        MMKVUtil.allKeys().iterator().forEach {
            val spItem = SpItem(it, MMKVUtil.getObjectValue(it), MMKVUtil.getObjectType(it))
            spItems.add(spItem)
        }
    }

    private fun initView() {
        val backIcon = findViewById<View>(R.id.back_icon)
        backIcon?.setOnClickListener {
            detach()
        }
        spRecyclerView = findViewById(R.id.sp_recycler)
        spRecyclerView?.layoutManager = LinearLayoutManager(context)
        adapter = SpItemAdapter(context, spItems)
        spRecyclerView?.adapter = adapter
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

    interface SpClickListener {
        fun onDelete(item: SpItem, position: Int)

        fun onModify(item: SpItem, position: Int)
    }

}
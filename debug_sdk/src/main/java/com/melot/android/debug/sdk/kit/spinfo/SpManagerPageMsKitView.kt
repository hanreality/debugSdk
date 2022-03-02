package com.melot.android.debug.sdk.kit.spinfo

import android.content.Context
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.TextView
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
    private var spSearchInput: EditText? = null
    private var spSearchBtn: TextView? = null

    override fun onCreate(context: Context) {

    }

    override fun onCreateView(context: Context?, rootView: FrameLayout?): View? {
        return LayoutInflater.from(context).inflate(R.layout.ms_sp_page_view, rootView, false)
    }

    override fun onViewCreated(rootView: FrameLayout?) {
        initView()
        initData()
    }

    private fun initData() {
        spItems.clear()
        MMKVUtil.allKeys().iterator().forEach {
            val spItem = SpItem(it, MMKVUtil.getObjectValue(it), MMKVUtil.getObjectType(it))
            spItems.add(spItem)
        }
        adapter?.notifyDataSetChanged()
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
        spSearchInput = findViewById(R.id.ms_sp_search_input)
        spSearchBtn = findViewById(R.id.ms_sp_search_btn)
        spSearchInput?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                spSearchBtn?.isEnabled = s?.isNotEmpty() == true
                if (s.isNullOrEmpty()) {
                    initData()
                } else {
                    filter(s.toString())
                }
            }

        })
        spSearchInput?.setOnKeyListener { v, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_ENTER) {
                spSearchBtn?.performClick()
            }
            return@setOnKeyListener true
        }
        spSearchBtn?.setOnClickListener {
            spSearchInput?.text?.run {
                filter(this.toString())
            }
        }
    }

    private fun filter(searchKey: String) {
        spItems.clear()
        MMKVUtil.allKeys().iterator().forEach {
            if (it.contains(searchKey)) {
                val spItem = SpItem(it, MMKVUtil.getObjectValue(it), MMKVUtil.getObjectType(it))
                spItems.add(spItem)
            }
        }
        adapter?.notifyDataSetChanged()
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
package com.melot.android.debug.sdk.kit.quicklogin

import android.content.Context
import android.os.Debug
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.reflect.TypeToken
import com.melot.android.debug.sdk.MsKit
import com.melot.android.debug.sdk.R
import com.melot.android.debug.sdk.core.AbsMsKitView
import com.melot.android.debug.sdk.core.MsKitViewLayoutParams
import com.melot.android.debug.sdk.model.DebugTestAccountData
import com.melot.android.debug.sdk.model.DebugTestAccountModel
import com.melot.android.debug.sdk.util.ActivityUtils
import com.melot.android.debug.sdk.util.DebugGsonUtil
import com.melot.android.debug.sdk.util.DevelopUtil
import okhttp3.*
import java.io.IOException

/**
 * Author: han.chen
 * Time: 2021/12/22 10:53
 */
class QuickLoginInfoMsKitView : AbsMsKitView(), View.OnClickListener {

    private var accountRecycler: RecyclerView? = null
    private var close: ImageView? = null
    private var adapter: AccountAdapter? = null
    private var model: DebugTestAccountModel? = null

    override fun onCreate(context: Context) {

    }

    override fun onCreateView(context: Context?, rootView: FrameLayout?): View? {
        return LayoutInflater.from(context).inflate(R.layout.ms_float_view_quick_login_info, null)
    }

    override fun onViewCreated(rootView: FrameLayout?) {
        accountRecycler = rootView?.findViewById(R.id.account_recycler)
        accountRecycler?.layoutManager = LinearLayoutManager(context)
        adapter = AccountAdapter(context)
        accountRecycler?.adapter = adapter
        close = findViewById(R.id.close)
        close?.setOnClickListener(this)
        getData()
    }

    override fun initMsKitViewLayoutParams(params: MsKitViewLayoutParams) {
        params.flags = MsKitViewLayoutParams.FLAG_NOT_FOCUSABLE
        params.x = 0
        params.y = DevelopUtil.getAppScreenHeight() - DevelopUtil.dp2px(185f)
        params.width = screenShortSideLength
        params.height = MsKitViewLayoutParams.WRAP_CONTENT
    }

    override fun onClick(v: View?) {
        if (v == close) {
            MsKit.removeFloating(QuickLoginInfoMsKitView::class.java)
        }
    }

    private fun getData() {
        val url =
            "http://10.0.6.34:8049/getTestAccountList?debug=" + MsKit.getProxy()
                ?.debugConfig()?.serverDebug + "&appName=" + MsKit.getProxy()
                ?.debugConfig()?.appName
        val okHttpClient = OkHttpClient()
        val request = Request.Builder()
            .url(url)
            .get()
            .build()
        val call = okHttpClient.newCall(request)
        call.enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                val responseStr = response.body()?.string()
                model = DebugGsonUtil.GsonToBean(
                    responseStr,
                    object : TypeToken<DebugTestAccountModel>() {}.type
                )
                post(Runnable {
                    adapter?.appendData(model?.data)
                })
            }
        })
    }

    inner class AccountAdapter(var context: Context?) : RecyclerView.Adapter<AccountViewHolder>() {

        private val accounts = ArrayList<DebugTestAccountData>()

        fun appendData(list: ArrayList<DebugTestAccountData>?) {
            list?.let {
                accounts.clear()
                accounts.addAll(it)
                notifyDataSetChanged()
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AccountViewHolder {
            return AccountViewHolder(context, parent)
        }

        override fun onBindViewHolder(holder: AccountViewHolder, position: Int) {
            holder.bindData(accounts[position])
        }

        override fun getItemCount(): Int {
            return accounts.size
        }

    }

    inner class AccountViewHolder(context: Context?, parent: ViewGroup) : RecyclerView.ViewHolder(
        LayoutInflater.from(context).inflate(R.layout.ms_item_account_view, parent, false)
    ) {
        private var accountId = itemView.findViewById<TextView>(R.id.account_id)
        private var accountDetail = itemView.findViewById<TextView>(R.id.account_detail)

        fun bindData(data: DebugTestAccountData) {
            accountId.text = data.info
            accountDetail.text = data.detail
            itemView.setOnClickListener {
                MsKit.getProxy()?.quickLogin(
                    data.accountId,
                    data.password
                )
                MsKit.removeFloating(QuickLoginInfoMsKitView::class.java)
            }
        }
    }
}
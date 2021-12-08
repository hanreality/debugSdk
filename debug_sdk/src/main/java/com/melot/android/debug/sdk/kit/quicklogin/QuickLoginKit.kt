package com.melot.android.debug.sdk.kit.quicklogin

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.google.auto.service.AutoService
import com.google.gson.reflect.TypeToken
import com.melot.android.debug.sdk.MsKit
import com.melot.android.debug.sdk.R
import com.melot.android.debug.sdk.kit.AbstractKit
import com.melot.android.debug.sdk.model.DebugTestAccountData
import com.melot.android.debug.sdk.model.DebugTestAccountModel
import com.melot.android.debug.sdk.util.DebugGsonUtil
import okhttp3.*
import java.io.IOException

/**
 * Author: han.chen
 * Time: 2021/12/7 17:58
 */
@AutoService(AbstractKit::class)
class QuickLoginKit : AbstractKit() {
    override val name: Int
        get() = R.string.ms_quick_login
    override val icon: Int
        get() = R.drawable.ms_ic_quick_login

    var alertDialog: AlertDialog? = null
    var model: DebugTestAccountModel? = null
    override fun onAppInit(context: Context?) {
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
                val responseStr = response.body?.string()
                model = DebugGsonUtil.GsonToBean(
                    responseStr,
                    object : TypeToken<DebugTestAccountModel>() {}.type
                )
            }
        })
    }

    override val isInnerKit: Boolean
        get() = true

    override fun innerKitId(): String {
        return "mskit_sdk_common_quick_login"
    }

    override fun onClick(activity: Activity?): Boolean {
        showQuickDialog(activity)
        return true
    }

    private fun showQuickDialog(activity: Activity?) {
        activity?.let { _activity ->
            val adapter = AccountAdapter(
                _activity, model?.data
            )
            alertDialog = AlertDialog.Builder(
                _activity,
                R.style.MyDialogTheme
            )
                .setTitle("请选择要登陆的账号")
                .setAdapter(
                    adapter
                ) { dialog, which ->
                    model?.data?.let {
                        dialog?.dismiss()
                        MsKit.getProxy()?.quickLogin(
                            it[which].accountId,
                            it[which].password
                        )
                        alertDialog = null
                    }
                }
                .create()
            alertDialog?.show()
        }

    }

    inner class AccountAdapter(val context: Context, val items: ArrayList<DebugTestAccountData>?) :
        BaseAdapter() {

        override fun getCount(): Int {
            return items?.size ?: 0
        }

        override fun getItem(position: Int): DebugTestAccountData? {
            return items?.get(position)
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val holder: AccountHolder
            val view: View
            if (convertView == null) {
                view = LayoutInflater.from(context)
                    .inflate(R.layout.layout_debug_account_view, parent, false)
                holder = AccountHolder(view)
                view.tag = holder
            } else {
                holder = convertView.tag as AccountHolder
                view = convertView
            }
            holder.bindData(items?.get(position))
            return view
        }

    }

    inner class AccountHolder(val itemView: View) {

        private val accountId: TextView = itemView.findViewById(R.id.account_id)

        fun bindData(model: DebugTestAccountData?) {
            model?.let {
                accountId.text = model.info
            }
        }

    }
}
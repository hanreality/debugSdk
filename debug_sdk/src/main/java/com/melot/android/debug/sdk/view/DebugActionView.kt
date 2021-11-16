package com.melot.android.debug.sdk.view

import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import com.melot.android.debug.sdk.DebugManager
import com.melot.android.debug.sdk.R
import com.melot.android.debug.sdk.model.DebugLoginModel
import com.melot.android.debug.sdk.proxy.DebugConfig

/**
 * Author: han.chen
 * Time: 2021/9/13 11:29
 */
class DebugActionView @JvmOverloads constructor(
    context: Context?,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {
    val container:View
    lateinit var closeView: View
    lateinit var exit: View
    lateinit var changeServer: TextView
    lateinit var changeLanguage: View
    lateinit var getCurrentPage: View
    lateinit var currentPageInfo: TextView
    lateinit var quickLogin: View
    lateinit var switchFragmentInfo: Switch
    var config: DebugConfig? = null
    var dialog: AlertDialog? = null
    var switchFragmentCallBack: CompoundButton.OnCheckedChangeListener? = null

    init {
        gravity = Gravity.CENTER
        orientation = VERTICAL
        inflate(context, R.layout.layout_debug_action_view, this)
        container = findViewById(R.id.container)
        container.setOnClickListener {
            dismiss()
        }
        config = DebugManager.INSTANCE.debugProxy?.debugConfig()
        initView(this)
    }


    private fun initView(view: View) {
        closeView = view.findViewById(R.id.close_view)
        closeView.setOnClickListener {
            dismiss()
        }

        currentPageInfo = view.findViewById(R.id.current_page_info)
        getCurrentPage = view.findViewById(R.id.get_current_page_info)
        getCurrentPage.setOnClickListener {
            DebugManager.INSTANCE.currentActivity?.let { activity ->
                currentPageInfo.visibility = View.VISIBLE
                val info = StringBuilder()
                    .append("Activity: ")
                    .append(activity::class.java.name)

                activity.intent.extras?.let { bundle ->
                    val extras = StringBuilder()
                    bundle.keySet().forEach {
                        extras.append(it).append("=").append(bundle[it]).append(",")
                    }
                    extras.replace(extras.lastIndex, extras.length, "")
                    info.append("{Bundle[${extras}]}")
                }
                currentPageInfo.text = info.toString()
            }
        }

        switchFragmentInfo = view.findViewById(R.id.switch_compat_fragment)
        switchFragmentInfo.setOnCheckedChangeListener { buttonView, isChecked ->
            switchFragmentCallBack?.onCheckedChanged(buttonView, isChecked)
        }

        quickLogin = view.findViewById(R.id.quick_login)
        quickLogin.setOnClickListener {
            showQuickDialog()
            dismiss()
        }

        changeServer = view.findViewById(R.id.change_server)
        config?.let {
            if (it.serverDebug) {
                changeServer.text = "切换服务器（当前测试服）"
            } else {
                changeServer.text = "切换服务器（当前正式服）"
            }
        }
        changeServer.setOnClickListener {
            DebugManager.INSTANCE.debugProxy?.changeServer()
            dismiss()
        }

        changeLanguage = view.findViewById(R.id.change_language)
        changeLanguage.setOnClickListener {
            dismiss()
            switchLanguage()
        }

        exit = view.findViewById(R.id.exit)
        exit.setOnClickListener {
            DebugManager.INSTANCE.debugProxy?.disable()
            DebugManager.INSTANCE.enable = false
            dismiss()
        }

    }

    private fun switchLanguage() {
        DebugManager.INSTANCE.currentActivity?.startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
    }

    private fun showQuickDialog() {
        val items = DebugManager.INSTANCE.debugProxy?.getTestAccounts()
        val adapter = AccountAdapter(
            DebugManager.INSTANCE.currentActivity as Context,
            items
        )
        dialog = AlertDialog.Builder(DebugManager.INSTANCE.currentActivity as Context, R.style.MyDialogTheme)
            .setTitle("请选择要登陆的账号")
            .setAdapter(
                adapter
            ) { dialog, which ->
                items?.let {
                    dialog?.dismiss()
                    DebugManager.INSTANCE.debugProxy?.quickLogin(it[which].id, it[which].pwd)
                }
            }
            .create()
        dialog?.show()
    }

    fun dismiss() {
        visibility = View.GONE
    }

    fun show() {
        visibility = View.VISIBLE
    }

    inner class AccountAdapter(val context: Context, val items: ArrayList<DebugLoginModel>?) :
        BaseAdapter() {

        override fun getCount(): Int {
            return items?.size ?: 0
        }

        override fun getItem(position: Int): DebugLoginModel? {
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

        fun bindData(model: DebugLoginModel?) {
            model?.let {
                accountId.text = model.displayName
            }
        }

    }
}
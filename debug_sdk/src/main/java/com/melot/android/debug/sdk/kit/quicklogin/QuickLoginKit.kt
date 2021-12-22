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
import com.melot.android.debug.sdk.kit.pageinfo.ViewPageInfoMsKitView
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

    override fun onAppInit(context: Context?) {}

    override val isInnerKit: Boolean
        get() = true

    override fun innerKitId(): String {
        return "mskit_sdk_common_quick_login"
    }

    override fun onClick(activity: Activity?): Boolean {
        MsKit.launchFloating<QuickLoginInfoMsKitView>()
        return true
    }
}
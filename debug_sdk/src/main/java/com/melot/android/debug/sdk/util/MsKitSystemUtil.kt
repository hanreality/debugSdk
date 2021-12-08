package com.melot.android.debug.sdk.util

import android.app.Activity
import android.content.Context
import com.melot.android.debug.sdk.core.ActivityLifecycleStatusInfo
import com.melot.android.debug.sdk.core.MsKitManager
import android.content.res.AssetManager
import android.content.res.Resources
import android.util.Log
import androidx.annotation.StringRes
import com.melot.android.debug.sdk.MsKit
import java.io.*
import java.lang.Exception
import java.lang.StringBuilder


/**
 * Author: han.chen
 * Time: 2021/12/3 17:24
 */
object MsKitSystemUtil {

    fun isMainLaunchActivity(activity: Activity?): Boolean {
        val packageManager = activity?.application?.packageManager
        val intent = packageManager?.getLaunchIntentForPackage(activity.packageName) ?: return false
        val launchComponentName = intent.component
        val componentName = activity.componentName
        if (launchComponentName != null && componentName.toString() == launchComponentName.toString()) {
            return true
        }
        return false
    }

    fun isOnlyFirstLaunchActivity(activity: Activity?): Boolean {
        val isMainActivity = isMainLaunchActivity(activity)
        val activityLifecycleInfo: ActivityLifecycleStatusInfo? =
            MsKitManager.ACTIVITY_LIFECYCLE_INFO_MAP[activity?.javaClass?.canonicalName]
        return activityLifecycleInfo != null && isMainActivity && !activityLifecycleInfo.isInvokeStopMethod
    }

    /**
     * 读取asset得到json文件的内容
     * @param context
     * @param fileName
     * @return
     */
    fun getAssetsJson(context: Context, fileName: String?): String? {
        val stringBuilder = StringBuilder()
        val assetManager: AssetManager = context.getAssets()
        try {
            val bufferedReader = BufferedReader(
                InputStreamReader(
                    assetManager.open(fileName!!), "utf-8"
                )
            )
            var line: String?
            while (bufferedReader.readLine().also { line = it } != null) {
                stringBuilder.append(line)
            }
            bufferedReader.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return stringBuilder.toString()
    }

    @StringRes
    @JvmStatic
    fun getStringId(str: String): Int {
        try {
            val r: Resources = MsKit.requireApp().resources
            return r.getIdentifier(str, "string", MsKit.requireApp().packageName)
        } catch (e: Exception) {
            Log.e("getStringId", "getStringId===>$str")
        }
        return -1
    }
}

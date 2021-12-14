package com.melot.android.debug.sdk.util

import android.app.Activity
import android.app.AppOpsManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Process
import android.provider.Settings
import android.util.Log
import java.lang.reflect.InvocationTargetException

/**
 * Author: han.chen
 * Time: 2021/12/13 12:19
 */
object MsKitPermissionUtil {
    private const val TAG = "MsKitPermissionUtil"
    private const val OP_SYSTEM_ALERT_WINDOW = 24

    /**
     * 判断是否具有悬浮窗权限
     * @param context
     * @return
     */
    fun canDrawOverlays(context: Context?): Boolean {
        //android 6.0及以上的判断条件
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Settings.canDrawOverlays(context)
        } else checkOp(context, OP_SYSTEM_ALERT_WINDOW)
        //android 4.4~6.0的判断条件
    }

    private fun checkOp(context: Context?, op: Int): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            val manager = context?.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
            val clazz: Class<*> = AppOpsManager::class.java
            try {
                val method = clazz.getDeclaredMethod(
                    "checkOp",
                    Int::class.javaPrimitiveType,
                    Int::class.javaPrimitiveType,
                    String::class.java
                )
                return AppOpsManager.MODE_ALLOWED == method.invoke(
                    manager,
                    op,
                    Process.myUid(),
                    context.packageName
                ) as Int
            } catch (e: NoSuchMethodException) {
                e.printStackTrace()
            } catch (e: IllegalAccessException) {
                e.printStackTrace()
            } catch (e: InvocationTargetException) {
                e.printStackTrace()
            }
        }
        return true
    }

    /**
     * 请求悬浮窗权限
     * @param context
     */
    fun requestDrawOverlays(context: Context) {
        val intent = Intent(
            "android.settings.action.MANAGE_OVERLAY_PERMISSION",
            Uri.parse("package:" + context.packageName)
        )
        if (context !is Activity) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        if (intent.resolveActivity(context.packageManager) != null) {
            context.startActivity(intent)
        } else {
            Log.e(TAG, "No activity to handle intent")
        }
    }
}
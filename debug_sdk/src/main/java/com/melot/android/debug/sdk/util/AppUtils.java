package com.melot.android.debug.sdk.util;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;

import com.melot.android.debug.sdk.MsKit;

import java.util.List;

/**
 * Author: han.chen
 * Time: 2021/11/17 14:58
 */
public class AppUtils {

    public static void relaunchApp(boolean isKillProcess) {
        Intent intent = getLaunchAppIntent(MsKit.requireApp().getPackageName());
        if (intent == null) {
            Log.e("AppUtils", "Didn't exist launcher activity.");
            return;
        }
        intent.addFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK
                        | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK
        );
        MsKit.requireApp().startActivity(intent);
        if (!isKillProcess) return;
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(0);
    }
    private static Intent getLaunchAppIntent(String pkgName) {
        String launcherActivity = getLauncherActivity(pkgName);
        if (TextUtils.isEmpty(launcherActivity)) {
            return null;
        }
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.setClassName(pkgName, launcherActivity);
        return intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    }
    private static String getLauncherActivity(@NonNull String pkg) {
        if (TextUtils.isEmpty(pkg)) {
            return "";
        }
        Intent intent = new Intent(Intent.ACTION_MAIN, null);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.setPackage(pkg);
        PackageManager pm = MsKit.requireApp().getPackageManager();
        List<ResolveInfo> info = pm.queryIntentActivities(intent, 0);
        if (info == null || info.size() == 0) {
            return "";
        }
        return info.get(0).activityInfo.name;
    }
}

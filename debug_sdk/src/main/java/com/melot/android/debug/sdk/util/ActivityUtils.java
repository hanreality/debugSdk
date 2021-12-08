package com.melot.android.debug.sdk.util;

import android.app.Activity;
import android.app.Application;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Author: han.chen
 * Time: 2021/12/7 20:30
 */
public class ActivityUtils {
    private final static LinkedList<Activity> sActivityList = new LinkedList<>();
    private static boolean mIsBackground = false;
    private static int mForegroundCount = 0;
    private static int mConfigCount = 0;

    public static Activity getTopActivity() {
        List<Activity> activityList = getActivityList();
        for (Activity activity : activityList) {
            if (!isActivityAlive(activity)) {
                continue;
            }
            return activity;
        }
        return null;
    }

    public static List<Activity> getActivityList() {
        if (!sActivityList.isEmpty()) {
            return new LinkedList<>(sActivityList);
        }
        List<Activity> reflectActivities = getActivitiesByReflect();
        sActivityList.addAll(reflectActivities);
        return new LinkedList<>(sActivityList);
    }

    public static Activity findActivity(View view) {
        List<Activity> activityList = getActivityList();
        for(Activity activity : activityList) {
            if (view == activity.getWindow().getDecorView().getRootView()) {
                return activity;
            }
        }
        return null;
    }

    /**
     * @return the activities which topActivity is first position
     */
    private static List<Activity> getActivitiesByReflect() {
        LinkedList<Activity> list = new LinkedList<>();
        Activity topActivity = null;
        try {
            Object activityThread = getActivityThread();
            Field mActivitiesField = activityThread.getClass().getDeclaredField("mActivities");
            mActivitiesField.setAccessible(true);
            Object mActivities = mActivitiesField.get(activityThread);
            if (!(mActivities instanceof Map)) {
                return list;
            }
            Map<Object, Object> binder_activityClientRecord_map = (Map<Object, Object>) mActivities;
            for (Object activityRecord : binder_activityClientRecord_map.values()) {
                Class activityClientRecordClass = activityRecord.getClass();
                Field activityField = activityClientRecordClass.getDeclaredField("activity");
                activityField.setAccessible(true);
                Activity activity = (Activity) activityField.get(activityRecord);
                if (topActivity == null) {
                    Field pausedField = activityClientRecordClass.getDeclaredField("paused");
                    pausedField.setAccessible(true);
                    if (!pausedField.getBoolean(activityRecord)) {
                        topActivity = activity;
                    } else {
                        list.add(activity);
                    }
                } else {
                    list.add(activity);
                }
            }
        } catch (Exception e) {
            Log.e("UtilsActivityLifecycle", "getActivitiesByReflect: " + e.getMessage());
        }
        if (topActivity != null) {
            list.addFirst(topActivity);
        }
        return list;
    }

    private static Object getActivityThread() {
        Object activityThread = getActivityThreadInActivityThreadStaticField();
        if (activityThread != null) return activityThread;
        return getActivityThreadInActivityThreadStaticMethod();
    }

    private static Object getActivityThreadInActivityThreadStaticField() {
        try {
            Class activityThreadClass = Class.forName("android.app.ActivityThread");
            Field sCurrentActivityThreadField = activityThreadClass.getDeclaredField("sCurrentActivityThread");
            sCurrentActivityThreadField.setAccessible(true);
            return sCurrentActivityThreadField.get(null);
        } catch (Exception e) {
            Log.e("UtilsActivityLifecycle", "getActivityThreadInActivityThreadStaticField: " + e.getMessage());
            return null;
        }
    }

    private static Object getActivityThreadInActivityThreadStaticMethod() {
        try {
            Class activityThreadClass = Class.forName("android.app.ActivityThread");
            return activityThreadClass.getMethod("currentActivityThread").invoke(null);
        } catch (Exception e) {
            Log.e("UtilsActivityLifecycle", "getActivityThreadInActivityThreadStaticMethod: " + e.getMessage());
            return null;
        }
    }

    public static boolean isActivityAlive(final Activity activity) {
        return activity != null && !activity.isFinishing()
                && (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1 || !activity.isDestroyed());
    }

    private static void setTopActivity(final Activity activity) {
        if (sActivityList.contains(activity)) {
            if (!sActivityList.getFirst().equals(activity)) {
                sActivityList.remove(activity);
                sActivityList.addFirst(activity);
            }
        } else {
            sActivityList.addFirst(activity);
        }
    }


    public static void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {
        setTopActivity(activity);
    }

    public static void onActivityStarted(@NonNull Activity activity) {
        if (!mIsBackground) {
            setTopActivity(activity);
        }
        if (mConfigCount < 0) {
            ++mConfigCount;
        } else {
            ++mForegroundCount;
        }
    }

    public static void onActivityResumed(@NonNull Activity activity) {
        if (mIsBackground) {
            mIsBackground = false;
        }
    }

    public static void onActivityPaused(@NonNull Activity activity) {

    }

    public static void onActivityStopped(@NonNull Activity activity) {
        if (activity.isChangingConfigurations()) {
            --mConfigCount;
        } else {
            --mForegroundCount;
            if (mForegroundCount <= 0) {
                mIsBackground = true;
            }
        }
    }


    public static void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {

    }

    public static void onActivityDestroyed(@NonNull Activity activity) {
        sActivityList.remove(activity);
    }

    public static void unInit() {
        sActivityList.clear();
    }
}

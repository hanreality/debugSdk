package com.melot.android.debug.sdk.util;

import com.melot.android.KKSp;

/**
 * Author: han.chen
 * Time: 2021/12/7 10:01
 */
public class MMKVUtil {
    private static KKSp kkSp;
    private static KKSp getKKsp() {
        if (kkSp == null) {
            kkSp = KKSp.getSp("ms_debug");
        }
        return kkSp;
    }

    public static void setBoolean(String key, boolean value) {
        getKKsp().putBoolean(key, value);
    }

    public static void setString(String key, String value) {
        getKKsp().putString(key, value);
    }

    public static void setInt(String key, int value) {
        getKKsp().putInt(key, value);
    }

    public static void setFloat(String key, float value) {
        getKKsp().putFloat(key, value);
    }

    public static void setLong(String key, long value) {
        getKKsp().putLong(key, value);
    }

    public static boolean getBoolean(String key) {
        return getBoolean(key, false);
    }

    public static boolean getBoolean(String key, boolean defaultValue) {
        return getKKsp().getBoolean(key, defaultValue);
    }

    public static int getInt(String key) {
        return getInt(key, 0);
    }

    public static int getInt(String key, int defaultValue) {
        return getKKsp().getInt(key, defaultValue);
    }

    public static String getString(String key) {
        return getString(key, "");
    }

    public static String getString(String key, String defaultValue) {
        return getKKsp().getString(key, defaultValue);
    }

    public static float getFloat(String key) {
        return getFloat(key, 0);
    }

    public static float getFloat(String key, float defaultValue) {
        return getKKsp().getFloat(key, defaultValue);
    }

    public static long getLong(String key) {
        return getLong(key, 0L);
    }

    public static long getLong(String key, long defaultValue) {
        return getKKsp().getLong(key, defaultValue);
    }

    public static void removeData(String key) {
        getKKsp().remove(key);
    }

}

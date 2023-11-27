package com.melot.android.debug.sdk.util;

import android.text.TextUtils;

import com.melot.android.debug.sdk.MsKit;
import com.melot.android.debug.sdk.proxy.IDebugProxy;
import com.tencent.mmkv.MMKV;

import java.util.Set;

/**
 * Author: han.chen
 * Time: 2021/12/7 10:01
 */
public class MMKVUtil {
    private static MMKV mmkv;
    private static MMKV getMMKV() {
        if (mmkv == null) {
            IDebugProxy proxy = MsKit.INSTANCE.getProxy();
            mmkv = (proxy == null || proxy.getMMKV() == null) ? MMKV.defaultMMKV() : proxy.getMMKV();
        }
        return mmkv;
    }

    public static void setBoolean(String key, boolean value) {
        getMMKV().putBoolean(key, value);
    }

    public static void setString(String key, String value) {
        getMMKV().putString(key, value);
    }

    public static void setInt(String key, int value) {
        getMMKV().putInt(key, value);
    }

    public static void setFloat(String key, float value) {
        getMMKV().putFloat(key, value);
    }

    public static void setLong(String key, long value) {
        getMMKV().putLong(key, value);
    }

    public static boolean getBoolean(String key) {
        return getBoolean(key, false);
    }

    public static boolean getBoolean(String key, boolean defaultValue) {
        return getMMKV().getBoolean(key, defaultValue);
    }

    public static int getInt(String key) {
        return getInt(key, 0);
    }

    public static int getInt(String key, int defaultValue) {
        return getMMKV().getInt(key, defaultValue);
    }

    public static String getString(String key) {
        return getString(key, "");
    }

    public static String getString(String key, String defaultValue) {
        return getMMKV().getString(key, defaultValue);
    }

    public static float getFloat(String key) {
        return getFloat(key, 0);
    }

    public static float getFloat(String key, float defaultValue) {
        return getMMKV().getFloat(key, defaultValue);
    }

    public static long getLong(String key) {
        return getLong(key, 0L);
    }

    public static long getLong(String key, long defaultValue) {
        return getMMKV().getLong(key, defaultValue);
    }

    public static void removeData(String key) {
        getMMKV().remove(key);
    }

    public static String[] allKeys() {
        return getMMKV().allKeys();
    }

    public static Object getObjectValue(String key) {
        String value = getString(key);
        if (!TextUtils.isEmpty(value)) {
            if (value.charAt(0) == 0x01) {
                return getMMKV().decodeStringSet(key);
            } else {
                return value;
            }
        }
        Set<String> set = getMMKV().decodeStringSet(key);
        if (set != null && set.size() == 0) {
            float valueFloat = getMMKV().decodeFloat(key);
            double valueDouble = getMMKV().decodeDouble(key);
            if (Float.compare(valueFloat, 0f) == 0 || Float.compare(valueFloat, Float.NaN) == 0) {
                return valueFloat;
            } else {
                return valueDouble;
            }
        }

        int valueInt = getMMKV().decodeInt(key);
        long valueLong = getMMKV().decodeLong(key);
        if (valueInt != valueLong) {
            return valueLong;
        } else {
            return valueInt;
        }
    }

    public static String getObjectType(String key) {
        String value = getString(key);
        if (!TextUtils.isEmpty(value)) {
            if (value.charAt(0) == 0x01) {
                return "StringSet";
            } else {
                return "String";
            }
        }
        Set<String> set = getMMKV().decodeStringSet(key);
        if (set != null && set.size() == 0) {
            float valueFloat = getMMKV().decodeFloat(key);
            if (Float.compare(valueFloat, 0f) == 0 || Float.compare(valueFloat, Float.NaN) == 0) {
                return "double";
            } else {
                return "float";
            }
        }

        int valueInt = getMMKV().decodeInt(key);
        long valueLong = getMMKV().decodeLong(key);
        if (valueInt != valueLong) {
            return "long";
        } else {
            return "int";
        }
    }

}

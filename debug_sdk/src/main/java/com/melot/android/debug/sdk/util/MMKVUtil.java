package com.melot.android.debug.sdk.util;

import android.text.TextUtils;

import com.melot.android.KKSp;

import java.util.Set;

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

    public static String[] allKeys() {
        return getKKsp().allKeys();
    }

    public static Object getObjectValue(String key) {
        String value = getString(key);
        if (!TextUtils.isEmpty(value)) {
            if (value.charAt(0) == 0x01) {
                return getKKsp().getMmkv().decodeStringSet(key);
            } else {
                return value;
            }
        }
        Set<String> set = getKKsp().getMmkv().decodeStringSet(key);
        if (set != null && set.size() == 0) {
            float valueFloat = getKKsp().getMmkv().decodeFloat(key);
            double valueDouble = getKKsp().getMmkv().decodeDouble(key);
            if (Float.compare(valueFloat, 0f) == 0 || Float.compare(valueFloat, Float.NaN) == 0) {
                return valueFloat;
            } else {
                return valueDouble;
            }
        }

        int valueInt = getKKsp().getMmkv().decodeInt(key);
        long valueLong = getKKsp().getMmkv().decodeLong(key);
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
        Set<String> set = getKKsp().getMmkv().decodeStringSet(key);
        if (set != null && set.size() == 0) {
            float valueFloat = getKKsp().getMmkv().decodeFloat(key);
            if (Float.compare(valueFloat, 0f) == 0 || Float.compare(valueFloat, Float.NaN) == 0) {
                return "double";
            } else {
                return "float";
            }
        }

        int valueInt = getKKsp().getMmkv().decodeInt(key);
        long valueLong = getKKsp().getMmkv().decodeLong(key);
        if (valueInt != valueLong) {
            return "long";
        } else {
            return "int";
        }
    }

}

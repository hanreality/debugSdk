package com.melot.android.debug.sdk.util

import com.google.gson.Gson
import com.google.gson.JsonElement
import java.lang.reflect.Type

/**
 * Author: han.chen
 * Time: 2021/11/23 16:34
 */
object DebugGsonUtil {
    private val sGson: Gson = Gson()
    fun getGson(): Gson? {
        return sGson
    }

    fun <T> GsonToBean(gsonString: String?, clazz: Class<T>?): T {
        return sGson.fromJson(gsonString, clazz)
    }

    fun <T> GsonToBean(element: JsonElement?, clazz: Class<T>?): T {
        return sGson.fromJson(element, clazz)
    }

    fun <T> GsonToBean(gsonString: String?, type: Type?): T {
        return sGson.fromJson(gsonString, type)
    }

    /**
     * bean 转换成 json
     *
     * @param object
     * @return
     */
    fun BeanToGson(`object`: Any?): String? {
        return sGson.toJson(`object`)
    }
}
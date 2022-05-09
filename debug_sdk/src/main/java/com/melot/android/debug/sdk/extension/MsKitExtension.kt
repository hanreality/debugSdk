package com.melot.android.debug.sdk.extension

import android.app.Activity
import com.melot.android.debug.sdk.core.MsKitView
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.plus
import kotlin.reflect.KClass

/**
 * Author: han.chen
 * Time: 2021/12/3 16:22
 */
val msKitGlobalScope = MainScope() + CoroutineName("MsKit")

val Activity.tagName: String
    get() {
        return this.javaClass.canonicalName ?: ""
    }

val MsKitView.tagName: String
    get() {
        return this.javaClass.canonicalName ?: ""
    }

val KClass<out Any>.tagName: String
    get() {
        return this.java.canonicalName ?: ""
    }

val Class<out Any>.tagName: String
    get() {
        return this.canonicalName ?: ""
    }
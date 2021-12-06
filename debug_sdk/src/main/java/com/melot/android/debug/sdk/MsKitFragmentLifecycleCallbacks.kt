package com.melot.android.debug.sdk

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.melot.android.debug.sdk.util.LifecycleListenerUtil

/**
 * Author: han.chen
 * Time: 2021/12/3 17:34
 */
class MsKitFragmentLifecycleCallbacks : FragmentManager.FragmentLifecycleCallbacks() {

    override fun onFragmentAttached(fm: FragmentManager, f: Fragment, context: Context) {
        super.onFragmentAttached(fm, f, context)
        LifecycleListenerUtil.LIFECYCLE_LISTENERS.forEach {
            it.onFragmentAttached(f)
        }

    }

    override fun onFragmentDetached(fm: FragmentManager, f: Fragment) {
        super.onFragmentDetached(fm, f)
        LifecycleListenerUtil.LIFECYCLE_LISTENERS.forEach {
            it.onFragmentDetached(f)
        }
    }
}
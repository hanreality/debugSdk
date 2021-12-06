package com.melot.android.debug.sdk.core

import android.os.Bundle
import android.widget.Toast
import com.melot.android.debug.sdk.base.BaseActivity
import com.melot.android.debug.sdk.base.BaseFragment
import com.melot.android.debug.sdk.constant.BundleKey
import com.melot.android.debug.sdk.constant.FragmentIndex

/**
 * Author: han.chen
 * Time: 2021/12/3 15:33
 */
open class UniversalActivity : BaseActivity() {
    var mFragmentClass: Class<out BaseFragment>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val bundle = intent.extras
        if (bundle == null) {
            finish()
            return
        }
        val index = bundle.getInt(BundleKey.FRAGMENT_INDEX)
        if (index == 0) {
            finish()
            return
        }

        when (index) {
            FragmentIndex.FRAGMENT_SYSTEM -> if (bundle[BundleKey.SYSTEM_FRAGMENT_CLASS] != null) {
                mFragmentClass = bundle[BundleKey.SYSTEM_FRAGMENT_CLASS] as Class<out BaseFragment>
            }
            FragmentIndex.FRAGMENT_CUSTOM -> if (bundle[BundleKey.CUSTOM_FRAGMENT_CLASS] != null) {
                mFragmentClass = bundle[BundleKey.CUSTOM_FRAGMENT_CLASS] as Class<out BaseFragment>
            }
            else -> {

            }
        }

        if (mFragmentClass == null) {
            finish()
            Toast.makeText(
                this,
                String.format("fragment index %s not found", index),
                Toast.LENGTH_SHORT
            ).show()
            return
        }
        showContent(mFragmentClass!!, bundle)

    }
}
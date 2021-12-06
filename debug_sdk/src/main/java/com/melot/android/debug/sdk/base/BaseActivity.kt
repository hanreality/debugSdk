package com.melot.android.debug.sdk.base

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.melot.android.debug.sdk.R
import java.util.ArrayDeque

/**
 * Author: han.chen
 * Time: 2021/12/3 14:21
 */
abstract class BaseActivity : AppCompatActivity() {
    private val mFragments = ArrayDeque<BaseFragment>()

    @JvmOverloads
    fun showContent(target: Class<out BaseFragment>, bundle: Bundle? = null) {
        try {
            val fragment = target.newInstance()
            if (bundle != null) {
                fragment.arguments = bundle
            }
            val fm = supportFragmentManager
            val ft = fm.beginTransaction()
            ft.add(R.id.content, fragment)
            mFragments.push(fragment)
            ft.addToBackStack("")
            ft.commit()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onBackPressed() {
        if (!mFragments.isEmpty()) {
            val fragment = mFragments.first
            if (!fragment.onBackPressed()) {
                mFragments.removeFirst()
                super.onBackPressed()
                if (mFragments.isEmpty()) {
                    finish()
                }
            }
        } else {
            super.onBackPressed()
        }
    }

    fun doBack(fragment: BaseFragment) {
        if (mFragments.contains(fragment)) {
            mFragments.remove(fragment)
            val fm = supportFragmentManager
            fm.popBackStack()
            if (mFragments.isEmpty()) {
                finish()
            }
        }
    }
}
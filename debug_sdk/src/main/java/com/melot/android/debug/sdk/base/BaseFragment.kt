package com.melot.android.debug.sdk.base

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment

/**
 * Author: han.chen
 * Time: 2021/12/3 14:23
 */
abstract class BaseFragment : Fragment() {
    @JvmField
    val TAG = this.javaClass.simpleName

    @LayoutRes
    protected abstract fun onRequestLayout(): Int

    fun <T : View> findViewById(@IdRes id: Int): T {
        return requireView().findViewById(id)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val id = onRequestLayout()
        var rootView: View? = null
        if (id > 0) {
            rootView = inflater.inflate(id, container, false)
        }
        if (interceptTouchEvents() && rootView != null) {
            rootView.setOnTouchListener { _, _ -> true }
        }
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        try {
            (view.context as? Activity)?.window?.decorView?.requestLayout()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        //TODO 移除悬浮窗口
    }

    protected fun interceptTouchEvents(): Boolean {
        return false
    }

    open fun onBackPressed(): Boolean {
        return false
    }

    @JvmOverloads
    fun showContent(fragmentClass: Class<out BaseFragment>, bundle: Bundle? = null) {
        (activity as? BaseActivity)?.showContent(fragmentClass, bundle)
    }

    fun finish() {
        (activity as? BaseActivity)?.doBack(this)
    }
}
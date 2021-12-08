package com.melot.android.debug.sdk.util

import android.app.Activity
import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import android.graphics.Point
import android.os.Build
import android.provider.Settings
import android.util.DisplayMetrics
import android.view.*
import com.melot.android.debug.sdk.MsKit
import java.lang.Exception
import java.lang.ref.WeakReference

/**
 * Author: han.chen
 * Time: 2021/10/12 10:32
 */
object DevelopUtil {
    @JvmStatic
    fun isNull(any: Any?, what: String) {
        assert(any != null) {
            throw NullPointerException("$what cannot be null!")
        }
    }

    @JvmStatic
    fun isPortrait(): Boolean {
        return MsKit.requireApp().resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT
    }

    @JvmStatic
    fun isLandscape(): Boolean {
        return MsKit.requireApp().resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    }

    /**
     * Return the application's width of screen, in pixel.
     *
     * @return the application's width of screen, in pixel
     */
    @JvmStatic
    fun getAppScreenWidth(): Int {
        val wm = MsKit.requireApp().getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val point = Point()
        wm.defaultDisplay.getSize(point)
        return point.x
    }

    /**
     * Return the application's height of screen, in pixel.
     *
     * @return the application's height of screen, in pixel
     */
    @JvmStatic
    fun getAppScreenHeight(): Int {
        val wm = MsKit.requireApp().getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val point = Point()
        wm.defaultDisplay.getSize(point)
        return point.y
    }

    @JvmStatic
    fun getAppFullScreenHeight(): Int {
        val wm = MsKit.requireApp().getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val point = Point()
        wm.defaultDisplay.getRealSize(point)
        return point.y
    }

    @JvmStatic
    fun dp2px(dpValue: Float): Int {
        val scale = Resources.getSystem().displayMetrics.density
        return (dpValue * scale + 0.5f).toInt()
    }

    /**
     * Return whether the status bar is visible.
     *
     * @param activity The activity.
     * @return `true`: yes<br></br>`false`: no
     */
    @JvmStatic
    fun isStatusBarVisible(activity: Activity): Boolean {
        val flags = activity.window.attributes.flags
        return flags and WindowManager.LayoutParams.FLAG_FULLSCREEN == 0
    }

    /**
     * Return the status bar's height.
     *
     * @return the status bar's height
     */
    @JvmStatic
    fun getStatusBarHeight(): Int {
        val resources: Resources = MsKit.requireApp().resources
        val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
        return resources.getDimensionPixelSize(resourceId)
    }

    /**
     * Return whether the navigation bar visible.
     *
     * @return `true`: yes<br></br>`false`: no
     */
    @JvmStatic
    fun isSupportNavBar(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            val wm = MsKit.requireApp().getSystemService(Context.WINDOW_SERVICE) as WindowManager
                ?: return false
            val display = wm.defaultDisplay
            val size = Point()
            val realSize = Point()
            display.getSize(size)
            display.getRealSize(realSize)
            return realSize.y != size.y || realSize.x != size.x
        }
        val menu = ViewConfiguration.get(MsKit.requireApp()).hasPermanentMenuKey()
        val back = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_BACK)
        return !menu && !back
    }

    /**
     * Return whether the navigation bar visible.
     *
     * Call it in onWindowFocusChanged will get right result.
     *
     * @param activity The activity.
     * @return `true`: yes<br></br>`false`: no
     */
    fun isNavBarVisible(activity: Activity): Boolean {
        return isNavBarVisible(activity.window)
    }

    /**
     * Return whether the navigation bar visible.
     *
     * Call it in onWindowFocusChanged will get right result.
     *
     * @param window The window.
     * @return `true`: yes<br></br>`false`: no
     */
    fun isNavBarVisible(window: Window): Boolean {
        var isVisible = false
        val decorView = window.decorView as ViewGroup
        var i = 0
        val count = decorView.childCount
        while (i < count) {
            val child = decorView.getChildAt(i)
            val id = child.id
            if (id != View.NO_ID) {
                val resourceEntryName: String =
                    MsKit.requireApp().resources.getResourceEntryName(id)
                if ("navigationBarBackground" == resourceEntryName && child.visibility == View.VISIBLE) {
                    isVisible = true
                    break
                }
            }
            i++
        }
        if (isVisible) {
            // 对于三星手机，android10以下非OneUI2的版本，比如 s8，note8 等设备上，
            // 导航栏显示存在bug："当用户隐藏导航栏时显示输入法的时候导航栏会跟随显示"，会导致隐藏输入法之后判断错误
            // 这个问题在 OneUI 2 & android 10 版本已修复
            if (RomUtils.isSamsung()
                && Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1 && Build.VERSION.SDK_INT < Build.VERSION_CODES.Q
            ) {
                try {
                    return Settings.Global.getInt(
                        MsKit.requireApp().contentResolver,
                        "navigationbar_hide_bar_enabled"
                    ) == 0
                } catch (ignore: Exception) {
                }
            }
            val visibility = decorView.systemUiVisibility
            isVisible = visibility and View.SYSTEM_UI_FLAG_HIDE_NAVIGATION == 0
        }
        return isVisible
    }

    /**
     * Return the navigation bar's height.
     *
     * @return the navigation bar's height
     */
    @JvmStatic
    fun getNavBarHeight(): Int {
        val res: Resources = MsKit.requireApp().getResources()
        val resourceId = res.getIdentifier("navigation_bar_height", "dimen", "android")
        return if (resourceId != 0) {
            res.getDimensionPixelSize(resourceId)
        } else {
            0
        }
    }
}
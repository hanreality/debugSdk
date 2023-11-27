package com.melot.android.debug.sdk.util

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import android.graphics.Point
import android.os.Build
import android.provider.Settings
import android.text.TextUtils
import android.util.TypedValue
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.annotation.AnyRes
import com.melot.android.debug.sdk.MsKit
import com.melot.android.debug.sdk.model.ViewWindow
import java.lang.reflect.Field
import java.lang.reflect.Method
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import kotlin.experimental.and

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
     * @param context The Context.
     * @return `true`: yes<br></br>`false`: no
     */
    @JvmStatic
    fun isStatusBarVisible(context: Context?): Boolean {
        return !(checkFullScreenByTheme(context) || checkFullScreenByCode(context) || checkFullScreenByCode2(
            context
        ))
    }

    private fun checkFullScreenByTheme(context: Context?): Boolean {
        val theme = context?.theme
        if (theme != null) {
            val typedValue = TypedValue()
            val result = theme.resolveAttribute(android.R.attr.windowFullscreen, typedValue, false)
            if (result) {
                typedValue.coerceToString()
                if (typedValue.type == TypedValue.TYPE_INT_BOOLEAN) {
                    return typedValue.data != 0
                }
            }
        }
        return false
    }

    private fun checkFullScreenByCode(context: Context?): Boolean {
        if (context is Activity) {
            val window = context.window
            if (window != null) {
                val decorView = window.decorView
                return decorView.systemUiVisibility and View.SYSTEM_UI_FLAG_FULLSCREEN == View.SYSTEM_UI_FLAG_FULLSCREEN
            }
        }
        return false
    }

    private fun checkFullScreenByCode2(context: Context?): Boolean {
        return if (context is Activity) {
            context.window.attributes.flags and WindowManager.LayoutParams.FLAG_FULLSCREEN == WindowManager.LayoutParams.FLAG_FULLSCREEN
        } else false
    }

    /**
     * Return the status bar's height.
     *
     * @return the status bar's height
     */
    @JvmStatic
    @JvmOverloads
    fun getStatusBarHeight(context: Context? = MsKit.requireApp()): Int {
        val resources: Resources = context?.resources ?: MsKit.requireApp().resources
        val result = 0
        try {
            val resourceId = Resources.getSystem().getIdentifier("status_bar_height", "dimen", "android")
            if (resourceId > 0) {
                val sizeOne: Int = resources.getDimensionPixelSize(resourceId)
                val sizeTwo = Resources.getSystem().getDimensionPixelSize(resourceId)
                return if (sizeTwo >= sizeOne) {
                    sizeTwo
                } else {
                    val densityOne: Float = resources.displayMetrics.density
                    val densityTwo = Resources.getSystem().displayMetrics.density
                    val f = sizeOne * densityTwo / densityOne
                    (if (f >= 0) f + 0.5f else f - 0.5f).toInt()
                }
            }
        } catch (ignored: Resources.NotFoundException) {
            return 0
        }
        return result
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

    /**
     * 获得app的contentView
     *
     * @param activity
     * @return
     */
    @JvmStatic
    fun getMsKitAppContentView(activity: Activity?): View? {
        val decorView = activity?.window?.decorView as? FrameLayout
        var mAppContentView = decorView?.findViewById(android.R.id.content) as? View
        if (mAppContentView != null) {
            return mAppContentView
        }
        for (index in 0 until (decorView?.childCount ?: 0)) {
            val child = decorView?.getChildAt(index)
            if (child is LinearLayout && TextUtils.isEmpty(getIdText(child).trim { it <= ' ' }) || child is FrameLayout) {
                mAppContentView = child
                break
            }
        }
        return mAppContentView
    }

    @JvmStatic
    fun getWindowDecorViews(): ArrayList<ViewWindow> {
        val decorViewList = ArrayList<ViewWindow>()
        try {
            val clazz = Class.forName("android.view.WindowManagerGlobal")
            val mViewsField: Field = clazz.getDeclaredField("mViews")
            val instanceMethod: Method = clazz.getMethod("getInstance")
            val mGlobal: Any = instanceMethod.invoke(null)
            mViewsField.isAccessible = true
            val mViews = mViewsField.get(mGlobal)
            (mViews as? List<*>)?.forEach { view ->
                if (filterDecorView(view)) {
                    decorViewList.add(ViewWindow(view as View))
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return decorViewList
    }

    private fun filterDecorView(decorView: Any?): Boolean {
        if (TextUtils.equals(decorView?.javaClass?.name, "com.android.internal.policy.DecorView")) {
            return true
        } else if (TextUtils.equals(decorView?.javaClass?.name, "android.widget.PopupWindow\$PopupDecorView")) {
            val clazzName = ((decorView as? ViewGroup)?.getChildAt(0) as? ViewGroup)?.getChildAt(0)?.javaClass?.name
            return clazzName?.contains("KeyboardPopLayout") != true
        }
        return false
    }

    /**
     * 要特别注意 返回的字段包含空格  做判断时一定要trim()
     *
     * @param view
     * @return
     */
    @JvmStatic
    fun getIdText(view: View): String {
        val id = view.id
        val out = StringBuilder()
        if (id != View.NO_ID) {
            val r = view.resources
            if (id > 0 && resourceHasPackage(id) && r != null) {
                try {
                    val pkgname: String = when (id and -0x1000000) {
                        0x7f000000 -> "app"
                        0x01000000 -> "android"
                        else -> r.getResourcePackageName(id)
                    }
                    val typename = r.getResourceTypeName(id)
                    val entryName = r.getResourceEntryName(id)
                    out.append(" ")
                    out.append(pkgname)
                    out.append(":")
                    out.append(typename)
                    out.append("/")
                    out.append(entryName)
                } catch (e: Resources.NotFoundException) {
                    e.printStackTrace()
                }
            }
        }
        return if (TextUtils.isEmpty(out.toString())) "" else out.toString()
    }

    private fun resourceHasPackage(@AnyRes resid: Int): Boolean {
        return resid ushr 24 != 0
    }

    fun closeSoftKeyBoard(con: Context?, et: EditText?): Boolean {
        val imm = con?.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        return if (imm != null && imm.isActive && et != null) {
            imm.hideSoftInputFromWindow(et.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
        } else false
    }

    fun copyText(content:String?) {
        content?.run {
            val clipboard = MsKit.requireApp().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            clipboard.setPrimaryClip(ClipData.newPlainText(null, this))//参数一：标签，可为空，参数二：要复制到剪贴板的文本)
        }
    }
}
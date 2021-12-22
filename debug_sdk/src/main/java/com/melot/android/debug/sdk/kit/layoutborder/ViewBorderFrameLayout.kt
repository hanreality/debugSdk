package com.melot.android.debug.sdk.kit.layoutborder

import android.content.Context
import android.graphics.drawable.Drawable
import android.graphics.drawable.LayerDrawable
import android.util.AttributeSet
import android.view.TextureView
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.melot.android.debug.sdk.R
import com.melot.android.debug.sdk.config.LayoutBorderConfig
import com.melot.android.debug.sdk.core.MsKitViewInterface
import java.util.ArrayList

/**
 * Author: han.chen
 * Time: 2021/12/22 16:13
 */
class ViewBorderFrameLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    init {
        id = R.id.msKit_view_border_id
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        if (LayoutBorderConfig.sLayoutBorderOpen) {
            traverseChild(this)
        } else {
            clearChild(this)
        }
    }

    private fun traverseChild(view: View) {
        if (view is ViewGroup && view !is MsKitViewInterface) {
            replaceDrawable(view)
            val childCount = view.childCount
            if (childCount != 0) {
                for (index in 0 until childCount) {
                    traverseChild(view.getChildAt(index))
                }
            }
        } else {
            replaceDrawable(view)
        }
    }

    private fun replaceDrawable(view: View) {
        if (view is TextureView) {
            return
        }
        val newDrawable: LayerDrawable
        if (view.background != null) {
            val oldDrawable: Drawable = view.background
            if (oldDrawable is LayerDrawable) {
                for (index in 0 until oldDrawable.numberOfLayers) {
                    if (oldDrawable.getDrawable(index) is ViewBorderDrawable) {
                        return
                    }
                }
            }
            newDrawable = LayerDrawable(
                arrayOf(
                    oldDrawable,
                    ViewBorderDrawable(view)
                )
            )
        } else {
            newDrawable = LayerDrawable(
                arrayOf(
                    ViewBorderDrawable(view)
                )
            )
        }
        try {
            view.background = newDrawable
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun clearChild(view: View) {
        if (view is ViewGroup) {
            clearDrawable(view)
            val childCount = view.childCount
            if (childCount != 0) {
                for (index in 0 until childCount) {
                    clearChild(view.getChildAt(index))
                }
            }
        } else {
            clearDrawable(view)
        }
    }

    private fun clearDrawable(view: View) {
        if (view.background == null) {
            return
        }

        val oldDrawable = view.background
        if (oldDrawable !is LayerDrawable) {
            return
        }
        val layerDrawable: LayerDrawable = oldDrawable
        val drawables: ArrayList<Drawable> = ArrayList()
        for (index in 0 until layerDrawable.numberOfLayers) {
            if (layerDrawable.getDrawable(index) is ViewBorderDrawable) {
                continue
            }
            drawables.add(layerDrawable.getDrawable(index))
        }
        val newDrawable = LayerDrawable(drawables.toTypedArray())
        view.background = newDrawable
    }
}
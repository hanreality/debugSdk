package com.melot.android.debug.sdk.base

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import androidx.recyclerview.widget.RecyclerView

/**
 * Author: han.chen
 * Time: 2021/12/7 17:03
 */
abstract class BaseViewHolder<T>( view: View) : RecyclerView.ViewHolder(view) {

    abstract fun bindData(data: T)
}
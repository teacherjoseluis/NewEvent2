package com.bridesandgrooms.event

import android.content.Context
import androidx.recyclerview.widget.RecyclerView

interface ItemTouchAdapterAction {
    fun onItemSwiftLeft(context: Context, position: Int, recyclerView: RecyclerView, action: String)
    fun onItemSwiftRight(context: Context, position: Int, recyclerView: RecyclerView, action: String)
}
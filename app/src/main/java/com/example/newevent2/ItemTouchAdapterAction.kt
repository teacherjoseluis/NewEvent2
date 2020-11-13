package com.example.newevent2

import androidx.recyclerview.widget.RecyclerView

interface ItemTouchAdapterAction {
    fun onItemSwiftLeft(position: Int, recyclerView: RecyclerView, action: String)
    fun onItemSwiftRight(position: Int, recyclerView: RecyclerView, action: String)
}
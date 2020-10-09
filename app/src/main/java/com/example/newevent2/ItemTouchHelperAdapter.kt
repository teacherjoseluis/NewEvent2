package com.example.newevent2

import androidx.recyclerview.widget.RecyclerView

interface ItemTouchHelperAdapter {
    fun onItemSwiftLeft(position: Int, recyclerView: RecyclerView)
    fun onItemSwiftRight(position: Int, recyclerView: RecyclerView)
}
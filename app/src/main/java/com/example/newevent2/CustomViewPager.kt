package com.example.newevent2

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.viewpager.widget.ViewPager

class CustomViewPager : ViewPager {
    private var swipeable: Boolean = false

    constructor(context: Context) : super(context) {
    }

    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet) {
    }

    fun setSwipeable(swipeable: Boolean) {
        this.swipeable = swipeable
    }

    override fun onInterceptTouchEvent(arg0: MotionEvent): Boolean {
        return when (swipeable) {
            true -> super.onInterceptTouchEvent(arg0)
            false -> false
        }
    }
}
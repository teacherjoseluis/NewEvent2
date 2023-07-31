package com.bridesandgrooms.event

import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable

open class SwipeControl {
    open val drawable:Drawable?=null
    open val background = ColorDrawable()
    open val backgroundcolor:Int?=null
    open val intrinsicWidth:Int?=null
    open val intrinsicHeight:Int?=null
}
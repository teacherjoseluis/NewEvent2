package com.example.newevent2

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat

@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
class SwipeControlDelete(context: Context) : SwipeControl() {

        override val drawable = ContextCompat.getDrawable(context, R.drawable.icons8_trash_can)!!
        override val background = ColorDrawable()
        override val backgroundcolor = Color.parseColor("#D32F2F")
        override val intrinsicWidth = drawable.intrinsicWidth
        override val intrinsicHeight = drawable.intrinsicHeight

        init {
            drawable.setTint(Color.WHITE)
        }
    }
package com.example.newevent2.ui.Validators

import android.content.Context
import android.util.AttributeSet
import com.google.android.material.textfield.TextInputEditText

class DateInputText : TextInputEditText {
    constructor(context: Context) : super(context)
    {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)
    {
        init()
    }

    private fun init()
    {
        //this.paintFlags = this.paintFlags or Paint.UNDERLINE_TEXT_FLAG
    }
}
package com.bridesandgrooms.event.Functions

import android.content.Context
import android.widget.Toast

internal fun displayErrorMsg(context: Context, message: String) {
    Toast.makeText(
        context,
        message,
        Toast.LENGTH_LONG
    ).show()
}
package com.example.newevent2.Functions

import android.app.Activity
import android.view.inputmethod.InputMethodManager


class Utility {

    companion object {
        fun hideSoftKeyboard(activity: Activity?) {
            if (activity == null) return
            if (activity.currentFocus == null) return
            val inputMethodManager: InputMethodManager =
                activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(activity.currentFocus!!.windowToken, 0)
        }
    }
}
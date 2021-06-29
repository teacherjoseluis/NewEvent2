package com.example.newevent2.ui

import android.text.TextWatcher
import android.widget.TextView
import java.util.regex.Pattern

class TextValidate(val textView: TextView) {

    private var validationmessage = ""

    fun namefieldValidate(): String {
        if (!minTextLength(textView.text.toString())) validationmessage = "text is too short"
        if (!specialChars(textView.text.toString())) validationmessage =
            "text can only contain numbers and letters"
        return validationmessage
    }

    private fun minTextLength(text: String): Boolean {
        return text.length >= MINTEXTLENGHT
    }

    private fun specialChars(text: String): Boolean {
        val special = Pattern.compile("[^a-z0-9] ")
        val hasnotSpecial = special.matcher(text)
        return !hasnotSpecial.find()
    }

    companion object {
        const val MINTEXTLENGHT = 4
    }
}
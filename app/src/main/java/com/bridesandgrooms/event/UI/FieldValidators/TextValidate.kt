package com.bridesandgrooms.event.UI.FieldValidators

import java.util.regex.Pattern

class TextValidate(val text: String) {

    fun nameFieldValidate(): Boolean {
        return (minTextLength(text) && specialChars(text))
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
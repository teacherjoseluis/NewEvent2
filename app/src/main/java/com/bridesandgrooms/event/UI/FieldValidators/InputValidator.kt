package com.bridesandgrooms.event.UI.FieldValidators

import android.content.Context
import android.provider.Settings.Global.getString
import com.bridesandgrooms.event.R
import com.google.android.material.textfield.TextInputEditText

class InputValidator(private val context: Context) {

    var errorCode: String? = null

    fun validate(input: TextInputEditText): Boolean {
        errorCode = null  // Reset the error code before each validation

        return when (getFieldType(input)) {
            FieldType.NAME -> validateName(input.text.toString())
            FieldType.DATE -> validateDate(input.text.toString())
            FieldType.PASSWORD -> validatePassword(input.text.toString())
            FieldType.PHONE -> validatePhoneNumber(input.text.toString())
            FieldType.EMAIL -> validateEmail(input.text.toString())
            else -> validateName(input.text.toString())
        }
    }

    private fun validateName(name: String): Boolean {
        if (name.isBlank()) {
            errorCode = context.getString(R.string.error_tasknameinput)
            return false
        }
        if (!TextValidate(name).nameFieldValidate()) {
            errorCode = context.getString(R.string.error_invalidnameinput)
            return false
        }
        return true
    }

    private fun validateDate(date: String): Boolean {
        if (date.isBlank()) {
            errorCode = context.getString(R.string.error_taskdateinput)
            return false
        }
        if (!date.matches(Regex("""\d{4}-\d{2}-\d{2}"""))) {
            errorCode = context.getString(R.string.error_invaliddate)
            return false
        }
        // Add more date validation logic here
        return true
    }

    private fun validatePassword(password: String): Boolean {
        if (password.isEmpty()) {
            errorCode = context.getString(R.string.password_requiredformat)
            return false
        }
        if (password.length < 8) {
            errorCode = context.getString(R.string.password_requiredformat)
            return false
        }
        // Add more password validation logic here
        return true
    }

    private fun validatePhoneNumber(phone: String): Boolean {
        // Regex to match US phone numbers with parentheses and hyphens
        val regex = Regex("""^\(?\d{3}\)?[-.\s]?\d{3}[-.\s]?\d{4}$""")
        if (phone.isEmpty()) {
            errorCode = context.getString(R.string.error_vendorphoneinput)
            return false
        }
        if (!phone.matches(regex)) {
            errorCode = context.getString(R.string.error_vendorphoneinputformat)
            return false
        }
        return true
    }

    private fun validateEmail(email: String): Boolean {
        val trimmedEmail = email.trim()  // Remove any leading or trailing whitespace
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(trimmedEmail).matches()) {
            errorCode = context.getString(R.string.resetpwderror_invalidcredentials)
            return false
        }
        return true
    }

    fun validateSpinner(selection: String): Boolean {
        if (selection.isEmpty()) {
            errorCode = context.getString(R.string.spinner_error)
            return false
        }
        return true
    }

    fun getFieldType(editText: TextInputEditText): FieldType {
        val tag = editText.tag?.toString() ?: return FieldType.NONE
        return FieldType.values().find { it.name.equals(tag, ignoreCase = true) } ?: FieldType.NONE
    }

    enum class FieldType {
        NAME, DATE, PASSWORD, PHONE, EMAIL, NONE
    }
}

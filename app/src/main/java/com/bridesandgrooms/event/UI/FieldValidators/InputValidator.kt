package com.bridesandgrooms.event.UI.FieldValidators

import Application.AnalyticsManager
import android.content.Context
import android.widget.EditText
import com.bridesandgrooms.event.R
import com.google.android.material.textfield.TextInputEditText
import java.util.regex.Pattern

class InputValidator(private val context: Context) {

    var errorCode: String? = null

    fun validate(input: TextInputEditText): Boolean {
        errorCode = null  // Reset the error code before each validation

        return when (getFieldType(input)) {
            FieldType.NAME -> validateName(input.text.toString())
            FieldType.NUMBER -> validateNumber(input.text.toString())
            FieldType.DATE -> validateDate(input.text.toString())
            FieldType.PASSWORD -> validatePassword(input.text.toString())
            FieldType.PHONE -> validatePhoneNumber(input.text.toString())
            FieldType.EMAIL -> validateEmail(input.text.toString())
            FieldType.MONEY -> validateMoney(input.text.toString())
            else -> validateName(input.text.toString())
        }
    }

    fun validate(input: EditText): Boolean {
        errorCode = null  // Reset the error code before each validation

        return when (getFieldType(input)) {
            FieldType.TEXTBODY -> validateTextBody(input.text.toString())
            else -> validateTextBody(input.text.toString())
        }
    }

    private fun validateName(name: String): Boolean {
        if (name.isBlank()) {
            AnalyticsManager.getInstance().trackError(
                null,
                "name",
                "validation",
                null
            )
            errorCode = context.getString(R.string.error_tasknameinput)
            return false
        }
        if (!TextValidate(name).nameFieldValidate()) {
            AnalyticsManager.getInstance().trackError(
                null,
                "name",
                "validation",
                null
            )
            errorCode = context.getString(R.string.error_invalidnameinput)
            return false
        }
        return true
    }

    private fun validateNumber(input: String, min: Int? = null, max: Int? = null): Boolean {
        if (input.isBlank()) {
            AnalyticsManager.getInstance().trackError(
                null,
                "number",
                "validation",
                null
            )
            errorCode = context.getString(R.string.error_numberguestsrequired)
            return false
        }
        val value = input.toIntOrNull()  // Converts the string to an integer or returns null if it's not a valid integer
        if (value == null) {
            AnalyticsManager.getInstance().trackError(
                null,
                "number",
                "validation",
                null
            )
            errorCode = context.getString(R.string.error_numberguestsrequired)
            return false
        }
        // Check if the integer is within a specified range, if range parameters are provided
        if ((min != null && value < min) || (max != null && value > max)) {
            AnalyticsManager.getInstance().trackError(
                null,
                "number",
                "validation",
                null
            )
            errorCode = context.getString(R.string.error_numberguestsrequired)
            return false
        }
        return true
    }

    private fun validateDate(date: String): Boolean {
        if (date.isBlank()) {
            AnalyticsManager.getInstance().trackError(
                null,
                "date",
                "validation",
                null
            )
            errorCode = context.getString(R.string.error_taskdateinput)
            return false
        }
        if (!date.matches(Regex("""\d{4}-\d{2}-\d{2}"""))) {
            AnalyticsManager.getInstance().trackError(
                null,
                "date",
                "validation",
                null
            )
            errorCode = context.getString(R.string.error_invaliddate)
            return false
        }
        // Add more date validation logic here
        return true
    }

    private fun validatePassword(password: String): Boolean {
        if (password.isEmpty()) {
            AnalyticsManager.getInstance().trackError(
                null,
                "password",
                "validation",
                null
            )
            errorCode = context.getString(R.string.password_requiredformat)
            return false
        }
        if (password.length < 8) {
            AnalyticsManager.getInstance().trackError(
                null,
                "password",
                "validation",
                null
            )
            errorCode = context.getString(R.string.password_requiredformat)
            return false
        }
        // Define the password pattern for validation
        val passwordPattern = "^(?=.*[0-9])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{8,}$"
        val pattern = Pattern.compile(passwordPattern)

        // Create a matcher to check the password
        val matcher = pattern.matcher(password)
        if (!matcher.matches()) {
            AnalyticsManager.getInstance().trackError(
                null,
                "password",
                "validation",
                null
            )
            errorCode = context.getString(R.string.password_requiredformat)
            return false
        }
        return true
    }

    private fun validatePhoneNumber(phone: String): Boolean {
        // Regex to match US phone numbers with parentheses and hyphens
        val regex = Regex("""^\(?\d{3}\)?[-.\s]?\d{3}[-.\s]?\d{4}$""")
        if (phone.isEmpty()) {
            AnalyticsManager.getInstance().trackError(
                null,
                "phone",
                "validation",
                null
            )
            errorCode = context.getString(R.string.error_vendorphoneinput)
            return false
        }
        if (!phone.matches(regex)) {
            AnalyticsManager.getInstance().trackError(
                null,
                "phone",
                "validation",
                null
            )
            errorCode = context.getString(R.string.error_vendorphoneinputformat)
            return false
        }
        return true
    }

    private fun validateEmail(email: String): Boolean {
        val trimmedEmail = email.trim()  // Remove any leading or trailing whitespace
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(trimmedEmail).matches()) {
            errorCode = context.getString(R.string.resetpwderror_invalidcredentials)
            AnalyticsManager.getInstance().trackError(
                null,
                "email",
                "validation",
                null
            )
            return false
        }
        return true
    }

    fun validateSpinner(selection: String): Boolean {
        if (selection.isEmpty()) {
            AnalyticsManager.getInstance().trackError(
                null,
                "spinner",
                "validation",
                null
            )
            errorCode = context.getString(R.string.spinner_error)
            return false
        }
        return true
    }

    private fun validateTextBody(body: String): Boolean {
        if (body.isBlank()) {
            AnalyticsManager.getInstance().trackError(
                null,
                "text body",
                "validation",
                null
            )
            errorCode = context.getString(R.string.error_tasknameinput)
            return false
        }
        return true
    }

    private fun validateMoney(amount: String): Boolean {
        if (amount.isBlank()) {
            AnalyticsManager.getInstance().trackError(
                null,
                "amount",
                "validation",
                null
            )
            errorCode = context.getString(R.string.error_invalid_money_input)
            return false
        }

        // Regex to validate money (e.g., 123.45 or 67)
        val regex = Regex("""^\d+(\.\d{1,2})?$""")

        if (!amount.matches(regex)) {
            AnalyticsManager.getInstance().trackError(
                null,
                "amount",
                "validation",
                null
            )
            errorCode = context.getString(R.string.error_invalid_money_input)
            return false
        }

        return true
    }


    fun getFieldType(editText: TextInputEditText): FieldType {
        val tag = editText.tag?.toString() ?: return FieldType.NONE
        return FieldType.values().find { it.name.equals(tag, ignoreCase = true) } ?: FieldType.NONE
    }

    fun getFieldType(editText: EditText): FieldType {
        val tag = editText.tag?.toString() ?: return FieldType.NONE
        return FieldType.values().find { it.name.equals(tag, ignoreCase = true) } ?: FieldType.NONE
    }

    enum class FieldType {
        NAME, NUMBER, DATE, PASSWORD, PHONE, EMAIL, NONE, TEXTBODY, MONEY
    }
}

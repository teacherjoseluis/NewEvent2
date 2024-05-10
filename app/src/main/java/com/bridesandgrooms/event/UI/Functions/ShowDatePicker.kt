package com.bridesandgrooms.event.UI.Functions

import android.app.DatePickerDialog
import androidx.fragment.app.FragmentManager
import com.bridesandgrooms.event.UI.Dialogs.DatePickerFragment

internal fun showDatePickerDialog(supportFragmentManager : FragmentManager) : String {
    var selectedDate = ""
    val newFragment =
        DatePickerFragment.newInstance(DatePickerDialog.OnDateSetListener { _, year, month, day ->
            // +1 because January is zero
            selectedDate = day.toString() + " / " + (month + 1) + " / " + year
        })
    newFragment.show(supportFragmentManager, "datePicker")
    return selectedDate
}
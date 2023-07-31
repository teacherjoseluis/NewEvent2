package com.bridesandgrooms.event.ui.Functions

import android.app.DatePickerDialog
import androidx.fragment.app.FragmentManager
import com.bridesandgrooms.event.ui.dialog.DatePickerFragment

var selectedDate = ""

internal fun showDatePickerDialog(supportFragmentManager : FragmentManager) : String {
    val newFragment =
        DatePickerFragment.newInstance(DatePickerDialog.OnDateSetListener { _, year, month, day ->
            // +1 because January is zero
            selectedDate = day.toString() + " / " + (month + 1) + " / " + year
        })
    newFragment.show(supportFragmentManager, "datePicker")
    return selectedDate
}
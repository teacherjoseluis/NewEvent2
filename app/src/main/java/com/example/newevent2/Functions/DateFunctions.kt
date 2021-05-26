package com.example.newevent2.Functions

import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

internal fun converttoDate(datestring: String) : Date{
    val pattern = "dd/MM/yyyy" //Pattern in which dates are saved in Firebase
    val locale = Locale.getDefault()
    val simpleDateFormat = SimpleDateFormat(pattern, locale)
    //val dateFormatter = DateFormat.getDateInstance(DateFormat.SHORT, locale) returns a String
    return simpleDateFormat.parse(datestring)
}

//TODO("I need a function that does the opposite, takes the date and returns a string for displaying.
// This could be formatted in several ways depending on the use")

internal fun converttoString(date: Date, style: Int) : String{
    val locale = Locale.getDefault()
    val dateFormatter = DateFormat.getDateInstance(style, locale)
    return dateFormatter.format(date)
}
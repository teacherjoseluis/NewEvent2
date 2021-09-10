package com.example.newevent2.Functions

import java.text.DecimalFormat
import java.util.*
import kotlin.collections.ArrayList

fun <T> removeDuplicates(list: ArrayList<T>): ArrayList<T>? {
    // Create a new ArrayList
    val newList = ArrayList<T>()
    // Traverse through the first list
    for (element in list) {
        // If this element is not present in newList
        // then add it
        if (!newList.contains(element)) {
            newList.add(element)
        }
    }
    // return the new list
    return newList
}

fun <T> clone(list: ArrayList<T>): ArrayList<T>? {
    val newList = ArrayList<T>()
    //------------------------------------------
    for (element in list) {
        newList.add(element)
    }
    //------------------------------------------
    return newList
}

fun sumStrings(list: ArrayList<Float>) : String {
    var sumlist = 0F
    for (amount in list){
        sumlist += amount
    }
    val formatter = DecimalFormat("$#,###.00")
    return formatter.format(sumlist)
}
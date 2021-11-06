package com.example.newevent2.Functions

import java.text.DecimalFormat

fun <T> clone(list: ArrayList<T>): ArrayList<T> {
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
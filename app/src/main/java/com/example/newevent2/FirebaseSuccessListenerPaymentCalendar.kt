package com.example.newevent2

import com.example.newevent2.Model.Payment
import java.util.*
import kotlin.collections.ArrayList


interface FirebaseSuccessListenerPaymentCalendar {
    fun onPaymentsDatesEvent(list: ArrayList<Date>)
    fun onPaymentsperDay(list: ArrayList<Payment>)
}
package com.example.newevent2

import com.example.newevent2.Model.Payment
import java.util.ArrayList

interface FirebaseSuccessListenerPayment {
    fun onPaymentEvent(sumpayment: Float)
    fun onPaymentList(list: ArrayList<Payment>)
    fun onPaymentStats(countpayment: Int, sumpayment: Float)
}
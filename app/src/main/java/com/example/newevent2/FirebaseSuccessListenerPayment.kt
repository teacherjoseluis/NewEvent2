package com.example.newevent2

interface FirebaseSuccessListenerPayment {
    fun onPaymentEvent(sumpayment: Float)
    fun onPaymentList(list: ArrayList<Payment>)
    fun onPaymentStats(countpayment: Int, sumpayment: Float)
}
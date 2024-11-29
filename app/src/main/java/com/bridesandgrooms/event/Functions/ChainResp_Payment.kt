package com.bridesandgrooms.event.Functions

import android.content.Context
import com.bridesandgrooms.event.Model.Payment
import com.bridesandgrooms.event.Model.User

interface CoRAddEditPayment {
    fun onAddEditPayment(payment: Payment)
}

interface CoRDeletePayment {
    fun onDeletePayment(paymentId: String)
}
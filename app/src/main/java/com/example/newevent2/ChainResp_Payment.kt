package com.example.newevent2

import com.example.newevent2.Model.Payment

interface CoRAddEditPayment {
    fun onAddEditPayment(payment: Payment)
}

interface CoRDeletePayment {
    fun onDeletePayment(payment: Payment)
}
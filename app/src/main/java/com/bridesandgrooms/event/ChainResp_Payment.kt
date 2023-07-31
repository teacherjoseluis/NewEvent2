package com.bridesandgrooms.event

import com.bridesandgrooms.event.Model.Payment

interface CoRAddEditPayment {
    fun onAddEditPayment(payment: Payment)
}

interface CoRDeletePayment {
    fun onDeletePayment(payment: Payment)
}
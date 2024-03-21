package com.bridesandgrooms.event.Functions

import Application.CalendarEvent
import Application.PaymentCreationException
import Application.PaymentDeletionException
import android.content.Context
import android.util.Log
import com.bridesandgrooms.event.Model.*

internal fun addPayment(context: Context,  userItem: User, paymentitem: Payment) {
    try {
        val calendarevent = CalendarEvent(context)
        val userdbhelper = UserDBHelper(context)
        val paymentmodel = PaymentModel()
        val paymentdbhelper = PaymentDBHelper(context)
        val usermodel = UserModel(userItem)

        val chainofcommand = orderChainAdd(calendarevent, paymentmodel, paymentdbhelper, userdbhelper, usermodel)
        chainofcommand.onAddEditPayment(context, userItem, paymentitem)
    } catch (e: Exception) {
        Log.e("PaymentFunctions.kt", e.message.toString())
        throw PaymentCreationException("Error during payment Creation: $e")
    }
}

internal fun deletePayment(context: Context, userItem: User, paymentitem: Payment) {
    try {
        val calendarevent = CalendarEvent(context)
        val userdbhelper = UserDBHelper(context)
        val usermodel = UserModel(userItem)
        val paymentmodel = PaymentModel()
        val paymentdbhelper = PaymentDBHelper(context)

        val chainofcommand =
            orderChainDel(calendarevent, userdbhelper, usermodel, paymentdbhelper, paymentmodel)
        chainofcommand.onDeletePayment(context, userItem, paymentitem)
    } catch (e: Exception) {
        Log.e("PaymentFunctions.kt", e.message.toString())
        throw PaymentDeletionException("Error during payment Deletion: $e")
    }
}

internal fun editPayment(context: Context, userItem: User, paymentitem: Payment) {
    try {
        val calendarevent = CalendarEvent(context)
        val paymentmodel = PaymentModel()
        val paymentdbhelper = PaymentDBHelper(context)

        val chainofcommand = orderChainEdit(calendarevent, paymentmodel, paymentdbhelper)
        chainofcommand.onAddEditPayment(context, userItem, paymentitem)
    } catch (e: Exception) {
        Log.e("PaymentFunctions.kt", e.message.toString())
        throw PaymentCreationException("Error during payment Edition: $e")
    }
}

private fun orderChainAdd(
    calendarEvent: CalendarEvent,
    paymentModel: PaymentModel,
    paymentDBHelper: PaymentDBHelper,
    userdbhelper: UserDBHelper,
    userModel: UserModel
): CoRAddEditPayment {
    calendarEvent.nexthandlerp = paymentModel
    paymentModel.nexthandler = paymentDBHelper
    paymentDBHelper.nexthandler = userdbhelper
    userdbhelper.nexthandlerp = userModel
    return calendarEvent
}

private fun orderChainDel(
    calendarEvent: CalendarEvent,
    userdbhelper: UserDBHelper,
    userModel: UserModel,
    paymentDBHelper: PaymentDBHelper,
    paymentModel: PaymentModel
): CoRDeletePayment {
    calendarEvent.nexthandlerpdel = userdbhelper
    userdbhelper.nexthandlerpdel = userModel
    userModel.nexthandlerpdel = paymentDBHelper
    paymentDBHelper.nexthandlerpdel = paymentModel
    return calendarEvent
}

private fun orderChainEdit(
    calendarEvent: CalendarEvent,
    paymentModel: PaymentModel,
    paymentDBHelper: PaymentDBHelper
): CoRAddEditPayment {
    calendarEvent.nexthandlerp = paymentModel
    paymentModel.nexthandler = paymentDBHelper
    return calendarEvent
}

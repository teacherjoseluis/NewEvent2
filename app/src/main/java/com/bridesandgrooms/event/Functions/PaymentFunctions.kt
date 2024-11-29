package com.bridesandgrooms.event.Functions

import Application.CalendarCreationException
import Application.CalendarEditionException
import Application.CalendarEvent
import Application.PaymentCreationException
import Application.PaymentDeletionException
import Application.TaskCreationException
import Application.UserEditionException
import android.content.Context
import android.util.Log
import com.bridesandgrooms.event.Model.*

internal fun addPayment(paymentitem: Payment) {
    try {
        val calendarevent = CalendarEvent.getInstance()
        val userdbhelper = UserDBHelper()
        val paymentmodel = PaymentModel()
        val paymentdbhelper = PaymentDBHelper()
        val usermodel = UserModel()

        val chainofcommand =
            orderChainAdd(calendarevent, paymentmodel, paymentdbhelper, userdbhelper, usermodel)
        chainofcommand.onAddEditPayment(paymentitem)
        //-------------------------------------------------------
    } catch (e: UserEditionException) {
        throw PaymentCreationException("Error while trying to edit the User: $e")
    } catch (e: CalendarEditionException) {
        throw PaymentCreationException("Error while trying to add the Payment to the local Calendar: $e")
        //-------------------------------------------------------
    } catch (e: Exception) {
        Log.e("PaymentFunctions.kt", e.message.toString())
        throw PaymentCreationException("Error during payment Creation: $e")
    }
}

internal fun deletePayment(paymentId: String) {
    try {
        val calendarevent = CalendarEvent.getInstance()
        val userdbhelper = UserDBHelper()
        val usermodel = UserModel()
        val paymentmodel = PaymentModel()
        val paymentdbhelper = PaymentDBHelper()

        val chainofcommand =
            orderChainDel(calendarevent, userdbhelper, usermodel, paymentdbhelper, paymentmodel)
        chainofcommand.onDeletePayment(paymentId)
    } catch (e: Exception) {
        Log.e("PaymentFunctions.kt", e.message.toString())
        throw PaymentDeletionException("Error during payment Deletion: $e")
    }
}

internal fun editPayment(paymentitem: Payment) {
    try {
        val calendarevent = CalendarEvent.getInstance()
        val paymentmodel = PaymentModel()
        val paymentdbhelper = PaymentDBHelper()

        val chainofcommand = orderChainEdit(calendarevent, paymentmodel, paymentdbhelper)
        chainofcommand.onAddEditPayment(paymentitem)
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

package com.example.newevent2.Functions

import Application.CalendarEvent
import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.widget.Toast
import com.example.newevent2.CoRAddEditPayment
import com.example.newevent2.CoRDeletePayment
import com.example.newevent2.Model.*
import com.example.newevent2.R

@SuppressLint("StaticFieldLeak")
private lateinit var calendarevent : CalendarEvent
var paymentmodel = PaymentModel()
lateinit var paymentdbhelper: PaymentDBHelper
private lateinit var usermodel: UserModel

internal fun addPayment(context: Context, paymentitem: Payment) {
    try {
        //------------------------------------------------
        // Adding Calendar Event
        calendarevent = CalendarEvent(context)
        //------------------------------------------------
        // Updating User information in Local DB
        userdbhelper = UserDBHelper(context)
        //------------------------------------------------

        // Adding a new record in Firebase
        val user = userdbhelper.getUser(userdbhelper.getUserKey())
        paymentmodel.userid = user.key
        paymentmodel.eventid = user.eventid
        //taskmodel.task = taskitem
        //------------------------------------------------
        // Adding a new record in Local DB
        paymentdbhelper = PaymentDBHelper(context)
        //taskdbhelper.task = taskitem
        //------------------------------------------------

        // Updating User information in Firebase
        usermodel = UserModel(user.key)
        //usermodel.tasksactive = user.tasksactive
        //------------------------------------------------
        val chainofcommand = orderChainAdd(calendarevent, paymentmodel, paymentdbhelper, userdbhelper, usermodel)
        chainofcommand.onAddEditPayment(paymentitem)
        //------------------------------------------------

        // ------- Analytics call ----------------
        val bundle = Bundle()
        bundle.putString("CATEGORY", paymentitem.category)
        bundle.putString("AMOUNT", paymentitem.amount)
        bundle.putString("COUNTRY", user.country)
        MyFirebaseApp.mFirebaseAnalytics.logEvent("ADDPAYMENT", bundle)
        //------------------------------------------------
        Toast.makeText(context, context.getString(R.string.succesfulpayment), Toast.LENGTH_LONG).show()
    } catch (e: Exception) {
        val errormsg = context.getString(R.string.failedpayment)
        errormsg.plus(e.message)
        Toast.makeText(
            context,
            errormsg,
            Toast.LENGTH_LONG
        ).show()
    }
}

internal fun deletePayment(context: Context, paymentitem: Payment) {
    try {
        //------------------------------------------------
        // Adding Calendar Event
        calendarevent = CalendarEvent(context)
        //------------------------------------------------
        // Updating User information in Local DB
        userdbhelper = UserDBHelper(context)
        //------------------------------------------------

        val user = userdbhelper.getUser(userdbhelper.getUserKey())
        paymentmodel.userid = user.key
        paymentmodel.eventid = user.eventid
        //taskmodel.task = taskitem
        //------------------------------------------------
        // Adding a new record in Local DB
        paymentdbhelper = PaymentDBHelper(context)
        paymentdbhelper.payment = paymentitem
        //------------------------------------------------


        // Updating User information in Firebase
        usermodel = UserModel(user.key)
        usermodel.paymentsactive = user.payments
        //------------------------------------------------

        val chainofcommand =
            orderChainDel(calendarevent, userdbhelper, usermodel, paymentdbhelper, paymentmodel)
        chainofcommand.onDeletePayment(paymentitem)
        //------------------------------------------------
        // ------- Analytics call ----------------
        val bundle = Bundle()
        bundle.putString("CATEGORY", paymentitem.category)
        bundle.putString("AMOUNT", paymentitem.amount)
        bundle.putString("COUNTRY", user.country)
        MyFirebaseApp.mFirebaseAnalytics.logEvent("DELETEPAYMENT", bundle)
        //------------------------------------------------
        Toast.makeText(context, context.getString(R.string.succesfuldeletepayment), Toast.LENGTH_LONG).show()
    } catch (e: Exception) {
        val errormsg = context.getString(R.string.faileddeletepayment)
        errormsg.plus(e.message)
        Toast.makeText(
            context,
            errormsg,
            Toast.LENGTH_LONG
        ).show()
    }
}

internal fun editPayment(context: Context, paymentitem: Payment) {
    try {
        //------------------------------------------------
        // Adding Calendar Event
        calendarevent = CalendarEvent(context)
        //---------------------------------------------------
        val user = getUserSession(context)
        paymentmodel.userid = user.key
        paymentmodel.eventid = user.eventid
        //taskmodel.task = taskitem
        //------------------------------------------------
        // Adding a new record in Local DB
        paymentdbhelper = PaymentDBHelper(context)
        //taskdbhelper.task = taskitem
        //------------------------------------------------

        val chainofcommand = orderChainEdit(calendarevent, paymentmodel, paymentdbhelper)
        chainofcommand.onAddEditPayment(paymentitem)
        //------------------------------------------------
        // ------- Analytics call ----------------
        val bundle = Bundle()
        bundle.putString("CATEGORY", paymentitem.category)
        bundle.putString("AMOUNT", paymentitem.amount)
        bundle.putString("COUNTRY", user.country)
        MyFirebaseApp.mFirebaseAnalytics.logEvent("EDITPAYMENT", bundle)
        //------------------------------------------------
        Toast.makeText(context, context.getString(R.string.succesfuleditpayment), Toast.LENGTH_LONG).show()
    } catch (e: Exception) {
        val errormsg = context.getString(R.string.failededitpayment)
        errormsg.plus(e.message)
        Toast.makeText(
            context,
            errormsg,
            Toast.LENGTH_LONG
        ).show()
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
    userdbhelper.nexthandleru = userModel
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
    userdbhelper.nexthandleru = userModel
    userModel.nexthandlerdelp = paymentDBHelper
    paymentDBHelper.nexthandlerdel = paymentModel
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

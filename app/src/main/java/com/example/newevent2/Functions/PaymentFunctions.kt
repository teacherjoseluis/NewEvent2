package com.example.newevent2.Functions

import android.content.Context
import android.widget.Toast
import com.example.newevent2.CoRAddEditPayment
import com.example.newevent2.CoRAddEditTask
import com.example.newevent2.CoRDeletePayment
import com.example.newevent2.CoRDeleteTask
import com.example.newevent2.Model.*

var paymentmodel = PaymentModel()
lateinit var paymentdbhelper: PaymentDBHelper
private lateinit var usermodel: UserModel

internal fun addPayment(context: Context, paymentitem: Payment) {
    try {
        //------------------------------------------------
        // Adding a new record in Firebase
        val user = com.example.newevent2.Functions.getUserSession(context!!)
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
        val chainofcommand = orderChainAdd(paymentmodel, paymentdbhelper, usermodel)
        chainofcommand.onAddEditPayment(paymentitem)
        //------------------------------------------------
        // Updating User information in Session
        user.payments = user.payments + 1
        user.haspayment = PaymentModel.ACTIVEFLAG
        user.saveUserSession(context)
        //------------------------------------------------
        Toast.makeText(context, "Payment was created successully", Toast.LENGTH_LONG).show()
    } catch (e: Exception) {
        Toast.makeText(
            context,
            "There was an error trying create the payment ${e.message}",
            Toast.LENGTH_LONG
        ).show()
    }
}

internal fun deletePayment(context: Context, paymentitem: Payment) {
    try {
        val user = getUserSession(context!!)
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
        // Updating User information in Session
        user.payments = user.payments - 1
        if (user.payments == 0) user.haspayment = TaskModel.INACTIVEFLAG
        user.saveUserSession(context)

        val chainofcommand =
            orderChainDel(usermodel, paymentdbhelper, paymentmodel)
        chainofcommand.onDeletePayment(paymentitem)
        //------------------------------------------------
        Toast.makeText(context, "Payment was deleted successully", Toast.LENGTH_LONG).show()
    } catch (e: Exception) {
        Toast.makeText(
            context,
            "There was an error trying delete the payment ${e.message}",
            Toast.LENGTH_LONG
        ).show()
    }
}

internal fun editPayment(context: Context, paymentitem: Payment) {
    try {
        //---------------------------------------------------
        val user = getUserSession(context!!)
        paymentmodel.userid = user.key
        paymentmodel.eventid = user.eventid
        //taskmodel.task = taskitem
        //------------------------------------------------
        // Adding a new record in Local DB
        paymentdbhelper = PaymentDBHelper(context)
        //taskdbhelper.task = taskitem
        //------------------------------------------------

        val chainofcommand = orderChainEdit(paymentmodel, paymentdbhelper)
        chainofcommand.onAddEditPayment(paymentitem)
        //------------------------------------------------
        Toast.makeText(context, "Payment was edited successully", Toast.LENGTH_LONG).show()
    } catch (e: Exception) {
        Toast.makeText(
            context,
            "There was an error trying edit the payment ${e.message}",
            Toast.LENGTH_LONG
        ).show()
    }
}

private fun orderChainAdd(
    paymentModel: PaymentModel,
    paymentDBHelper: PaymentDBHelper,
    userModel: UserModel
): CoRAddEditPayment {
    paymentModel.nexthandler = paymentDBHelper
    paymentDBHelper.nexthandler = userModel
    return paymentmodel
}

private fun orderChainDel(
    userModel: UserModel,
    paymentDBHelper: PaymentDBHelper,
    paymentModel: PaymentModel
): CoRDeletePayment {
    userModel.nexthandlerdelp = paymentDBHelper
    paymentDBHelper.nexthandlerdel = paymentModel
    return userModel
}

private fun orderChainEdit(
    paymentModel: PaymentModel,
    paymentDBHelper: PaymentDBHelper
): CoRAddEditPayment {
    paymentModel.nexthandler = paymentDBHelper
    return paymentmodel
}

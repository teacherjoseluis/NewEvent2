package com.bridesandgrooms.event.MVP

import android.content.Context
import android.util.Log
import com.bridesandgrooms.event.MVP.TaskPresenter.Companion.ERRCODETASKS
import com.bridesandgrooms.event.Model.Payment
import com.bridesandgrooms.event.Model.PaymentDBHelper
import com.bridesandgrooms.event.Model.Task
import com.bridesandgrooms.event.Model.TaskDBHelper
import com.bridesandgrooms.event.UI.Fragments.PaymentsAllCalendar
import com.bridesandgrooms.event.UI.Fragments.TasksAllCalendar
import java.util.Date

class TasksPaymentAllCalendarPresenter(
    val context: Context,
    val fragmentTask: TasksAllCalendar?,
    val fragmentPayment: PaymentsAllCalendar?
) {

    fun getDateTaskArray(date: Date) {
        val taskDBHelper = TaskDBHelper(context)
        try {
            val taskArray = taskDBHelper.getDateTaskArray(date)
            fragmentTask?.onTaskArray(taskArray)
        } catch (e: Exception) {
            Log.e("TasksPaymentAllCalendarPresenter", e.message.toString())
            fragmentTask?.onTaskArrayError(ERRCODETASKS)
        }
    }

    fun getDatePaymentArray(date: Date) {
        val paymentDBHelper = PaymentDBHelper(context)
        try {
            val paymentArray = paymentDBHelper.getDatePaymentArray(date)
            fragmentPayment?.onPaymentArray(paymentArray)
        } catch (e: Exception) {
            Log.e("TasksPaymentAllCalendarPresenter", e.message.toString())
            fragmentPayment?.onPaymentArrayError(ERRCODETASKS)
        }
    }

    interface TaskArrayInterface {
        fun onTaskArray(list: ArrayList<Task>?)
        fun onTaskArrayError(errcode: String)
    }

    interface PaymentArrayInterface {
        fun onPaymentArray(list: ArrayList<Payment>?)
        fun onPaymentArrayError(errcode: String)
    }

}


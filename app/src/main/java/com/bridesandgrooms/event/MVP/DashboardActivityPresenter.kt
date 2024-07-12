package com.bridesandgrooms.event.MVP

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.applandeo.materialcalendarview.CalendarDay
import com.bridesandgrooms.event.DashboardActivity
import com.bridesandgrooms.event.Functions.converttoDate
import com.bridesandgrooms.event.MVP.TaskPresenter.Companion.ERRCODETASKS
import com.bridesandgrooms.event.Model.PaymentDBHelper
import com.bridesandgrooms.event.Model.Task
import com.bridesandgrooms.event.Model.TaskDBHelper
import com.bridesandgrooms.event.Model.TaskJournal
import java.util.Calendar
import java.util.Date

class DashboardActivityPresenter(
    val context: Context,
    val fragment: DashboardActivity
) {

    fun getTasksFromMonthYear(month: Int, year: Int) {
        val taskDBHelper = TaskDBHelper(context)
        try {
            val taskCalendarList = taskDBHelper.getTasksFromMonthYear(month, year)
            fragment.onTaskCalendar(taskCalendarList)
        } catch (e: Exception) {
            Log.e("DashboardActivityPresenter", e.message.toString())
            fragment.onTaskCalendarError(ERRCODETASKS)
        }
    }

    fun getTaskFromDate(date: Date) {
        val taskDBHelper = TaskDBHelper(context)
        try {
            val taskCalendarList = taskDBHelper.getTaskfromDate(date)
            fragment.onTaskCalendar(taskCalendarList)
        } catch (e: Exception) {
            Log.e("DashboardActivityPresenter", e.message.toString())
            fragment.onTaskCalendarError(ERRCODETASKS)
        }
    }

    fun getPaymentsFromMonthYear(month: Int, year: Int) {
        val paymentDBHelper = PaymentDBHelper(context)
        try {
            val paymentCalendarList = paymentDBHelper.getPaymentsFromMonthYear(month, year)
            fragment.onPaymentCalendar(paymentCalendarList)
        } catch (e: Exception) {
            Log.e("DashboardActivityPresenter", e.message.toString())
            fragment.onPaymentCalendarError(ERRCODETASKS)
        }
    }

    fun getPaymentFromDate(date: Date) {
        val paymentDBHelper = PaymentDBHelper(context)
        try {
            val paymentCalendarList = paymentDBHelper.getPaymentfromDate(date)
            fragment.onPaymentCalendar(paymentCalendarList)
        } catch (e: Exception) {
            Log.e("DashboardActivityPresenter", e.message.toString())
            fragment.onPaymentCalendarError(ERRCODETASKS)
        }
    }

    interface TaskCalendarInterface {
        fun onTaskCalendar(
            list: List<Date>?
        )
        fun onTaskCalendar(
            list: ArrayList<String>?
        )

        fun onTaskCalendarError(errcode: String)
    }

    interface PaymentCalendarInterface {
        fun onPaymentCalendar(
            list: List<Date>?
        )
        fun onPaymentCalendar(
            list: ArrayList<String>?
        )

        fun onPaymentCalendarError(errcode: String)
    }
}


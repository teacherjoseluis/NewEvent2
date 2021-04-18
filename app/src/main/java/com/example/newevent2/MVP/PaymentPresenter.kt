package com.example.newevent2.MVP

import android.app.Activity
import android.os.Build
import androidx.annotation.RequiresApi
import com.example.newevent2.FirebaseSuccessListenerLogWelcome
import com.example.newevent2.Functions.FirebaseGetLogSuccess
import com.example.newevent2.LoginView
import com.example.newevent2.Functions.Loginfo
import com.example.newevent2.Functions.getLog
import com.example.newevent2.Functions.removeLog
import com.example.newevent2.Model.PaymentModel
import com.example.newevent2.Model.Task
import com.example.newevent2.Model.TaskModel
import com.example.newevent2.WelcomeView
import java.text.SimpleDateFormat
import java.time.Duration
import java.time.Instant.now
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList

class PaymentPresenter(
    view: WelcomeView,
    val userid: String,
    val eventid: String
) {
    var viewWelcome: WelcomeView = view

    fun getPaymentStats(category: String) {
        val payment = PaymentModel()
        payment.getPaymentStats(
            userid,
            eventid,
            category,
            object : PaymentModel.FirebaseSuccessStatsPayment {
                override fun onPaymentStats(countpayment: Int, sumpayment: Float) {
                    if (countpayment == 0) {
                        //There are no payments made
                        viewWelcome.onViewPaymentError("BLANK_STATS")
                    } else {
                        // Get the total Task Budget
                        val task = TaskModel()
                        task.getTasksBudget(
                            userid,
                            eventid,
                            object : TaskModel.FirebaseSuccessTaskBudget {
                                override fun onTasksBudget(sumbudget: Float) {
                                    //Show the stats
                                    viewWelcome.onViewPaymentStatsSuccess(
                                        countpayment,
                                        sumpayment,
                                        sumbudget
                                    )
                                }
                            })
                    }
                }
            })
    }

    interface ViewPaymentWelcomeActivity {
        fun onViewPaymentStatsSuccess(countpayment: Int, sumpayment: Float, sumbudget: Float)
        fun onViewPaymentError(errcode: String)
    }
}
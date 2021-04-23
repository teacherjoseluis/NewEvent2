package com.example.newevent2.MVP

import com.example.newevent2.DashboardEvent
import com.example.newevent2.Model.PaymentModel
import com.example.newevent2.Model.TaskModel
import com.example.newevent2.DashboardView

class PaymentPresenter(
    view: DashboardEvent,
    val userid: String,
    val eventid: String
) {
    var viewDashboardEvent: DashboardEvent = view

    fun getPaymentStats(category: String = "") {
        val payment = PaymentModel()
        payment.getPaymentStats(
            userid,
            eventid,
            category,
            object : PaymentModel.FirebaseSuccessStatsPayment {
                override fun onPaymentStats(countpayment: Int, sumpayment: Float) {
                    if (countpayment == 0) {
                        //There are no payments made
                        viewDashboardEvent.onViewPaymentError("BLANK_STATS")
                    } else {
                        // Get the total Task Budget
                        val task = TaskModel()
                        task.getTasksBudget(
                            userid,
                            eventid,
                            object : TaskModel.FirebaseSuccessTaskBudget {
                                override fun onTasksBudget(sumbudget: Float) {
                                    //Show the stats
                                    viewDashboardEvent.onViewPaymentStatsSuccess(
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
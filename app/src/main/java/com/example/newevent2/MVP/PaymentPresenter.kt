package com.example.newevent2.MVP

import android.view.View
import com.example.newevent2.DashboardEvent
import com.example.newevent2.Model.PaymentModel
import com.example.newevent2.Model.TaskModel
import com.example.newevent2.DashboardView
import com.example.newevent2.MainEventSummary

class PaymentPresenter {

    var userid = ""
    var eventid = ""
    lateinit var inflatedView: View
    lateinit var viewDashboardEvent: DashboardEvent
    lateinit var fragmentEventSummary: MainEventSummary

    constructor(view: DashboardEvent, userid: String, eventid: String) {
        this.userid = userid
        this.eventid = eventid
        viewDashboardEvent = view
    }

    constructor(fragment: MainEventSummary, view: View, userid: String, eventid: String) {
        this.userid = userid
        this.eventid = eventid
        inflatedView = view
        fragmentEventSummary = fragment
    }

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
                        if (::viewDashboardEvent.isInitialized) {
                            viewDashboardEvent.onViewPaymentError("BLANK_STATS")
                        } else if (::fragmentEventSummary.isInitialized) {
                            fragmentEventSummary.onViewPaymentErrorFragment(inflatedView, "BLANK_STATS")
                        }
                    } else {
                        // Get the total Task Budget
                        val task = TaskModel()
                        task.getTasksBudget(
                            userid,
                            eventid,
                            object : TaskModel.FirebaseSuccessTaskBudget {
                                override fun onTasksBudget(sumbudget: Float) {
                                    //Show the stats
                                    if (::viewDashboardEvent.isInitialized) {
                                        viewDashboardEvent.onViewPaymentStatsSuccess(
                                            countpayment,
                                            sumpayment,
                                            sumbudget
                                        )
                                    } else if (::fragmentEventSummary.isInitialized) {
                                        fragmentEventSummary.onViewPaymentStatsSuccessFragment(inflatedView,
                                            countpayment,
                                            sumpayment,
                                            sumbudget
                                        )
                                    }
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

    interface ViewPaymentFragment {
        fun onViewPaymentStatsSuccessFragment(
            inflatedView: View,
            countpayment: Int, sumpayment: Float, sumbudget: Float
        )

        fun onViewPaymentErrorFragment(inflatedView: View, errcode: String)
    }
}
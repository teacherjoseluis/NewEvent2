package com.example.newevent2.MVP

import android.os.Build
import android.view.View
import androidx.annotation.RequiresApi
import com.example.newevent2.*
import com.example.newevent2.Model.PaymentModel
import com.example.newevent2.Model.TaskModel
import java.util.ArrayList

class PaymentPresenter {

    var userid = ""
    var eventid = ""
    lateinit var inflatedView: View
    lateinit var viewDashboardEvent: DashboardEvent
    lateinit var fragmentEventSummary: MainEventSummary
    lateinit var fragmentTaskPaymentPayment: TaskPayment_Payments

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

    constructor(fragment: TaskPayment_Payments, view: View, userid: String, eventid: String) {
        this.userid = userid
        this.eventid = eventid
        fragmentTaskPaymentPayment = fragment
        inflatedView = view
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
                        } else if(::fragmentTaskPaymentPayment.isInitialized){
                            fragmentTaskPaymentPayment.onViewPaymentErrorFragment(inflatedView, "BLANK_STATS")
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
                                    } else if (::fragmentTaskPaymentPayment.isInitialized) {
                                        fragmentTaskPaymentPayment.onViewPaymentStatsSuccessFragment(inflatedView,
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

    fun getPaymentList(category: String) {
        val payment = PaymentModel()
        payment.getPaymentsList(
            userid,
            eventid,
            category,
            object : PaymentModel.FirebaseSuccessPaymentList {
                @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
                override fun onPaymentList(list: ArrayList<com.example.newevent2.Model.Payment>) {
                    if (list.isNotEmpty()) {
                        fragmentTaskPaymentPayment.onViewPaymentListFragment(
                            inflatedView,
                            category,
                            list
                        )
                    } else {
                        fragmentTaskPaymentPayment.onViewPaymentListErrorFragment(
                            inflatedView,
                            category,
                            "NO_TASKS"
                        )
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

    interface ViewPaymentList {
        fun onViewPaymentListFragment(
            inflatedView: View,
            category: String,
            list: ArrayList<com.example.newevent2.Model.Payment>
        )

        fun onViewPaymentListErrorFragment(
            inflatedView: View,
            category: String,
            errcode: String
        )
    }
}
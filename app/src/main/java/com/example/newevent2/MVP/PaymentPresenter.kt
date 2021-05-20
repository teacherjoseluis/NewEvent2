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
    lateinit var fragmentDashboard: DashboardEvent
    lateinit var fragmentEventSummary: MainEventSummary
    lateinit var fragmentTaskPaymentPayment: TaskPayment_Payments

    constructor(fragment: DashboardEvent, view: View, userid: String, eventid: String) {
        this.userid = userid
        this.eventid = eventid
        inflatedView = view
        fragmentDashboard = fragment
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
                        when {
                            ::fragmentDashboard.isInitialized -> {
                                fragmentDashboard.onPaymentStatsError(
                                    inflatedView,
                                    "BLANK_STATS"
                                )
                            }
                            ::fragmentEventSummary.isInitialized -> {
                                fragmentEventSummary.onPaymentStatsError(
                                    inflatedView,
                                    "BLANK_STATS"
                                )
                            }
                            ::fragmentTaskPaymentPayment.isInitialized -> {
                                fragmentTaskPaymentPayment.onPaymentStatsError(
                                    inflatedView,
                                    "BLANK_STATS"
                                )
                            }
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
                                    when {
                                        ::fragmentDashboard.isInitialized -> {
                                            fragmentDashboard.onPaymentStats(
                                                inflatedView,
                                                countpayment,
                                                sumpayment,
                                                sumbudget
                                            )
                                        }
                                        ::fragmentEventSummary.isInitialized -> {
                                            fragmentEventSummary.onPaymentStats(
                                                inflatedView,
                                                countpayment,
                                                sumpayment,
                                                sumbudget
                                            )
                                        }
                                        ::fragmentTaskPaymentPayment.isInitialized -> {
                                            fragmentTaskPaymentPayment.onPaymentStats(
                                                inflatedView,
                                                countpayment,
                                                sumpayment,
                                                sumbudget
                                            )
                                        }
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
                        fragmentTaskPaymentPayment.onPaymentList(
                            inflatedView,
                            category,
                            list
                        )
                    } else {
                        fragmentTaskPaymentPayment.onPaymentListError(
                            inflatedView,
                            category,
                            "NO_TASKS"
                        )
                    }
                }
            })
    }

    interface PaymentStats {
        fun onPaymentStats(
            inflatedView: View,
            countpayment: Int, sumpayment: Float, sumbudget: Float
        )

        fun onPaymentStatsError(inflatedView: View, errcode: String)
    }

    interface PaymentList {
        fun onPaymentList(
            inflatedView: View,
            category: String,
            list: ArrayList<com.example.newevent2.Model.Payment>
        )

        fun onPaymentListError(
            inflatedView: View,
            category: String,
            errcode: String
        )
    }
}
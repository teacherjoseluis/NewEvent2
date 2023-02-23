package com.example.newevent2.MVP

import Application.Cache
import android.content.Context
import android.os.Build.VERSION_CODES
import androidx.annotation.RequiresApi
import com.example.newevent2.*
import com.example.newevent2.Functions.userdbhelper
import com.example.newevent2.Model.*
import com.example.newevent2.Model.Payment
import kotlin.collections.ArrayList

class PaymentPresenter : Cache.PaymentArrayListCacheData {

    private var activefragment = ""
    private var mContext: Context

    private lateinit var fragmentDE: DashboardEventPresenter
    private lateinit var fragmentTPP: TaskPaymentPaymentsPresenter

    private lateinit var cachepayment: Cache<Payment>

    constructor(context: Context, fragment: DashboardEventPresenter) {
        fragmentDE = fragment
        mContext = context
        activefragment = "DE"
    }

    constructor(context: Context, fragment: TaskPaymentPaymentsPresenter) {
        fragmentTPP = fragment
        mContext = context
        activefragment = "TPP"
    }


    @RequiresApi(VERSION_CODES.LOLLIPOP)
    fun getPaymentsList() {
        cachepayment = Cache(mContext, this)
        cachepayment.loadarraylist(Payment::class)
    }
//
//    fun getPaymentStats(category: String = "") {
//        if (category != "") {
//            val payment = PaymentModel()
//            payment.getPaymentStats(
//                userid,
//                eventid,
//                category,
//                object : PaymentModel.FirebaseSuccessStatsPayment {
//                    override fun onPaymentStats(countpayment: Int, sumpayment: Float) {
//                        if (countpayment == 0) {
//                            //There are no payments made
//                            when (activefragment) {
//                                "DE" -> fragmentDE.onPaymentStatsError(inflatedView, "BLANK_STATS")
//                                //"ME" -> fragmentME.onPaymentStatsError(inflatedView, "BLANK_STATS")
//                                "TP" -> fragmentTP.onPaymentStatsError(inflatedView, "BLANK_STATS")
//                            }
//                        } else {
//                            // Get the total Task Budget
//                            val task = TaskModel()
//                            task.getTasksBudget(
//                                userid,
//                                eventid,
//                                object : TaskModel.FirebaseSuccessTaskBudget {
//                                    override fun onTasksBudget(sumbudget: Float) {
//                                        //Show the stats
//                                        when (activefragment) {
//                                            "DE" -> {
//                                                fragmentDE.onPaymentStats(
//                                                    inflatedView,
//                                                    countpayment,
//                                                    sumpayment,
//                                                    sumbudget
//                                                )
//                                            }
////                                        "ME" -> {
////                                            fragmentME.onPaymentStats(
////                                                inflatedView,
////                                                countpayment,
////                                                sumpayment,
////                                                sumbudget
////                                            )
////                                        }
//                                            "TP" -> {
//                                                fragmentTP.onPaymentStats(
//                                                    inflatedView,
//                                                    countpayment,
//                                                    sumpayment,
//                                                    sumbudget
//                                                )
//                                            }
//                                        }
//                                    }
//                                })
//                        }
//                    }
//                })
//
//        } else {
//            paymentcategory = category
////            cachepaymenttoken = Cache(mContext, this)
////            cachepaymenttoken.loadsingleitem(
////                CacheCategory.PaymentStatsAll,
////                PaymentStatsToken::class
////            )
//        }
//    }


//    private fun filterpaymentlist(arrayList: ArrayList<Payment>): java.util.ArrayList<Payment> {
//        // The below code returns an ArrayList based on the Category and Status requested
//        var paymentlist = java.util.ArrayList<Payment>()
//        for (paymentitem in arrayList) {
//            if (paymentcategory != "") {
//                if (paymentitem!!.category == paymentcategory) {
//                    paymentlist.add(paymentitem!!)
//                }
//            }
//        }
//        return paymentlist
//    }

    override fun onArrayListP(arrayList: ArrayList<Payment>) {
        if (arrayList.size == 0) {
            when (activefragment) {
                "DE" -> fragmentDE.onPaymentListError(
                    ERRCODEPAYMENTS
                )
                "TPP" -> fragmentTPP.onPaymentListError(
                    ERRCODEPAYMENTS
                )
            }
        } else {
            when (activefragment) {
                "DE" -> fragmentDE.onPaymentList(arrayList)
                "TPP" -> fragmentTPP.onPaymentList(arrayList)
            }
        }
    }

    override fun onEmptyListP() {
        val user = userdbhelper.getUser(userdbhelper.getUserKey())
        val payment = PaymentModel()
        payment.getPaymentsList(
            user.key!!,
            user.eventid,
            object : PaymentModel.FirebaseSuccessPaymentList {
                @RequiresApi(VERSION_CODES.LOLLIPOP)
                override fun onPaymentList(arrayList: ArrayList<Payment>) {
                    if (arrayList.isNotEmpty()) {
                        cachepayment.save(arrayList)

                        when (activefragment) {
                            "DE" -> fragmentDE.onPaymentList(arrayList)
                            "TPP" -> fragmentTPP.onPaymentList(arrayList)
                        }
                    } else {
                        when (activefragment) {
                            "DE" -> fragmentDE.onPaymentListError(ERRCODEPAYMENTS)
                            "TPP" -> fragmentTPP.onPaymentListError(ERRCODEPAYMENTS)
                        }
                    }
                }
            })
    }

    interface PaymentList {
        fun onPaymentList(list: ArrayList<Payment>)
        fun onPaymentListError(errcode: String)
    }

    companion object {
        const val ERRCODEPAYMENTS = "NOPAYMENTS"
    }
}

package com.example.newevent2.MVP

import Application.Cache
import Application.CacheCategory
import android.content.Context
import android.os.Build
import android.view.View
import androidx.annotation.RequiresApi
import com.example.newevent2.*
import com.example.newevent2.Model.*
import com.example.newevent2.Model.Payment
import kotlin.collections.ArrayList

class PaymentPresenter : Cache.ArrayListCacheData, Cache.SingleItemCacheData {

    private var activefragment = ""
    private var inflatedView: View
    private var mContext: Context

    private var paymentcategory = ""

    private lateinit var cachepayment: Cache<Payment>
    private lateinit var cachepaymenttoken: Cache<PaymentStatsToken>

    private lateinit var fragmentDE: DashboardEvent
    private lateinit var fragmentME: EventCategories
    private lateinit var fragmentTP: TaskPayment_Payments

    var userid = ""
    var eventid = ""

    constructor(context: Context, fragment: DashboardEvent, view: View) {
        fragmentDE = fragment
        inflatedView = view
        mContext = context
        activefragment = "DE"
    }

    constructor(context: Context, fragment: EventCategories, view: View) {
        fragmentME = fragment
        inflatedView = view
        mContext = context
        activefragment = "ME"
    }

    // I might as well be removing the call to the presenter from this fragment
    constructor(context: Context, fragment: TaskPayment_Payments, view: View) {
        fragmentTP = fragment
        inflatedView = view
        mContext = context
        activefragment = "TP"
    }

    fun getPaymentStats(category: String = "") {
        if (category != "") {
            val payment = PaymentModel()
            payment.getPaymentStats(
                userid,
                eventid,
                category,
                object : PaymentModel.FirebaseSuccessStatsPayment {
                    override fun onPaymentStats(countpayment: Int, sumpayment: Float) {
                        if (countpayment == 0) {
                            //There are no payments made
                            when (activefragment) {
                                "DE" -> fragmentDE.onPaymentStatsError(inflatedView, "BLANK_STATS")
                                //"ME" -> fragmentME.onPaymentStatsError(inflatedView, "BLANK_STATS")
                                "TP" -> fragmentTP.onPaymentStatsError(inflatedView, "BLANK_STATS")
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
                                        when (activefragment) {
                                            "DE" -> {
                                                fragmentDE.onPaymentStats(
                                                    inflatedView,
                                                    countpayment,
                                                    sumpayment,
                                                    sumbudget
                                                )
                                            }
//                                        "ME" -> {
//                                            fragmentME.onPaymentStats(
//                                                inflatedView,
//                                                countpayment,
//                                                sumpayment,
//                                                sumbudget
//                                            )
//                                        }
                                            "TP" -> {
                                                fragmentTP.onPaymentStats(
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

        } else {
            paymentcategory = category
            cachepaymenttoken = Cache(mContext, this)
            cachepaymenttoken.loadsingleitem(
                CacheCategory.PaymentStatsAll,
                PaymentStatsToken::class
            )
        }
    }

    fun getPaymentList(category: String) {
        paymentcategory = category
        cachepayment = Cache(mContext, this)
        cachepayment.loadarraylist(CacheCategory.ArrayTask, Payment::class)
    }

    private fun filterpaymentlist(arrayList: ArrayList<Payment>): java.util.ArrayList<Payment> {
        // The below code returns an ArrayList based on the Category and Status requested
        var paymentlist = java.util.ArrayList<Payment>()
        for (paymentitem in arrayList) {
            if (paymentcategory != "") {
                if (paymentitem!!.category == paymentcategory) {
                    paymentlist.add(paymentitem!!)
                }
            }
        }
        return paymentlist
    }

    override fun onArrayList(arrayList: ArrayList<*>, classtype: String) {
        if (classtype == CacheCategory.SinglePayment.classtype) {
            // I'm getting an arraylist of Tasks from the Cache
            // Cache stores a complete list of Tasks in the application
            var paymentlist = filterpaymentlist(arrayList as ArrayList<Payment>)

            when (activefragment) {
                "TP" -> fragmentTP.onPaymentList(
                    inflatedView,
                    paymentcategory,
                    paymentlist
                )
            }
        }
    }

    override fun onEmptyList(classtype: String) {
        if (classtype == CacheCategory.SinglePayment.classtype) {
            val payment = PaymentModel()
            payment.getPaymentsList(
                userid,
                eventid,
                object : PaymentModel.FirebaseSuccessPaymentList {
                    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
                    override fun onPaymentList(arrayList: ArrayList<Payment>) {
                        if (arrayList.isNotEmpty()) {
                            cachepayment.save(CacheCategory.ArrayTask, arrayList)

                            var paymentlist = filterpaymentlist(arrayList)
                            when (activefragment) {
                                "TP" -> fragmentTP.onPaymentList(
                                    inflatedView,
                                    paymentcategory,
                                    paymentlist
                                )
                            }
                        } else {
                            when (activefragment) {
                                "TP" -> fragmentTP.onPaymentListError(
                                    inflatedView,
                                    paymentcategory,
                                    "NO_PAYMENTS"
                                )
                            }
                        }
                    }
                })
        }
    }


    override fun onSingleItem(item: Any) {
        if (item is PaymentStatsToken) {
            when (activefragment) {
                "DE" -> fragmentDE.onPaymentStats(
                    inflatedView,
                    item.countpayment,
                    item.sumpayment,
                    item.sumbudget
                )
            }
        }
    }

    override fun onEmptyItem(classtype: String) {
        val payment = PaymentModel()
        payment.getPaymentStats(
            userid,
            eventid,
            paymentcategory,
            object : PaymentModel.FirebaseSuccessStatsPayment {
                override fun onPaymentStats(countpayment: Int, sumpayment: Float) {
                    if (countpayment == 0) {
                        //There are no payments made
                        when (activefragment) {
                            "DE" -> fragmentDE.onPaymentStatsError(inflatedView, "BLANK_STATS")
                            //"ME" -> fragmentME.onPaymentStatsError(inflatedView, "BLANK_STATS")
                            "TP" -> fragmentTP.onPaymentStatsError(inflatedView, "BLANK_STATS")
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
                                    cachepaymenttoken.save(
                                        CacheCategory.PaymentStatsAll,
                                        countpayment,
                                        sumpayment,
                                        sumbudget
                                    )
                                    when (activefragment) {
                                        "DE" -> {
                                            fragmentDE.onPaymentStats(
                                                inflatedView,
                                                countpayment,
                                                sumpayment,
                                                sumbudget
                                            )
                                        }
//                                        "ME" -> {
//                                            fragmentME.onPaymentStats(
//                                                inflatedView,
//                                                countpayment,
//                                                sumpayment,
//                                                sumbudget
//                                            )
//                                        }
                                        "TP" -> {
                                            fragmentTP.onPaymentStats(
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
            list: ArrayList<Payment>
        )

        fun onPaymentListError(
            inflatedView: View,
            category: String,
            errcode: String
        )
    }

}

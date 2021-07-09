package com.example.newevent2

import android.content.Context
import android.view.View
import com.example.newevent2.MVP.PaymentPresenter
import com.example.newevent2.Model.Payment
import kotlin.collections.ArrayList

class TaskPaymentPaymentsPresenter(
    val context: Context,
    val fragment: TaskPaymentPayments,
    val view: View,
    private val paymentcategory: String
) :
    PaymentPresenter.PaymentList {

    private var presenterpayment: PaymentPresenter = PaymentPresenter(context!!, this)

    init {
        presenterpayment.getPaymentsList()
    }

    override fun onPaymentList(list: ArrayList<Payment>) {
        var filteredpaymentlistactive = ArrayList<Payment>()
        for (payment in list) {
            if ((payment.category == paymentcategory) || (paymentcategory == "")) {
//                if (task.status == status) {
                    filteredpaymentlistactive.add(payment)
//                }
            }
        }
        fragment.onTPPayments(view, filteredpaymentlistactive)
    }

    override fun onPaymentListError(errcode: String) {
        fragment.onTPPaymentsError(view, PaymentPresenter.ERRCODEPAYMENTS)
    }

    interface TPPayments {
        fun onTPPayments(
            inflatedView: View,
            list: ArrayList<Payment>
        )

        fun onTPPaymentsError(inflatedView: View, errcode: String)
    }
}
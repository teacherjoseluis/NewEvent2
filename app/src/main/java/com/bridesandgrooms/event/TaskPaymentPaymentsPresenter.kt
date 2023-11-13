package com.bridesandgrooms.event

import android.content.Context
import android.view.View
import com.bridesandgrooms.event.MVP.PaymentPresenter
import com.bridesandgrooms.event.Model.Payment
import kotlin.collections.ArrayList

class TaskPaymentPaymentsPresenter(
    val context: Context,
    val fragment: TaskPaymentPayments,
    val view: View,
    private val paymentcategory: String
) :
    PaymentPresenter.PaymentList {

    private var presenterpayment: PaymentPresenter = PaymentPresenter(context, this)

    init {
        presenterpayment.getPaymentsList()
    }

    override fun onPaymentList(list: ArrayList<Payment>) {
        val filteredpaymentlistactive = ArrayList<Payment>()
        for (payment in list) {
            if ((payment.category == paymentcategory) || (paymentcategory == "")) {
//                if (task.status == status) {
                    filteredpaymentlistactive.add(payment)
//                }
            }
        }
        fragment.onTPPayments(filteredpaymentlistactive)
    }

    override fun onPaymentListError(errcode: String) {
        fragment.onTPPaymentsError(PaymentPresenter.ERRCODEPAYMENTS)
    }

    interface TPPayments {
        fun onTPPayments(
            list: ArrayList<Payment>
        )

        fun onTPPaymentsError(errcode: String)
    }
}
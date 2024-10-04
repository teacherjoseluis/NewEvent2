package com.bridesandgrooms.event.Model

import android.content.Context
import android.os.Handler
import android.os.Looper
import com.bridesandgrooms.event.MVP.PaymentPresenter
import com.bridesandgrooms.event.TaskPaymentPayments
import kotlin.collections.ArrayList

class TaskPaymentPaymentsPresenter(
    val context: Context,
    val fragment: TaskPaymentPayments,
    private val paymentcategory: String
) :
    PaymentPresenter.PaymentList {

    private val mHandler = Handler(Looper.getMainLooper())
    private var presenterpayment: PaymentPresenter = PaymentPresenter(context, this)

    fun getPaymentList() {
        Thread {
            presenterpayment.getPaymentsList()
        }.start()
    }
    override fun onPaymentList(list: ArrayList<Payment>) {
        mHandler.post {
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
    }

    override fun onPaymentListError(errcode: String) {
        mHandler.post {
            fragment.onTPPaymentsError(PaymentPresenter.ERRCODEPAYMENTS)
        }
    }

    interface TPPayments {
        fun onTPPayments(
            list: ArrayList<Payment>
        )

        fun onTPPaymentsError(errcode: String)
    }
}
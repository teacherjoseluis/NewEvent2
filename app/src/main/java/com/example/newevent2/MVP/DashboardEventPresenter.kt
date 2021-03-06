package com.example.newevent2.MVP

import android.content.Context
import android.view.View
import com.example.newevent2.DashboardEvent
import com.example.newevent2.MVP.PaymentPresenter.Companion.ERRCODEPAYMENTS
import com.example.newevent2.MVP.TaskPresenter.Companion.ERRCODETASKS
import com.example.newevent2.Model.Guest
import com.example.newevent2.Model.GuestModel
import com.example.newevent2.Model.Payment
import com.example.newevent2.Model.Task
import java.util.*
import kotlin.Comparator
import kotlin.collections.ArrayList

class DashboardEventPresenter(val context: Context, val fragment: DashboardEvent, val view: View) :
    TaskPresenter.TaskList, PaymentPresenter.PaymentList, GuestPresenter.GuestList {

    private var presentertask: TaskPresenter = TaskPresenter(context!!, this)
    private var presenterpayment: PaymentPresenter = PaymentPresenter(context!!, this)
    private var presenterguest: GuestPresenter = GuestPresenter(context!!, this)

    private var paymentsumbudget = 0.0F

    init {
        presentertask.getTasksList()
    }

    override fun onTaskList(list: ArrayList<Task>) {
        var countactive = 0 // Active/Open Tasks
        var countcompleted = 0 // Tasks Completed
        var sumbudget = 0.0f // Budget Amount for all Tasks

        val re = Regex("[^A-Za-z0-9 ]")
        for (task in list) {
            val budgetamount = re.replace(task.budget, "").dropLast(2)
            sumbudget += budgetamount.toFloat()
            if (task.status == "A") {
                countactive += 1
            } else if (task.status == "C") {
                countcompleted += 1
            }
        }

        Collections.sort(list,
            Comparator { o1, o2 ->
                if (o1.date == null || o2.date == null) 0 else o1.date
                    .compareTo(o2.date)
            })

        fragment.onTasksStats(view, countactive, countcompleted, sumbudget, list[0])
        paymentsumbudget = sumbudget
        presenterpayment.getPaymentsList()
    }

    override fun onTaskListError(errcode: String) {
        fragment.onTaskStatsError(view, ERRCODETASKS)
        presenterpayment.getPaymentsList()
    }

    override fun onPaymentList(list: ArrayList<Payment>) {
        var countpayment = 0 // Number of Payments made
        var sumpayment = 0.0f // Amount of Payments

        val re = Regex("[^A-Za-z0-9 ]")
        for (payment in list) {

            val paidamount = re.replace(payment.amount, "").dropLast(2)
            sumpayment += paidamount.toFloat()
            countpayment += 1
        }
        fragment.onPaymentsStats(view, countpayment, sumpayment, paymentsumbudget)
        presenterguest.getGuestList()
    }

    override fun onPaymentListError(errcode: String) {
        fragment.onPaymentsStatsError(view, ERRCODEPAYMENTS)
        presenterguest.getGuestList()
    }

    override fun onGuestList(list: ArrayList<Guest>) {
        var confirmed = 0
        var rejected = 0
        var pending = 0

        for (guestitem in list) {
            when (guestitem.rsvp) {
                "y" -> confirmed += 1
                "n" -> rejected += 1
                "pending" -> pending += 1
            }
        }
        fragment.onGuestConfirmation(view, confirmed, rejected, pending)
    }

    override fun onGuestListError(errcode: String) {
        fragment.onGuestConfirmationError(view, GuestPresenter.ERRCODEGUESTS)
    }

    interface TaskStats {
        fun onTasksStats(
            inflatedView: View,
            taskpending: Int,
            taskcompleted: Int,
            sumbudget: Float,
            task: Task
        )

        fun onTaskStatsError(inflatedView: View, errcode: String)
    }

    interface PaymentStats {
        fun onPaymentsStats(
            inflatedView: View,
            countpayment: Int,
            sumpayment: Float,
            sumbudget: Float
        )

        fun onPaymentsStatsError(inflatedView: View, errcode: String)
    }

    interface GuestStats {
        fun onGuestConfirmation(
            inflatedView: View,
            confirmed: Int,
            rejected: Int,
            pending: Int
        )

        fun onGuestConfirmationError(
            inflatedView: View,
            errcode: String
        )
    }


}


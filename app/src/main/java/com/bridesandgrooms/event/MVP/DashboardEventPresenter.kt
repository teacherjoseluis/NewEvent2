package com.bridesandgrooms.event.MVP

import android.content.Context
import android.view.View
import com.bridesandgrooms.event.DashboardEvent
import com.bridesandgrooms.event.MVP.PaymentPresenter.Companion.ERRCODEPAYMENTS
import com.bridesandgrooms.event.MVP.TaskPresenter.Companion.ERRCODETASKS
import com.bridesandgrooms.event.Model.Event
import com.bridesandgrooms.event.Model.Guest
import com.bridesandgrooms.event.Model.Payment
import com.bridesandgrooms.event.Model.Task

class DashboardEventPresenter(val context: Context, val fragment: DashboardEvent, val view: View) :
    TaskPresenter.TaskList, PaymentPresenter.PaymentList, GuestPresenter.GuestList,
    EventPresenter.EventItem {

    private var presentertask: TaskPresenter = TaskPresenter(context, this)
    private var presenterpayment: PaymentPresenter = PaymentPresenter(context, this)
    private var presenterguest: GuestPresenter = GuestPresenter(context, this)
    private var presenterevent: EventPresenter = EventPresenter(context, this)


    private var paymentsumbudget = 0.0F

//    fun getEventchildrenflag(): Boolean {
//        //This function needs to return a boolean
//        val eventdbhelper = EventDBHelper(context)
//        val event = eventdbhelper.getEvent()
//        return presenterevent.getEventChildrenflag(event.key)
//    }

    fun getTaskList(){
        presentertask.getTasksList()
    }

    fun getPaymentList(){
        presenterpayment.getPaymentsList()
    }

    fun getEvent(){
        presenterevent.getEventDetail()
    }

//    fun getEventChildrenflag(){
//        presenterevent.getEventDetail()
//    }

    fun getGuestList(){
        presenterguest.getGuestList()
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
        paymentsumbudget = sumbudget

        list.sortWith(Comparator { o1, o2 ->
            if (o1.date == null || o2.date == null) 0 else o1.date
                .compareTo(o2.date)
        })
        val nextactivetask = nextactive(list)
        fragment.onTasksStats(countactive, countcompleted, sumbudget, nextactivetask)
    }

    private fun nextactive(list: ArrayList<Task>): Task {
        var nexttask = Task()
        for (task in list) {
            if (task.status == "A") {
                nexttask = task
                break
            }
        }
        return nexttask
    }

    override fun onTaskListError(errcode: String) {
        fragment.onTaskStatsError(ERRCODETASKS)
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
        fragment.onPaymentsStats(countpayment, sumpayment, paymentsumbudget)
    }

    override fun onPaymentListError(errcode: String) {
        fragment.onPaymentsStatsError(ERRCODEPAYMENTS)
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
        fragment.onGuestConfirmation(confirmed, rejected, pending)
        presenterevent.getEventDetail()
    }

    override fun onGuestListError(errcode: String) {
        fragment.onGuestConfirmationError(GuestPresenter.ERRCODEGUESTS)
        presenterevent.getEventDetail()
    }

    override fun onEvent(event: Event) {
        fragment.onEvent(context, event)
    }

    override fun onEventError(errcode: String) {
        fragment.onEventError(view, EventPresenter.ERRCODEEVENTS)
    }

    interface TaskStats {
        fun onTasksStats(
            taskpending: Int,
            taskcompleted: Int,
            sumbudget: Float,
            task: Task
        )

        fun onTaskStatsError(errcode: String)
    }

    interface PaymentStats {
        fun onPaymentsStats(
            countpayment: Int,
            sumpayment: Float,
            sumbudget: Float
        )

        fun onPaymentsStatsError(errcode: String)
    }

    interface GuestStats {
        fun onGuestConfirmation(
            confirmed: Int,
            rejected: Int,
            pending: Int
        )

        fun onGuestConfirmationError(
            errcode: String
        )
    }

    interface EventInterface {
        fun onEvent(context: Context, event: Event)
        fun onEventError(inflatedview: View, errorcode: String)
    }
}


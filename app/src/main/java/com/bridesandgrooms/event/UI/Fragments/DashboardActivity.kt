package com.bridesandgrooms.event.UI.Fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.applandeo.materialcalendarview.CalendarDay
import com.applandeo.materialcalendarview.listeners.OnCalendarDayClickListener
import com.applandeo.materialcalendarview.listeners.OnCalendarPageChangeListener
import com.bridesandgrooms.event.MVP.DashboardActivityPresenter
import com.bridesandgrooms.event.R
import com.bridesandgrooms.event.UI.Activities.ExportPDF
import com.bridesandgrooms.event.databinding.DashboardactivityBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class DashboardActivity : Fragment(), DashboardActivityPresenter.TaskCalendarInterface,
    DashboardActivityPresenter.PaymentCalendarInterface {

    private lateinit var dashboardAP: DashboardActivityPresenter
    private lateinit var inf: DashboardactivityBinding
    private lateinit var activitymenu: Menu

    private lateinit var dateFormat: SimpleDateFormat
    private var events = mutableListOf<CalendarDay>()

    private lateinit var taskpaymentDate: Date

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        dateFormat = SimpleDateFormat("d/M/yyyy", Locale.getDefault())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        inf = DataBindingUtil.inflate(inflater, R.layout.dashboardactivity, container, false)
        val currentDateTime = Calendar.getInstance()
        var month =
            currentDateTime.get(Calendar.MONTH) // Calendar.MONTH returns zero-based month (0 for January)
        val year = currentDateTime.get(Calendar.YEAR)

        inf.calendarView.setDate(currentDateTime)

        try {
            dashboardAP = DashboardActivityPresenter(requireContext(), this)
            dashboardAP.getTasksFromMonthYear(month, year)
            dashboardAP.getPaymentsFromMonthYear(month, year)
            inf.calendarView.setCalendarDays(events)
        } catch (e: Exception) {
            Log.e(TAG, e.message.toString())
        }

        inf.calendarView.setOnPreviousPageChangeListener(object : OnCalendarPageChangeListener {
            override fun onChange() {
                month -= 1
                dashboardAP.getTasksFromMonthYear(month, year)
                dashboardAP.getPaymentsFromMonthYear(month, year)
            }
        })

        inf.calendarView.setOnForwardPageChangeListener(object : OnCalendarPageChangeListener {
            override fun onChange() {
                month += 1
                dashboardAP.getTasksFromMonthYear(month, year)
                dashboardAP.getPaymentsFromMonthYear(month, year)
            }
        })

        inf.calendarView.setOnCalendarDayClickListener(object : OnCalendarDayClickListener {
            override fun onClick(calendarDay: CalendarDay) {
                taskpaymentDate = calendarDay.calendar.time

                inf.taskItemCalendar.root.visibility = View.GONE
                inf.paymentItemCalendar.root.visibility = View.GONE

                dashboardAP.getTaskFromDate(calendarDay.calendar.time)
                dashboardAP.getPaymentFromDate(calendarDay.calendar.time)
            }
        })

        inf.taskItemCalendar.root.setOnClickListener {
            val fragment = TasksAllCalendar()
            val bundle = Bundle()
            bundle.putLong("taskDate", taskpaymentDate.time)  // Store Date as milliseconds
            fragment.arguments = bundle
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit()
        }

        inf.paymentItemCalendar.root.setOnClickListener {
            val fragment = PaymentsAllCalendar()
            val bundle = Bundle()
            bundle.putLong("paymentDate", taskpaymentDate.time)  // Store Date as milliseconds
            fragment.arguments = bundle
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit()
        }

        return inf.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.tasklist_menu, menu)
        activitymenu = menu
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.export_task -> {
                val exportpdf = Intent(context, ExportPDF::class.java)
                startActivity(exportpdf)
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }

    override fun onTaskCalendar(list: List<Date>?) {
        if (list != null) {
            if (list.isNotEmpty()) {
                for (date in list) {
                    val calendar = Calendar.getInstance()
                    calendar.time = date
                    calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH))
                    calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH))

                    val calendarDay = CalendarDay(calendar)
                    calendarDay.imageResource = R.drawable.icons8_task_completed_24
                    events.add(calendarDay)
                }
            }
        }
    }

    override fun onTaskCalendar(list: ArrayList<String>?, date: Date) {
        if (list != null) {
            if (list.isNotEmpty()) {
                inf.taskItemCalendar.root.visibility = View.VISIBLE
                val numberOfTasks = minOf(list.size, 3)
                for (i in 1..numberOfTasks) {
                    val taskName = "taskname$i"
                    val taskViewField =
                        inf.taskItemCalendar::class.java.getDeclaredField(taskName).apply {
                            isAccessible = true
                        }
                    val taskView = taskViewField.get(inf.taskItemCalendar) as TextView
                    taskView.visibility = View.VISIBLE
                    taskView.text = list[i - 1]
                }
            }
        }
    }

    override fun onTaskCalendarError(errcode: String) {
    }

    override fun onPaymentCalendar(list: List<Date>?) {
        if (list != null) {
            if (list.isNotEmpty()) {
                for (date in list) {
                    val calendar = Calendar.getInstance()
                    calendar.time = date
                    calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH))
                    calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH))

                    val calendarDay = CalendarDay(calendar)
                    calendarDay.imageResource = R.drawable.icons8_invoice_paid_32
                    events.add(calendarDay)
                }
            }
        }
    }

    override fun onPaymentCalendar(list: ArrayList<String>?, date: Date) {
        if (list != null) {
            if (list.isNotEmpty()) {
                inf.paymentItemCalendar.root.visibility = View.VISIBLE
                val numberOfPayments = minOf(list.size, 3)
                for (i in 1..numberOfPayments) {
                    val paymentName = "taskname$i"
                    val paymentViewField =
                        inf.paymentItemCalendar::class.java.getDeclaredField(paymentName).apply {
                            isAccessible = true
                        }
                    val paymentView = paymentViewField.get(inf.paymentItemCalendar) as TextView
                    paymentView.visibility = View.VISIBLE
                    paymentView.text = list[i - 1]
                }
            }
        }
    }

    override fun onPaymentCalendarError(errcode: String) {
    }

    companion object {
        const val TAG = "DashboardActivity"
    }
}


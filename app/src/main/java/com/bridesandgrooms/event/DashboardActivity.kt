package com.bridesandgrooms.event


import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.applandeo.materialcalendarview.CalendarDay
import com.applandeo.materialcalendarview.listeners.OnCalendarPageChangeListener
import com.bridesandgrooms.event.MVP.DashboardActivityPresenter
import com.bridesandgrooms.event.UI.Activities.ExportPDF
import com.bridesandgrooms.event.UI.Components.DrawableUtils
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

//        recyclerViewActivity = inf.journalparentrv
//        recyclerViewActivity.apply {
//            layoutManager = LinearLayoutManager(inf.root.context).apply {
//                stackFromEnd = true
//                reverseLayout = true
//                isNestedScrollingEnabled = false
//            }
//        }
//        inf.NewTaskActionButton.setOnClickListener {
//            val newtask = Intent(activity, TaskCreateEdit::class.java)
//            newtask.putExtra("userid", "")
////            newtask.putExtra("eventid", userSession.eventid)
//            startActivityForResult(newtask, REQUEST_CODE_TASK)
//        }

//        val events: MutableList<CalendarDay> = ArrayList()
//
//        val calendar = Calendar.getInstance()
//        calendar.add(Calendar.DAY_OF_MONTH, 13)
//        val calendarDay = CalendarDay(calendar)
//        calendarDay.imageResource=R.drawable.sample_icon_2
//        events.add(calendarDay)
//        inf.calendarView.setCalendarDays(events)


//        try {
            dashboardAP = DashboardActivityPresenter(requireContext(), this)
            dashboardAP.getTasksFromMonthYear(month, year)
            dashboardAP.getPaymentsFromMonthYear(month, year)
        inf.calendarView.setCalendarDays(events)
//        } catch (e: Exception) {
//            Log.e(TAG, e.message.toString())
//        }
//
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
            for (date in list) {
                val calendar = Calendar.getInstance()
                calendar.time = date
                calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH))
                calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH))
                //calendar.add(Calendar.DAY_OF_MONTH, 7)

                val calendarDay = CalendarDay(calendar)
                calendarDay.imageResource = R.drawable.icons8_task_completed_24
                events.add(calendarDay)
            }
            //inf.calendarView.setCalendarDays(events)
        }
    }

    override fun onTaskCalendarError(errcode: String) {
    }

    override fun onPaymentCalendar(list: List<Date>?) {
        if (list != null) {
            for (date in list) {
                val calendar = Calendar.getInstance()
                calendar.time = date
                calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH))
                calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH))
                //calendar.add(Calendar.DAY_OF_MONTH, 7)

                val calendarDay = CalendarDay(calendar)
                calendarDay.imageResource = R.drawable.icons8_invoice_paid_32
                events.add(calendarDay)
            }
            //inf.calendarView.setCalendarDays(events)
        }
    }

    override fun onPaymentCalendarError(errcode: String) {
    }

    companion object {
        const val TAG = "DashboardActivity"
    }
}



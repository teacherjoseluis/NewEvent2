package com.example.newevent2

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.applandeo.materialcalendarview.CalendarView
import com.applandeo.materialcalendarview.EventDay
import com.applandeo.materialcalendarview.listeners.OnCalendarPageChangeListener
import com.applandeo.materialcalendarview.listeners.OnDayClickListener
import kotlinx.android.synthetic.main.calendar.*
import kotlinx.android.synthetic.main.guests_all.view.*
import kotlinx.android.synthetic.main.navbottom.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

internal class MyCalendar : AppCompatActivity() {
    private var mCalendarView: CalendarView? = null
    private var currentCalendar = Calendar.getInstance()
    private var eventkey = ""
    private val events: MutableList<EventDay?> = ArrayList()

    lateinit var recyclerViewTaskCalendar: RecyclerView
    lateinit var recyclerViewPaymentCalendar: RecyclerView

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.calendar)

        eventkey = intent.getStringExtra("eventkey").toString()

        recyclerViewTaskCalendar = RecyclerViewTaskCalendar
        recyclerViewTaskCalendar.apply {
            layoutManager = LinearLayoutManager(context).apply {
                stackFromEnd = true
                reverseLayout = true
            }
        }

//        recyclerViewPaymentCalendar = RecyclerViewPaymentCalendar
//        recyclerViewPaymentCalendar.apply {
//            layoutManager = LinearLayoutManager(context).apply {
//                stackFromEnd = true
//                reverseLayout = true
//            }
//        }

        mCalendarView = findViewById<CalendarView>(R.id.calendarView) as CalendarView
        loadcalendar(currentCalendar)
// Load all dates for Active Tasks and Payments for all of the existing Events in the active month


        // Mark the days of the month in the calendar for those dates

//        val calendar = Calendar.getInstance()
//        val drawable = resources.getDrawable(R.drawable.icons8_sphere_24)
//        drawable.setTint(Color.BLUE)
//
//        events.add(EventDay(calendar, R.drawable.three_dots_more_indicator_primarycolor))
//
//        val calendar1 = GregorianCalendar(2020, 10, 28)
//        events.add(EventDay(calendar1, R.drawable.ic_icons8_clock))

//        mCalendarView = findViewById<CalendarView>(R.id.calendarView) as CalendarView
//        mCalendarView!!.setEvents(events)

        // This is not working
//        mCalendarView!!.setHighlightedDays(calendars)

//        val floatingActionButton =
//            findViewById<View>(R.id.floatingActionButton) as FloatingActionButton
//        floatingActionButton.setOnClickListener {
////            addNote()
//        }

        mCalendarView!!.setOnDayClickListener(object : OnDayClickListener {
            override fun onDayClick(eventDay: EventDay) {
//                Load the recyclerview with the details for the selected day
                if (events.contains(eventDay)) {
                    TaskPaymentLabel.text = SimpleDateFormat("dd/MM/yyyy").format(eventDay.calendar.time)
                    val taskentity = TaskEntity()
                    taskentity.eventid = eventkey
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                        taskentity.getTasksperDay(
                            eventDay.calendar,
                            object : FirebaseSuccessListenerTaskCalendar {
                                override fun onTasksDatesEvent(list: ArrayList<Date>) {
                                    TODO("Not yet implemented")
                                }

                                override fun onTasksperDay(list: ArrayList<Task>) {
                                    val rvAdapter = Rv_TaskCalendarAdapter(list)
                                    recyclerViewTaskCalendar.adapter = rvAdapter
                                }
                            })
                    }
                }
                else {
                    TaskPaymentLabel.text = "There are no tasks for the selected date"
                    recyclerViewTaskCalendar.adapter = null
                }
            }
        })

        mCalendarView!!.setOnPreviousPageChangeListener(object : OnCalendarPageChangeListener {
            override fun onChange() {
                // Execute the same process as in the creation of the activity just for the month - 1
                currentCalendar.add(Calendar.MONTH, -1)
                loadcalendar(currentCalendar)
            }
        })

        mCalendarView!!.setOnForwardPageChangeListener(object : OnCalendarPageChangeListener {
            override fun onChange() {
                // Execute the same process as in the creation of the activity just for the month + 1
                currentCalendar.add(Calendar.MONTH, +1)
                loadcalendar(currentCalendar)
            }
        })

//        imageButton1.setOnClickListener {
//            val home = Intent(this, Welcome::class.java)
//            startActivity(home)
//        }
//        imageButton.setOnClickListener {
//            val events = Intent(this, EventDetail::class.java)
//            //calendar.putExtra("eventkey", eventkey)
//            startActivity(events)
//        }
//
//        imageButton3.setOnClickListener {
//            val contacts = Intent(this, MyContacts::class.java)
//            //contacts.putExtra("eventkey", eventkey)
//            startActivity(contacts)
//        }
//
//        imageButton4.setOnClickListener {
//            val notes = Intent(this, MyNotes::class.java)
//            //notes.putExtra("eventkey", eventkey)
//            startActivity(notes)
//        }
    }

    private fun loadcalendar(calendar: Calendar?) {
        val taskentity = TaskEntity()
        taskentity.eventid = eventkey
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            taskentity.getTasksDatesEvent(
                calendar!!.get(Calendar.MONTH),
                object : FirebaseSuccessListenerTaskCalendar {
                    override fun onTasksDatesEvent(list: ArrayList<Date>) {
                        for (date in list) {
                            val calendar = Calendar.getInstance()
                            calendar.time = date
                            events.add(EventDay(calendar, R.drawable.three_dots_more_indicator_primarycolor))
                        }
                        mCalendarView!!.setEvents(events)
                    }

                    override fun onTasksperDay(list: ArrayList<Task>) {
                        TODO("Not yet implemented")
                    }
                })
        }
//        val paymententity = PaymentEntity()
//        paymententity.eventid = eventkey
//        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
//            paymententity.getPaymentsDatesEvent(
//                calendar!!.get(Calendar.MONTH),
//                object : FirebaseSuccessListenerPaymentCalendar {
//                    override fun onPaymentsDatesEvent(list: ArrayList<Date>) {
//                        for (date in list) {
//                            val calendar = Calendar.getInstance()
//                            calendar.time = date
//                            events.add(EventDay(calendar, R.drawable.three_dots_more_indicator_graycolor))
//                        }
//                        //mCalendarView!!.setEvents(eventspayment)
//                    }
//
//                    override fun onPaymentsperDay(list: ArrayList<Payment>) {
//                        TODO("Not yet implemented")
//                    }
//                })
//        }
    }

//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//
//        if (requestCode == ADD_NOTE && resultCode == RESULT_OK) {
//            val myEventDay:MyEventDay? = data!!.getParcelableExtra(RESULT)
//            if (myEventDay != null) {
//                mCalendarView!!.setDate(myEventDay.calendar)
//            }
//            mEventDays.add(myEventDay)
//            mCalendarView!!.setEvents(mEventDays)
//        }
//    }
//
//    private fun addNote() {
//        val intent = Intent(this, AddNoteActivity::class.java)
//        startActivityForResult(intent, ADD_NOTE)
//    }
//
//    private fun previewNote(eventDay: EventDay) {
//        val intent = Intent(this, NotePreviewActivity::class.java)
//        if (eventDay is MyEventDay) {
//            intent.putExtra(EVENT, eventDay)
//        }
//        startActivity(intent)
//    }
//
//    companion object {
//        const val RESULT = "result"
//        const val EVENT = "event"
//        private const val ADD_NOTE = 44
//    }

}




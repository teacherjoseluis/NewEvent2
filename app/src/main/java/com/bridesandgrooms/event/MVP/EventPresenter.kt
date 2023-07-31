package com.bridesandgrooms.event.MVP

import Application.Cache
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import com.bridesandgrooms.event.Functions.userdbhelper
import com.bridesandgrooms.event.MainActivity
import com.bridesandgrooms.event.Model.Event
import com.bridesandgrooms.event.Model.EventModel

class EventPresenter : Cache.EventItemCacheData {

    private var activefragment = ""
    private var mContext: Context

    //private lateinit var fragmentES: EventSummaryPresenter
    private lateinit var fragmentMA: MainActivity
    private lateinit var fragmentDE: DashboardEventPresenter

    private lateinit var cacheevent: Cache<Event>

//    constructor(context: Context, fragment: EventSummaryPresenter) {
//        fragmentES = fragment
//        mContext = context
//        activefragment = "ES"
//    }

    constructor(context: Context, fragment: MainActivity) {
        fragmentMA = fragment
        mContext = context
        activefragment = "MA"
    }

    constructor(context: Context, fragment: DashboardEventPresenter) {
        fragmentDE = fragment
        mContext = context
        activefragment = "DE"
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getEventDetail() {
        cacheevent = Cache(mContext, this)
        cacheevent.loadarraylist(Event::class)
    }

//    fun getEventChildrenflag(eventkey: String): Boolean {
//        var eventchilderenflag = false
//        val eventdbhelper = EventDBHelper(mContext)
//        if (eventdbhelper.getEventChildrenflag(eventkey)) {
//            eventchilderenflag = true
//        }
//        return eventchilderenflag
//    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onEvent(item: Event) {
        when (activefragment) {
            //"ES" -> fragmentES.onEvent(item)
            "MA" -> fragmentMA.onEvent(item)
            "DE" -> fragmentDE.onEvent(item)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onEventError() {
        val user = userdbhelper.getUser(userdbhelper.getUserKey())
        val event = EventModel()
        event.getEventdetail(
            user.userid!!,
            user.eventid,
            object : EventModel.FirebaseSuccessListenerEventDetail {
                @RequiresApi(Build.VERSION_CODES.O)
                override fun onEvent(event: Event) {
                    cacheevent.save(event)
                    when (activefragment) {
                        //"ES" -> fragmentES.onEvent(event)
                        "MA" -> fragmentMA.onEvent(event)
                        "DE" -> fragmentDE.onEvent(event)
                    }
                }
            }
        )

        when (activefragment) {
            //"ES" -> fragmentES.onEventError(ERRCODEEVENTS)
            "MA" -> fragmentMA.onEventError(ERRCODEEVENTS)
            "DE" -> fragmentDE.onEventError(ERRCODEEVENTS)
        }
    }

//        val event = EventModel()
//        event.getEventdetail(
//            userid,
//            eventid,
//            object : EventModel.FirebaseSuccessListenerEventDetail {
//                @RequiresApi(Build.VERSION_CODES.O)
//                override fun onEvent(event: Event) {
//                    if (event.key != "") {
//                        fragmentEventSummary.onViewEventSuccessFragment(mContext, inflatedView, event)
//                    } else {
//                        fragmentEventSummary.onViewEventErrorFragment(inflatedView, "BLANK_EVENT")
//                    }
//                }
//            })
//    }

    //    interface ViewEventActivity {
//        fun onViewEventSuccessFragment(context: Context, inflatedview: View, event: Event)
//        fun onViewEventErrorFragment(inflatedview: View, errorcode: String)
//    }
    interface EventItem {
        fun onEvent(event: Event)
        fun onEventError(errcode: String)
    }

    companion object {
        const val ERRCODEEVENTS = "NOEVENTS"
    }


}
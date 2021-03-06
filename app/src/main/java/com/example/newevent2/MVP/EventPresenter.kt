package com.example.newevent2.MVP

import Application.Cache
import android.content.Context
import android.os.Build
import android.view.View
import androidx.annotation.RequiresApi
import com.example.newevent2.EventSummary
import com.example.newevent2.MainActivity
import com.example.newevent2.MainEventView_clone

import com.example.newevent2.Model.Event
import com.example.newevent2.Model.EventModel
import com.example.newevent2.Model.Guest

class EventPresenter : Cache.EventItemCacheData {

    private var activefragment = ""
    private var mContext: Context

    private lateinit var fragmentES: EventSummaryPresenter
    private lateinit var fragmentMA: MainActivity

    private lateinit var cacheevent: Cache<Event>

    constructor(context: Context, fragment: EventSummaryPresenter) {
        fragmentES = fragment
        mContext = context
        activefragment = "ES"
    }

    constructor(context: Context, fragment: MainActivity) {
        fragmentMA = fragment
        mContext = context
        activefragment = "MA"
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getEventDetail() {
        cacheevent = Cache(mContext, this)
        cacheevent.loadarraylist(Event::class)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onEvent(item: Event) {
        when (activefragment) {
            "ES" -> fragmentES.onEvent(item)
            "MA" -> fragmentMA.onEvent(item)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onEventError() {
        val user = com.example.newevent2.Functions.getUserSession(mContext!!)
        val event = EventModel()
        event.getEventdetail(
            user.key,
            user.eventid,
            object : EventModel.FirebaseSuccessListenerEventDetail {
                @RequiresApi(Build.VERSION_CODES.O)
                override fun onEvent(event: Event) {
                    cacheevent.save(event)
                    when (activefragment) {
                        "ES" -> fragmentES.onEvent(event)
                        "MA" -> fragmentMA.onEvent(event)
                    }
                }
            }
        )

        when (activefragment) {
            "ES" -> fragmentES.onEventError(ERRCODEEVENTS)
            "MA" -> fragmentMA.onEventError(ERRCODEEVENTS)
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
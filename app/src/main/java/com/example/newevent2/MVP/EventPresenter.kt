package com.example.newevent2.MVP

import android.content.Context
import android.os.Build
import android.view.View
import androidx.annotation.RequiresApi
import com.example.newevent2.EventSummary
import com.example.newevent2.MainEventView_clone

import com.example.newevent2.Model.Event
import com.example.newevent2.Model.EventModel

class EventPresenter {
    var userid = ""
    var eventid = ""
    lateinit var inflatedView: View
    lateinit var fragmentEventSummary: EventSummary
    lateinit var viewMainEvent: MainEventView_clone
    lateinit var mContext: Context

//    constructor(view: MainEventView, userid: String, eventid: String) {
//        this.userid = userid
//        this.eventid = eventid
//        viewMainEvent = view
//    }

    constructor(context: Context, fragment: EventSummary, view: View, userid: String, eventid: String) {
        this.userid = userid
        this.eventid = eventid
        mContext = context
        fragmentEventSummary = fragment
        inflatedView = view
    }

    fun getEventDetail() {
        val event = EventModel()
        event.getEventdetail(
            userid,
            eventid,
            object : EventModel.FirebaseSuccessListenerEventDetail {
                @RequiresApi(Build.VERSION_CODES.O)
                override fun onEvent(event: Event) {
                    if (event.key != "") {
                        fragmentEventSummary.onViewEventSuccessFragment(mContext, inflatedView, event)
                    } else {
                        fragmentEventSummary.onViewEventErrorFragment(inflatedView, "BLANK_EVENT")
                    }
                }
            })
    }

    interface ViewEventActivity {
        fun onViewEventSuccessFragment(context: Context, inflatedview: View, event: Event)
        fun onViewEventErrorFragment(inflatedview: View, errorcode: String)
    }
}
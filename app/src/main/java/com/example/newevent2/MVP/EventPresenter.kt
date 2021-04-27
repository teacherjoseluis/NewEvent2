package com.example.newevent2.MVP

import android.view.View
import com.example.newevent2.Model.Task
import com.example.newevent2.Model.TaskModel
import com.example.newevent2.DashboardView
import com.example.newevent2.MainEventDetail
import com.example.newevent2.MainEventSummary
import com.example.newevent2.MainEventView
import com.example.newevent2.Model.Event
import com.example.newevent2.Model.EventModel

class EventPresenter {
    var userid = ""
    var eventid = ""
    lateinit var inflatedView: View
    lateinit var fragmentEventDetail: MainEventDetail
    lateinit var viewMainEvent: MainEventView

//    constructor(view: MainEventView, userid: String, eventid: String) {
//        this.userid = userid
//        this.eventid = eventid
//        viewMainEvent = view
//    }

    constructor(fragment: MainEventDetail, view: View, userid: String, eventid: String) {
        this.userid = userid
        this.eventid = eventid
        fragmentEventDetail = fragment
        inflatedView = view
    }

    fun getEventDetail() {
        val event = EventModel()
        event.getEventdetail(
            userid,
            eventid,
            object : EventModel.FirebaseSuccessListenerEventDetail {
                override fun onEvent(event: Event) {
                    if (event.key != "") {
                        fragmentEventDetail.onViewEventSuccessFragment(inflatedView, event)
                    } else {
                        fragmentEventDetail.onViewEventErrorFragment(inflatedView, "BLANK_EVENT")
                    }
                }
            })
    }

    interface ViewEventActivity {
        fun onViewEventSuccessFragment(inflatedview: View, event: Event)
        fun onViewEventErrorFragment(inflatedview: View, errorcode: String)
    }
}
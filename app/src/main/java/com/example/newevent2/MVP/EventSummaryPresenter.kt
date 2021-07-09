package com.example.newevent2.MVP

import android.content.Context
import android.os.Build
import android.view.View
import androidx.annotation.RequiresApi
import com.example.newevent2.DashboardEvent
import com.example.newevent2.EventSummary
import com.example.newevent2.MVP.EventPresenter.Companion.ERRCODEEVENTS
import com.example.newevent2.MVP.GuestPresenter.Companion.ERRCODEGUESTS
import com.example.newevent2.Model.Event
import com.example.newevent2.Model.Guest
import com.example.newevent2.Model.Payment
import com.example.newevent2.Model.Task
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.ValueEventListener
import java.util.*
import kotlin.Comparator
import kotlin.collections.ArrayList

@RequiresApi(Build.VERSION_CODES.O)
class EventSummaryPresenter(val context: Context, val fragment: EventSummary, val view: View) :
     EventPresenter.EventItem {


    private var presenterevent: EventPresenter = EventPresenter(context!!, this)

    init {
        presenterevent.getEventDetail()
    }

//    override fun onGuestList(list: ArrayList<Guest>) {
//        var confirmed = 0
//        var rejected = 0
//        var pending = 0
//
//        for (guestitem in list) {
//            when (guestitem.rsvp) {
//                "y" -> confirmed += 1
//                "n" -> rejected += 1
//                "pending" -> pending += 1
//            }
//        }
//        fragment.onGuestConfirmation(view, confirmed, rejected, pending)
//    }

//    override fun onGuestListError(errcode: String) {
//        fragment.onGuestConfirmationError(view, ERRCODEGUESTS)
//    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onEvent(event: Event) {
        fragment.onEvent(context, view, event)
        //presenterguest.getGuestList()
    }

    override fun onEventError(errcode: String) {
        fragment.onEventError(view, ERRCODEEVENTS)
        //presenterguest.getGuestList()
    }

    interface EventInterface {
        fun onEvent(context:Context, inflatedview: View, event: Event)
        fun onEventError(inflatedview: View, errorcode: String)
    }

}




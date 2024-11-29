package com.bridesandgrooms.event.MVP

import Application.Cache
import android.content.Context
import com.bridesandgrooms.event.UI.Fragments.MainActivity
import com.bridesandgrooms.event.Model.Event
import com.bridesandgrooms.event.Model.EventModel
import com.bridesandgrooms.event.Model.User

class EventPresenter : Cache.EventItemCacheData {

    private var activefragment = ""
    private var mContext: Context

    private lateinit var fragmentMA: MainActivity
    private lateinit var fragmentDE: DashboardEventPresenter

    private lateinit var cacheevent: Cache<Event>

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

    fun getEventDetail() {
        cacheevent = Cache(mContext, this)
        cacheevent.loadarraylist(Event::class)
    }

    override fun onEvent(item: Event) {
        when (activefragment) {
            //"ES" -> fragmentES.onEvent(item)
            "MA" -> fragmentMA.onEvent(item)
            "DE" -> fragmentDE.onEvent(item)
        }
    }

    override fun onEventError() {
        //val userdbhelper = UserDBHelper(mContext)
        //val user = User().getUser()
        val event = EventModel()
        event.getEventdetail(
            object : EventModel.FirebaseSuccessListenerEventDetail {
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

    interface EventItem {
        fun onEvent(event: Event)
        fun onEventError(errcode: String)
    }

    companion object {
        const val ERRCODEEVENTS = "NOEVENTS"
    }
}
package com.example.newevent2.MVP

import android.content.Context;
import android.view.View;

import com.example.newevent2.EventSummary;
import com.example.newevent2.Model.GuestModel

class GuestPresenter {

    private var inflatedView: View
    private var mContext: Context
    private var fragmentMED: EventSummary

    var userid = ""
    var eventid = ""

    constructor(context: Context, fragment: EventSummary, view: View) {
        fragmentMED = fragment
        mContext = context
        inflatedView = view
    }

    fun getGuestsEvent() {
        val guest = GuestModel()
        guest.getGuestsEvent(userid, eventid, object : GuestModel.FirebaseSuccessGuestStats {
            override fun onGuestConfirmation(confirmed: Int, rejected: Int, pending: Int) {
                fragmentMED.onGuestConfirmation(inflatedView, confirmed, rejected, pending)
            }
        })
    }

    interface GuestStats{
        fun onGuestConfirmation(inflatedView: View, confirmed: Int, rejected: Int, pending: Int)
    }
}

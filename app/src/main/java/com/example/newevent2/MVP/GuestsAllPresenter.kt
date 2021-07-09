package com.example.newevent2.MVP

import android.content.Context
import android.view.View
import com.example.newevent2.GuestsAll
import com.example.newevent2.Model.Guest
import kotlin.collections.ArrayList

class GuestsAllPresenter(
    val context: Context,
    val fragment: GuestsAll,
    val view: View
) :
    GuestPresenter.GuestList {

    private var presenterguest: GuestPresenter = GuestPresenter(context!!, this)

    init {
        presenterguest.getGuestList()
    }

    override fun onGuestList(list: ArrayList<Guest>) {
        fragment.onGAGuests(view, list)
    }

    override fun onGuestListError(errcode: String) {
        fragment.onGAGuestsError(view, GuestPresenter.ERRCODEGUESTS)
    }

    interface GAGuests {
        fun onGAGuests(
            inflatedView: View,
            list: ArrayList<Guest>
        )

        fun onGAGuestsError(inflatedView: View, errcode: String)
    }
}
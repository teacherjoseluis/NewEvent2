package com.example.newevent2.MVP

import android.content.Context
import android.view.View
import com.example.newevent2.GuestsAll
import com.example.newevent2.Model.Guest

class GuestsAllPresenter(
    val context: Context,
    val fragment: GuestsAll
) :
    GuestPresenter.GuestList {

    private var presenterguest: GuestPresenter = GuestPresenter(context, this)

    init {
        presenterguest.getGuestList()
    }

    override fun onGuestList(list: ArrayList<Guest>) {
       fragment.onGAGuests(list)
    }

    override fun onGuestListError(errcode: String) {
        fragment.onGAGuestsError(GuestPresenter.ERRCODEGUESTS)
    }

    interface GAGuests {
        fun onGAGuests(
            list: ArrayList<Guest>
        )

        fun onGAGuestsError(errcode: String)
    }
}
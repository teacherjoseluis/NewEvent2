package com.bridesandgrooms.event.MVP

import android.content.Context
import com.bridesandgrooms.event.GuestsAll
import com.bridesandgrooms.event.Model.Guest

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
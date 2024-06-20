package com.bridesandgrooms.event.MVP

import android.content.Context
import android.os.Handler
import android.os.Looper
import com.bridesandgrooms.event.UI.Fragments.GuestsAll
import com.bridesandgrooms.event.Model.Guest

class GuestsAllPresenter(
    val context: Context,
    val fragment: GuestsAll
) :
    GuestPresenter.GuestList {

    private var presenterguest: GuestPresenter = GuestPresenter(context, this)
    private val mHandler = Handler(Looper.getMainLooper())

    fun getGuestList() {
        Thread {
            presenterguest.getGuestList()
        }.start()
    }

    override fun onGuestList(list: ArrayList<Guest>) {
        mHandler.post {
            fragment.onGAGuests(list)
        }
    }

    override fun onGuestListError(errcode: String) {
        mHandler.post {
            fragment.onGAGuestsError(GuestPresenter.ERRCODEGUESTS)
        }
    }

    interface GAGuests {
        fun onGAGuests(
            list: ArrayList<Guest>
        )

        fun onGAGuestsError(errcode: String)
    }
}
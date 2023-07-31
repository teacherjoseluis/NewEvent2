package com.bridesandgrooms.event.MVP

import android.content.Context
import com.bridesandgrooms.event.Model.Guest
import com.bridesandgrooms.event.Model.TableGuests
import com.bridesandgrooms.event.TableGuestsActivity

class TableGuestsActivityPresenter(
    val context: Context,
    val fragment: TableGuestsActivity
) :
    GuestPresenter.GuestList {

    private var presenterguest: GuestPresenter = GuestPresenter(context, this)

    init {
        presenterguest.getGuestList()
    }

    override fun onGuestList(list: ArrayList<Guest>) {
        val tablelist: ArrayList<String> = ArrayList()
        for (guest in list) {
            if (!tablelist.contains(guest.table)) {
                tablelist.add(guest.table)
            }
        }

        val guesttables: ArrayList<TableGuests> = ArrayList()
        val guesttableslist: ArrayList<Guest> = ArrayList()
        for (tables in tablelist) {
            guesttableslist.clear()
            var count = 0
            for (guest in list) {
                //if (guest.table == tables && guest.rsvp != "n"){
                if (guest.table == tables) {
                    count += when (guest.companion) {
                        "none" -> 1
                        else -> 2
                    }
                    guesttableslist.add(guest)
                }
            }
            val newguestlist: ArrayList<Guest> = ArrayList(guesttableslist.size)
            for (guestlist in guesttableslist) newguestlist.add(guestlist)
            guesttables.add(TableGuests(tables, count, newguestlist))

        }
        fragment.onTableGuestList(guesttables)
    }

    override fun onGuestListError(errcode: String) {
        fragment.onTableGuestListError(GuestPresenter.ERRCODEGUESTS)
    }

    interface TableGuestList {
        fun onTableGuestList(list: ArrayList<TableGuests>)
        fun onTableGuestListError(errcode: String)
    }

}


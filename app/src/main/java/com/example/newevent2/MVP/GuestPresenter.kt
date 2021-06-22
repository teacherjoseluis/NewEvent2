package com.example.newevent2.MVP

import Application.Cache
import android.content.Context;
import android.os.Build
import android.view.View;
import androidx.annotation.RequiresApi

import com.example.newevent2.EventSummary;
import com.example.newevent2.Model.Guest
import com.example.newevent2.Model.GuestModel
import com.example.newevent2.Model.Task

class GuestPresenter : Cache.GuestArrayListCacheData {

    private var activefragment = ""
    private var mContext: Context

    private lateinit var fragmentES: EventSummaryPresenter

    private lateinit var cacheguest: Cache<Guest>

    constructor(context: Context, fragment: EventSummaryPresenter) {
        fragmentES = fragment
        mContext = context
        activefragment = "ES"
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getGuestList() {
        cacheguest = Cache(mContext, this)
        cacheguest.loadarraylist(Guest::class)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onArrayListG(arrayList: ArrayList<Guest>) {
        if (arrayList.size != 0) {
            when (activefragment) {
                "ES" -> fragmentES.onGuestList(arrayList)
            }
        }
    }

    override fun onEmptyListG() {
        val user = com.example.newevent2.Functions.getUserSession(mContext!!)
        val guest = GuestModel()
        guest.getAllGuestList(
            user.key,
            user.eventid,
            object : GuestModel.FirebaseSuccessGuestList {
                @RequiresApi(Build.VERSION_CODES.O)
                override fun onGuestList(arrayList: ArrayList<Guest>) {
                    if (arrayList.isNotEmpty()) {
                        // This may be heavy, getting all of the tasks from Firebase but storing them into the cache
                        cacheguest.save(arrayList)

                        when (activefragment) {
                            "ES" -> fragmentES.onGuestList(arrayList)
                        }
                    }else {
                        // This is when there is no data coming from Firebase
                        when (activefragment) {
                            "ES" -> fragmentES.onGuestListError(ERRCODEGUESTS)
                        }
                    }
                }

            }
        )
//        guest.getGuestsEvent(userid, eventid, object : GuestModel.FirebaseSuccessGuestStats {
//            override fun onGuestConfirmation(confirmed: Int, rejected: Int, pending: Int) {
//                fragmentMED.onGuestConfirmation(inflatedView, confirmed, rejected, pending)
//            }
//        })
//    }
//
//    interface GuestStats{
//        fun onGuestConfirmation(inflatedView: View, confirmed: Int, rejected: Int, pending: Int)
//    }
    }
    interface GuestList {
        fun onGuestList(list: ArrayList<Guest>)
        fun onGuestListError(errcode: String)
    }

    companion object {
        const val ERRCODEGUESTS = "NOGUESTS"
    }
}

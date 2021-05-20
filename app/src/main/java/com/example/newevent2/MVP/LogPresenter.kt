package com.example.newevent2.MVP

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.newevent2.DashboardActivity
import com.example.newevent2.Functions.FirebaseGetLogSuccess
import com.example.newevent2.Functions.Loginfo
import com.example.newevent2.Functions.getLog
import com.example.newevent2.Functions.removeLog
import com.example.newevent2.DashboardView
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.collections.ArrayList

class LogPresenter(
    view: DashboardActivity,
    userid: String,
    eventid: String
) {
    var viewDashboardActivity: DashboardActivity = view

    @RequiresApi(Build.VERSION_CODES.O)
    val dtf = DateTimeFormatter.ofPattern("yyyyMMdd")
    @RequiresApi(Build.VERSION_CODES.O)
    val currDate = LocalDate.now().minusDays(7)//Number of days old

    init {
        getLog(userid, eventid, object : FirebaseGetLogSuccess {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onGetLogSuccess(loglist: java.util.ArrayList<Loginfo>) {
                if (loglist.isNotEmpty()) {
                    for (log in loglist) {
                        val logDate = LocalDate.parse(log.date, dtf)
                        if (logDate.isBefore(currDate)) {
                            loglist.remove(log)
                            removeLog(userid, eventid, log.logkey)
                        }
                    }
                    if (loglist.isNotEmpty()){
                        //Log has enough elements to be shown
                        //viewDashboardActivity.onViewLogSuccess(loglist)
                    } else {
                        //Log has old elements and those will not be shown
                        //viewDashboardActivity.onViewLogError("OLD_LOG")
                    }

                } else {
                    //Log has no elements at all. Probably they were removed and user left and came back
                    //viewDashboardActivity.onViewLogError("EMPTY_LOG")
                }
            }
        })
    }

    interface ViewLogActivity {
        fun onViewLogSuccess(loglist: ArrayList<Loginfo>)
        fun onViewLogError(errcode: String)
    }
}
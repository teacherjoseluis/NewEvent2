package com.example.newevent2

import android.content.Intent
import android.icu.text.DateFormat.DAY
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.newevent2.MVP.DashboardActivityPresenter
import com.example.newevent2.MVP.TableGuestsActivityPresenter
import com.example.newevent2.Model.TableGuests
//import com.example.newevent2.MVP.TaskPresenter
import com.example.newevent2.Model.Task
import com.example.newevent2.Model.TaskJournal
import com.example.newevent2.ui.ViewAnimation
import kotlinx.android.synthetic.main.dashboardactivity.*
import kotlinx.android.synthetic.main.dashboardactivity.noactivity
import kotlinx.android.synthetic.main.dashboardactivity.view.*
import kotlinx.android.synthetic.main.dashboardactivity.view.NewTaskActionButton
import kotlinx.android.synthetic.main.guests_all.*
import kotlinx.android.synthetic.main.guests_all.view.*
import kotlinx.android.synthetic.main.tableguestsactivity.*
import kotlinx.android.synthetic.main.tableguestsactivity.view.*
import kotlinx.android.synthetic.main.welcome.*
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList

class TableGuestsActivity() : Fragment(), TableGuestsActivityPresenter.TableGuestList {

    lateinit var recyclerViewActivity: RecyclerView
    private lateinit var presenterguest: TableGuestsActivityPresenter
    private var isRotate = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        val inf = inflater.inflate(R.layout.tableguestsactivity, container, false)

        recyclerViewActivity = inf.tableguestsparentrv
        recyclerViewActivity.apply {
            layoutManager = LinearLayoutManager(inf.context).apply {
                stackFromEnd = true
                reverseLayout = true
                isNestedScrollingEnabled = false
            }
        }

        presenterguest = TableGuestsActivityPresenter(context!!, this)

        ViewAnimation.init(inf.NewGuestTG)
        ViewAnimation.init(inf.ContactGuestTG)

        inf.floatingActionButtonGuestTG.setOnClickListener()
        {
            isRotate = ViewAnimation.rotateFab(inf.floatingActionButtonGuestTG, !isRotate)
            if (isRotate) {
                ViewAnimation.showIn(NewGuestTG)
                ViewAnimation.showIn(ContactGuestTG)
            } else {
                ViewAnimation.showOut(NewGuestTG)
                ViewAnimation.showOut(ContactGuestTG)
            }
        }

        inf.fabNewGuestTG.setOnClickListener {
            val newguest = Intent(context, GuestCreateEdit::class.java)
            newguest.putExtra("userid", "")
            startActivity(newguest)
        }

        inf.fabContactGuestTG.setOnClickListener {
            val newguest = Intent(context, ContactsAll::class.java)
            newguest.putExtra("guestid", "")
            startActivity(newguest)
        }
        return inf
    }

    override fun onTableGuestList(list: ArrayList<TableGuests>) {
        val rvAdapter = Rv_TableGuestsAdapter(list)
        recyclerViewActivity.adapter = rvAdapter
    }

    override fun onTableGuestListError(errcode: String) {
        tableguestsparentrv.visibility = View.GONE
        noactivity.visibility = View.VISIBLE
    }
}



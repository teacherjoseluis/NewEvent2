package com.example.newevent2

import android.content.Intent
import android.hardware.SensorManager.getOrientation
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.newevent2.MVP.PaymentPresenter
import com.example.newevent2.MVP.TaskPresenter
import com.example.newevent2.Model.Task
import com.example.newevent2.ui.ViewAnimation
import kotlinx.android.synthetic.main.event_detail.*
import kotlinx.android.synthetic.main.mainevent_summary.*
import kotlinx.android.synthetic.main.mainevent_summary.view.*
import java.text.DecimalFormat
import java.util.*
import kotlin.collections.ArrayList

class EventCategories : Fragment() {

    var userid = ""
    var eventid = ""

    lateinit var recyclerViewCategory: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
        userid = this.arguments!!.get("userid").toString()
        eventid = this.arguments!!.get("eventid").toString()

        ViewAnimation.init(activity!!.TaskLayout)
        ViewAnimation.init(activity!!.PaymentLayout)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val inf = inflater.inflate(R.layout.mainevent_summary, container, false)

        //----------------------------------------------------------------------------------------------------

        val list = ArrayList<Category>(EnumSet.allOf(Category::class.java))

        recyclerViewCategory = inf.categoryrv
        recyclerViewCategory.apply {
            layoutManager = GridLayoutManager(context, 2).apply {
                reverseLayout = true
            }
        }

        val rvAdapter = rvCategoryAdapter(list)
        recyclerViewCategory.adapter = rvAdapter
        return inf
    }
}
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
import com.example.newevent2.Model.Task
import com.example.newevent2.Model.TaskDBHelper
import com.example.newevent2.ui.ViewAnimation
import kotlinx.android.synthetic.main.event_detail.*
import kotlinx.android.synthetic.main.mainevent_summary.*
import kotlinx.android.synthetic.main.mainevent_summary.view.*
import kotlinx.android.synthetic.main.taskpayment_list.*
import java.text.DecimalFormat
import java.util.*
import kotlin.collections.ArrayList

class EventCategories : Fragment() {

//    var userid = ""
//    var eventid = ""
    lateinit var recyclerViewCategory: RecyclerView
    private var isRotate = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
//        userid = this.arguments!!.get("userid").toString()
//        eventid = this.arguments!!.get("eventid").toString()


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val inf = inflater.inflate(R.layout.mainevent_summary, container, false)
        //----------------------------------------------------------------------------------------------------
        //val list = ArrayList<Category>(EnumSet.allOf(Category::class.java))
        val taskdb = TaskDBHelper(context!!)
        val list = taskdb.getActiveCategories()
        //list1.addAll(list2) - Need to consider adding categories from Payments in cases were no tasks where created but Payments were

        recyclerViewCategory = inf.categoryrv
        recyclerViewCategory.apply {
            layoutManager = GridLayoutManager(context, 2).apply {
                reverseLayout = true
            }
        }
        val rvAdapter = rvCategoryAdapter(list)
        recyclerViewCategory.adapter = rvAdapter

        ViewAnimation.init(inf.TaskLayout)
        ViewAnimation.init(inf.PaymentLayout)

        inf.NewTaskPaymentActionButton.setOnClickListener()
        {
            isRotate = ViewAnimation.rotateFab(inf.NewTaskPaymentActionButton, !isRotate)
            if (isRotate) {
                ViewAnimation.showIn(TaskLayout)
                ViewAnimation.showIn(PaymentLayout)
            } else {
                ViewAnimation.showOut(TaskLayout)
                ViewAnimation.showOut(PaymentLayout)
            }
        }

        inf.fabTask.setOnClickListener {
            val newtask = Intent(context, TaskCreateEdit::class.java)
            newtask.putExtra("userid", "")
            startActivity(newtask)
        }

        inf.fabPayment.setOnClickListener {
            val newpayment = Intent(context, PaymentCreateEdit::class.java)
            newpayment.putExtra("userid", "")
            startActivity(newpayment)
        }

        return inf
    }
}
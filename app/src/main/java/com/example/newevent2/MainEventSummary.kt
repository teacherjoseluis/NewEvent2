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
import com.example.newevent2.ui.ViewAnimation
import kotlinx.android.synthetic.main.event_detail.*
import kotlinx.android.synthetic.main.mainevent_summary.*
import kotlinx.android.synthetic.main.mainevent_summary.view.*
import java.text.DecimalFormat
import java.util.*
import kotlin.collections.ArrayList


class MainEventSummary : Fragment(), TaskPresenter.ViewTaskFragment,
    PaymentPresenter.ViewPaymentFragment {

    var isRotate = false
    var userid = ""
    var eventid = ""
    private val formatter = DecimalFormat("$#,###.00")

    lateinit var recyclerViewCategory: RecyclerView
    private lateinit var presentertask: TaskPresenter
    private lateinit var presenterpayment: PaymentPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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

        presentertask = TaskPresenter(this, inf, userid, eventid)
        presentertask.getTaskStats()

        presenterpayment = PaymentPresenter(this, inf, userid, eventid)
        presenterpayment.getPaymentStats()
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

        activity!!.NewTaskPaymentActionButton.setOnClickListener()
        {
            isRotate = ViewAnimation.rotateFab(activity!!.NewTaskPaymentActionButton, !isRotate)
            if (isRotate) {
                ViewAnimation.showIn(activity!!.TaskLayout);
                ViewAnimation.showIn(activity!!.PaymentLayout);
            } else {
                ViewAnimation.showOut(activity!!.TaskLayout);
                ViewAnimation.showOut(activity!!.PaymentLayout);
            }
        }

        activity!!.fabTask.setOnClickListener {
            val newtask = Intent(activity, NewTask_TaskDetail::class.java)
            startActivity(newtask)
        }

        activity!!.fabPayment.setOnClickListener {
            val newpayment = Intent(activity, NewTask_PaymentDetail::class.java)
            startActivity(newpayment)
        }
        return inf
    }

    override fun onViewTaskStatsSuccessFragment(
        inflatedView: View,
        taskpending: Int,
        taskcompleted: Int,
        sumbudget: Float
    ) {
        eventsummarylayoutempty.visibility = ConstraintLayout.GONE
        inflatedView.taskpending.text = taskpending.toString()
        inflatedView.taskcompleted.text = taskcompleted.toString()
        inflatedView.tasknumber.text = (taskpending + taskcompleted).toString()
        inflatedView.taskbudget.text = formatter.format(sumbudget)
    }

    override fun onViewTaskErrorFragment(inflatedView: View, errcode: String) {
        visiblelayout.visibility = ConstraintLayout.GONE
    }

    override fun onViewPaymentStatsSuccessFragment(
        inflatedView: View,
        countpayment: Int,
        sumpayment: Float,
        sumbudget: Float
    ) {
        eventsummarylayoutempty.visibility = ConstraintLayout.GONE
        inflatedView.paymentpaid0.text = formatter.format(sumpayment)
    }

    override fun onViewPaymentErrorFragment(inflatedView: View, errcode: String) {
        //eventsummarylayoutempty.visibility = ConstraintLayout.GONE
        inflatedView.paymentpaid0.text = "$0.00"
    }
}
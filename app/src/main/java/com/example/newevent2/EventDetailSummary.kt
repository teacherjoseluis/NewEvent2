package com.example.newevent2

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.newevent2.ui.ViewAnimation
import kotlinx.android.synthetic.main.event_detail.*
import kotlinx.android.synthetic.main.eventdetail_summary.view.*
import kotlinx.android.synthetic.main.my_events.*
import java.text.DecimalFormat


class EventDetailSummary : Fragment() {

    //lateinit var eventkey: String
    var isRotate = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //eventkey = this.arguments!!.get("eventkey").toString()

        ViewAnimation.init(activity!!.TaskLayout)
        ViewAnimation.init(activity!!.PaymentLayout)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val inf = inflater.inflate(R.layout.eventdetail_summary, container, false)


        // Event Summary
        // Tasks Summary
        //----------------------------------------------------------------------------------------------------

        val formatter = DecimalFormat("$#,###.00")

        val taskentity = TaskEntity()
        //taskentity.eventid = eventkey
        taskentity.getTasksEvent(activity!!.applicationContext, object : FirebaseSuccessListenerTask {
            override fun onTasksEvent(taskpending: Int, taskcompleted: Int, sumbudget: Float) {
                inf.taskpending.text = taskpending.toString()
                inf.taskcompleted.text = taskcompleted.toString()
                inf.tasknumber.text = (taskpending + taskcompleted).toString()
                inf.taskbudget.text = formatter.format(sumbudget)
            }

            override fun onTasksList(list: ArrayList<Task>) {
                TODO("Not yet implemented")
            }
        })

        val paymententity = PaymentEntity()
        //paymententity.eventid = eventkey
        paymententity.getPaymentEvent(activity!!.applicationContext, object : FirebaseSuccessListenerPayment {
            override fun onPaymentEvent(sumpayment: Float) {
                inf.paymentpaid.text = formatter.format(sumpayment)
            }

            override fun onPaymentList(list: ArrayList<Payment>) {
                TODO("Not yet implemented")
            }

            override fun onPaymentStats(countpayment: Int, sumpayment: Float) {
                TODO("Not yet implemented")
            }
        })

        inf.cardView_venue.setOnClickListener()
        {
            val tasklist = Intent(activity, TaskPaymentList::class.java)
            //tasklist.putExtra("eventkey", eventkey)
            tasklist.putExtra("category", "venue")
            startActivity(tasklist)
        }
        inf.cardView_photo.setOnClickListener()
        {
            val tasklist = Intent(activity, TaskPaymentList::class.java)
            //tasklist.putExtra("eventkey", eventkey)
            tasklist.putExtra("category", "photo")
            startActivity(tasklist)
        }
        inf.cardView_entertainment.setOnClickListener()
        {
            val tasklist = Intent(activity, TaskPaymentList::class.java)
            //tasklist.putExtra("eventkey", eventkey)
            tasklist.putExtra("category", "entertainment")
            startActivity(tasklist)
        }
        inf.cardView_flowers.setOnClickListener()
        {
            val tasklist = Intent(activity, TaskPaymentList::class.java)
            //tasklist.putExtra("eventkey", eventkey)
            tasklist.putExtra("category", "flowers")
            startActivity(tasklist)
        }
        inf.cardView_transportation.setOnClickListener()
        {
            val tasklist = Intent(activity, TaskPaymentList::class.java)
            //tasklist.putExtra("eventkey", eventkey)
            tasklist.putExtra("category", "transportation")
            startActivity(tasklist)
        }
        inf.cardView_ceremony.setOnClickListener()
        {
            val tasklist = Intent(activity, TaskPaymentList::class.java)
            //tasklist.putExtra("eventkey", eventkey)
            tasklist.putExtra("category", "ceremony")
            startActivity(tasklist)
        }
        inf.cardView_attire.setOnClickListener()
        {
            val tasklist = Intent(activity, TaskPaymentList::class.java)
            //tasklist.putExtra("eventkey", eventkey)
            tasklist.putExtra("category", "attire")
            startActivity(tasklist)
        }
        inf.cardView_beauty.setOnClickListener()
        {
            val tasklist = Intent(activity, TaskPaymentList::class.java)
            //tasklist.putExtra("eventkey", eventkey)
            tasklist.putExtra("category", "beauty")
            startActivity(tasklist)
        }
        inf.cardView_food.setOnClickListener()
        {
            val tasklist = Intent(activity, TaskPaymentList::class.java)
            //tasklist.putExtra("eventkey", eventkey)
            tasklist.putExtra("category", "food")
            startActivity(tasklist)
        }
        inf.cardView_guests.setOnClickListener()
        {
            val tasklist = Intent(activity, TaskPaymentList::class.java)
            //tasklist.putExtra("eventkey", eventkey)
            tasklist.putExtra("category", "guests")
            startActivity(tasklist)
        }

        activity!!.NewTaskPaymentActionButton.setOnClickListener()
        {

            isRotate = ViewAnimation.rotateFab(activity!!.NewTaskPaymentActionButton, !isRotate)
            if(isRotate){
                ViewAnimation.showIn(activity!!.TaskLayout);
                ViewAnimation.showIn(activity!!.PaymentLayout);
            }else{
                ViewAnimation.showOut(activity!!.TaskLayout);
                ViewAnimation.showOut(activity!!.PaymentLayout);
            }
        }

        activity!!.fabTask.setOnClickListener(View.OnClickListener {
            val newtask = Intent(activity, NewTask_TaskDetail::class.java)
            //newtask.putExtra("eventkey", eventkey)
            startActivity(newtask)
//            Toast.makeText(
//                activity,
//                "New Task",
//                Toast.LENGTH_SHORT
//            ).show()
        })

        activity!!.fabPayment.setOnClickListener(View.OnClickListener {
            val newpayment = Intent(activity, NewTask_PaymentDetail::class.java)
            //newpayment.putExtra("eventkey", eventkey)
            startActivity(newpayment)
//            Toast.makeText(
//                activity,
//                "New Payment",
//                Toast.LENGTH_SHORT
//            ).show()
        })
        return inf
    }
}
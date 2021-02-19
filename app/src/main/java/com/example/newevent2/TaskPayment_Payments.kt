package com.example.newevent2

import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.taskpayment_payments.view.*
import kotlinx.android.synthetic.main.taskpayment_tasks.view.*
import java.text.DecimalFormat

class TaskPayment_Payments : Fragment() {
    //private var eventkey: String = ""
    private var category: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //eventkey = this.arguments!!.get("eventkey").toString()
        category = this.arguments!!.get("category").toString()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val inf = inflater.inflate(R.layout.taskpayment_payments, container, false)

        val recyclerView = inf.PaymentsRecyclerView

        recyclerView.apply {
            layoutManager = LinearLayoutManager(inf.context).apply {
                stackFromEnd = true
                reverseLayout = true
            }
        }

        val paymententity = PaymentEntity()
        //paymententity.eventid = eventkey
        paymententity.category = category

        paymententity.getPaymentsList(activity!!.applicationContext, object : FirebaseSuccessListenerPayment {
            override fun onPaymentEvent(sumpayment: Float) {
                TODO("Not yet implemented")
            }

            @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
            override fun onPaymentList(list: ArrayList<Payment>) {
                val rvAdapter = Rv_PaymentAdapter(list)
                recyclerView.adapter = rvAdapter

                val swipeController = SwipeControllerTasks(
                    inf.context,
                    rvAdapter,
                    recyclerView,
                    null,
                    "delete"
                )
                val itemTouchHelper = ItemTouchHelper(swipeController)
                itemTouchHelper.attachToRecyclerView(recyclerView)
            }

            override fun onPaymentStats(countpayment: Int, sumpayment: Float) {
                TODO("Not yet implemented")
            }
        })

        paymententity.getPaymentStats(activity!!.applicationContext, object: FirebaseSuccessListenerPayment {
            override fun onPaymentEvent(sumpayment: Float) {
                TODO("Not yet implemented")
            }

            override fun onPaymentList(list: ArrayList<Payment>) {
                TODO("Not yet implemented")
            }

            override fun onPaymentStats(countpayment: Int, sumpayment: Float) {
                val formatter = DecimalFormat("$#,###.00")
                inf.totalpaid.text = formatter.format(sumpayment)
                inf.payments.text = countpayment.toString()
            }
        })
        return inf
    }

}
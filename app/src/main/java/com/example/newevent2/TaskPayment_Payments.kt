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
    private var eventkey: String = ""
    private var category: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        eventkey = this.arguments!!.get("eventkey").toString()
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
        paymententity.eventid = eventkey
        paymententity.category = category

        paymententity.getPaymentsList(object : FirebaseSuccessListenerPayment {
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

        paymententity.getPaymentStats(object: FirebaseSuccessListenerPayment {
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

//        val database = FirebaseDatabase.getInstance()
//        val myRef = database.reference
//        //FirebaseUser user = mAuth.getCurrentUser();
//        val postRef = myRef.child("User").child("Event").child(eventkey).child("Payment")
//        var paymentlist = ArrayList<Payment>()
//
//        val paymentListener = object : ValueEventListener {
//            @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
//            override fun onDataChange(p0: DataSnapshot) {
//
//                paymentlist.clear()
//
//                if (p0.exists()) {
//
//                    for (snapshot in p0.children) {
//                        val paymentitem = snapshot.getValue(Payment::class.java)
//                        if (paymentitem!!.category == category) {
//                            paymentitem!!.key = snapshot.key.toString()
//                            paymentlist.add(paymentitem!!)
//                        }
//                    }
//                }
//
//                //        pass the values to RvAdapter
//                val rvAdapter = Rv_PaymentAdapter(paymentlist)
////        set the recyclerView to the adapter
//                recyclerView.adapter = rvAdapter
//
//                val swipeController = SwipeControllerPayment(inf.context, rvAdapter, recyclerView)
//                val itemTouchHelper = ItemTouchHelper(swipeController)
//                itemTouchHelper.attachToRecyclerView(recyclerView)
//
//                var sumpayment: Float = 0.0F
//                var countpayment: Int = 0
//
//                for (payment in paymentlist) {
//                    val re = Regex("[^A-Za-z0-9 ]")
//                    val paymentamount = re.replace(payment.amount, "").dropLast(2)
//                    sumpayment += paymentamount.toFloat()
//                    countpayment += 1
//                }
//
//                val formatter = DecimalFormat("$#,###.00")
//
//                inf.totalpaid.text = formatter.format(sumpayment)
//                inf.payments.text = countpayment.toString()
//            }
//
//            override fun onCancelled(databaseError: DatabaseError) {
//                println("loadPost:onCancelled ${databaseError.toException()}")
//            }
//        }
//        postRef.addValueEventListener(paymentListener)

        return inf
    }

}
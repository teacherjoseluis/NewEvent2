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
import kotlinx.android.synthetic.main.tasklist_payments.view.*
import kotlinx.android.synthetic.main.tasklist_tasks.view.recyclerView
import java.text.DecimalFormat

class TaskList_Payments : Fragment() {
    lateinit var eventkey: String
    lateinit var category: String

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
        val inf = inflater.inflate(R.layout.tasklist_payments, container, false)
        //inf.textView9.text = category

        val recyclerView = inf.recyclerView

        recyclerView.apply {
            layoutManager = LinearLayoutManager(inf.context).apply {
                stackFromEnd = true
                reverseLayout = true
            }
        }

        val database = FirebaseDatabase.getInstance()
        val myRef = database.reference
        //FirebaseUser user = mAuth.getCurrentUser();
        val postRef = myRef.child("User").child("Event").child(eventkey).child("Payment")
        var paymentlist = ArrayList<Payment>()

        val paymentListener = object : ValueEventListener {
            @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
            override fun onDataChange(p0: DataSnapshot) {

                paymentlist.clear()

                if (p0.exists()) {

                    for (snapshot in p0.children) {
                        val paymentitem = snapshot.getValue(Payment::class.java)
                        if (paymentitem!!.category == category) {
                            paymentitem!!.key = snapshot.key.toString()
                            paymentlist.add(paymentitem!!)
                        }
                    }
                }

                //        pass the values to RvAdapter
                val rvAdapter = Rv_PaymentAdapter(paymentlist)
//        set the recyclerView to the adapter
                recyclerView.adapter = rvAdapter

                val swipeController = SwipeControllerPayment(inf.context, rvAdapter, recyclerView)
                val itemTouchHelper = ItemTouchHelper(swipeController)
                itemTouchHelper.attachToRecyclerView(recyclerView)

                var sumpayment: Float = 0.0F
                var countpayment: Int = 0

                for (payment in paymentlist) {
                    val re = Regex("[^A-Za-z0-9 ]")
                    val paymentamount = re.replace(payment.amount, "").dropLast(2)
                    sumpayment += paymentamount.toFloat()
                    countpayment += 1
                }

                val formatter = DecimalFormat("$#,###.00")

                inf.textView16.text = formatter.format(sumpayment)
                inf.textView17.text = countpayment.toString()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                println("loadPost:onCancelled ${databaseError.toException()}")
            }
        }
        postRef.addValueEventListener(paymentListener)

        return inf
    }

}
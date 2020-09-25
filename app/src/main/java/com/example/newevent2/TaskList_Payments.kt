package com.example.newevent2

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.eventdetail_summary.*
import kotlinx.android.synthetic.main.eventdetail_summary.view.*
import kotlinx.android.synthetic.main.tasklist_payments.view.*
import kotlinx.android.synthetic.main.tasklist_tasks.view.*
import kotlinx.android.synthetic.main.tasklist_tasks.view.recyclerView
import java.text.DecimalFormat

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [EventDetail_Summary.newInstance] factory method to
 * create an instance of this fragment.
 */
class TaskList_Payments : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    lateinit var eventkey: String
    lateinit var category: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        eventkey = this.arguments!!.get("eventkey").toString()
        category = this.arguments!!.get("category").toString()

        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
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
            override fun onDataChange(p0: DataSnapshot) {
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

                var sumpayment: Float = 0.0F
                var countpayment: Int = 0

                for (payment in paymentlist) {
                    val re = Regex("[^A-Za-z0-9 ]")
                    val paymentamount = re.replace(payment.amount, "").dropLast(2)
                    sumpayment += paymentamount.toFloat()
                    countpayment += 1
                }

                val formatter =DecimalFormat("$#,###.00")

                inf.textView16.text = formatter.format(sumpayment)
                inf.textView17.text = countpayment.toString()

                /*
                val obs = recyclerView.viewTreeObserver
                obs.addOnGlobalLayoutListener {
                    recyclerView.requestLayout()
                    recyclerView.invalidate()
                }
                */
                //recyclerView.scrollToPosition(recyclerView.adapter!!.itemCount - 1)

            }

            override fun onCancelled(databaseError: DatabaseError) {
                println("loadPost:onCancelled ${databaseError.toException()}")
            }
        }
        postRef.addValueEventListener(paymentListener)

        return inf
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment EventDetail_Summary.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            EventDetail_Summary().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
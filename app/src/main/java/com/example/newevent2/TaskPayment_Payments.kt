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
import com.example.newevent2.MVP.PaymentPresenter
import com.example.newevent2.Model.Payment
import kotlinx.android.synthetic.main.taskpayment_payments.view.*
import java.text.DecimalFormat
import java.util.ArrayList

class TaskPayment_Payments : Fragment(), PaymentPresenter.ViewPaymentList,
    PaymentPresenter.ViewPaymentFragment {

    private var userid: String = ""
    private var eventid: String = ""
    private var category: String = ""

    private lateinit var presenterpayment: PaymentPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userid = this.arguments!!.get("userid").toString()
        eventid = this.arguments!!.get("eventid").toString()
        category = this.arguments!!.get("category").toString()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val inf = inflater.inflate(R.layout.taskpayment_payments, container, false)

        presenterpayment = PaymentPresenter(this, inf, userid, eventid)
        presenterpayment.getPaymentStats(category)
        presenterpayment.getPaymentList(category)

        return inf
    }

    override fun onViewPaymentStatsSuccessFragment(
        inflatedView: View,
        countpayment: Int,
        sumpayment: Float,
        sumbudget: Float
    ) {
        val formatter = DecimalFormat("$#,###.00")
        inflatedView.totalpaid.text = formatter.format(sumpayment)
        inflatedView.payments.text = countpayment.toString()
    }

    override fun onViewPaymentErrorFragment(inflatedView: View, errcode: String) {
        TODO("Not yet implemented")
        // What to show when the consulted category has no payments?
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onViewPaymentListFragment(
        inflatedView: View,
        category: String,
        list: ArrayList<Payment>
    ) {
        val recyclerView = inflatedView.PaymentsRecyclerView

        recyclerView.apply {
            layoutManager = LinearLayoutManager(inflatedView.context).apply {
                stackFromEnd = true
                reverseLayout = true
            }
        }

        val rvAdapter = Rv_PaymentAdapter(userid, eventid, list)
        recyclerView.adapter = rvAdapter

        val swipeController = SwipeControllerTasks(
            inflatedView.context,
            rvAdapter,
            recyclerView,
            LEFTACTION,
            RIGHTACTION
        )
        val itemTouchHelper = ItemTouchHelper(swipeController)
        itemTouchHelper.attachToRecyclerView(recyclerView)
    }

    override fun onViewPaymentListErrorFragment(
        inflatedView: View,
        category: String,
        errcode: String
    ) {
        TODO("Not yet implemented")
        // What to show when the consulted category has no payments?
    }

    companion object {
        const val LEFTACTION = ""
        const val RIGHTACTION = "delete"
    }
}
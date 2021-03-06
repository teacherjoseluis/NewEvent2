package com.example.newevent2

import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.newevent2.MVP.PaymentPresenter
import com.example.newevent2.MVP.TaskPaymentTasksPresenter
import com.example.newevent2.Model.Payment
import com.example.newevent2.Model.TaskModel
import kotlinx.android.synthetic.main.taskpayment_payments.view.*
import kotlinx.android.synthetic.main.taskpayment_tasks.view.*
import java.text.DecimalFormat

class TaskPaymentPayments : Fragment(), TaskPaymentPaymentsPresenter.TPPayments {

    private var category: String = ""

    private lateinit var presenterpayment: TaskPaymentPaymentsPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        category = if (this.arguments!!.get("category") != null) {
            this.arguments!!.get("category").toString()
        } else {
            ""
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val inf = inflater.inflate(R.layout.taskpayment_payments, container, false)
        presenterpayment = TaskPaymentPaymentsPresenter(context!!, this, inf, category)
        return inf
    }

    override fun onTPPayments(inflatedView: View, list: ArrayList<Payment>) {
        if (list.size != 0) {
                val recyclerView = inflatedView.PaymentsRecyclerView
            recyclerView.apply {
                    layoutManager = LinearLayoutManager(inflatedView.context).apply {
                        stackFromEnd = true
                        reverseLayout = true
                    }
                }
                val rvAdapter = Rv_PaymentAdapter(list)
            recyclerView.adapter = rvAdapter

                val swipeController =
                    SwipeControllerTasks(
                        inflatedView.context,
                        rvAdapter,
                        recyclerView,
                        null,
                        RIGHTACTION
                    )
                val itemTouchHelper = ItemTouchHelper(swipeController)
                itemTouchHelper.attachToRecyclerView(recyclerView)
        } else if (list.size == 0) {
            inflatedView.activepaymentslayout.visibility = ConstraintLayout.INVISIBLE
        }
    }

    override fun onTPPaymentsError(inflatedView: View, errcode: String) {
        inflatedView.withnodatataskpaymentp.visibility = ConstraintLayout.VISIBLE
        inflatedView.scrollviewp.visibility = ConstraintLayout.GONE
    }

    companion object {
        const val RIGHTACTION = "delete"
    }
}
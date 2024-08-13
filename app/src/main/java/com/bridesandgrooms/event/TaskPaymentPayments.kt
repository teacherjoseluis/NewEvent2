package com.bridesandgrooms.event

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bridesandgrooms.event.Model.Payment
import com.bridesandgrooms.event.TaskPaymentTasks.Companion.TAG
import com.bridesandgrooms.event.UI.Adapters.ItemSwipeListenerPayment
import com.bridesandgrooms.event.UI.Adapters.PaymentAdapter
import com.bridesandgrooms.event.databinding.TaskpaymentPaymentsBinding
import com.bridesandgrooms.event.UI.SwipeControllerTasks
import com.google.android.material.floatingactionbutton.FloatingActionButton

class TaskPaymentPayments : Fragment(), TaskPaymentPaymentsPresenter.TPPayments,
    ItemSwipeListenerPayment, TPP_PaymentFragmentActionListener {

    private var category: String = ""

    private lateinit var inf: TaskpaymentPaymentsBinding
    private lateinit var presenterpayment: TaskPaymentPaymentsPresenter
    private lateinit var recyclerView: RecyclerView
    private lateinit var rvAdapter: PaymentAdapter

    private var mContext: Context? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        category = if (arguments?.get("category") != null) {
            this.requireArguments().get("category").toString()
        } else {
            ""
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        inf = DataBindingUtil.inflate(inflater, R.layout.taskpayment_payments, container, false)
        try {
            presenterpayment = TaskPaymentPaymentsPresenter(mContext!!, this, category)
            presenterpayment.getPaymentList()
        } catch (e: Exception) {
            Log.e(TAG, e.message.toString())
        }
        return inf.root
    }

    override fun onTPPayments(list: ArrayList<Payment>) {
        if (list.size > 0) {
            recyclerView = inf.PaymentsRecyclerView
            recyclerView.apply {
                layoutManager = LinearLayoutManager(inf.root.context).apply {
                    stackFromEnd = true
                    reverseLayout = true
                }
            }

            rvAdapter = PaymentAdapter(this, list, this, mContext!!)
            recyclerView.adapter = null
            recyclerView.adapter = rvAdapter

            val swipeController =
                SwipeControllerTasks(
                    inf.root.context,
                    rvAdapter,
                    recyclerView,
                    null,
                    RIGHTACTION
                )
            val itemTouchHelper = ItemTouchHelper(swipeController)
            itemTouchHelper.attachToRecyclerView(recyclerView)
        } else {
            emptyStateFragment()
        }
    }

    override fun onTPPaymentsError(errcode: String) {
        emptyStateFragment()
    }

    fun emptyStateFragment() {
        val container = view as ViewGroup?
        container?.removeAllViews()

        val newView = layoutInflater.inflate(R.layout.empty_state_fragment, container, false)
        container?.addView(newView)

        newView.findViewById<TextView>(R.id.emptystate_message).setText(R.string.emptystate_nopaymentsmsg)
        newView.findViewById<TextView>(R.id.emptystate_cta).setText(R.string.emptystate_nopaymentscta)
        newView.findViewById<FloatingActionButton>(R.id.fab_action).setOnClickListener {
            callPaymentCreateFragment()
        }
    }

    override fun onItemSwiped(paymentList: MutableList<Payment>) {
        if (paymentList.isEmpty()) {
            emptyStateFragment()
        } else {
            inf.activepaymentslayout.visibility = ConstraintLayout.VISIBLE
            val emptystateLayout = inf.withnodatataskpaymentp
            emptystateLayout.root.visibility = ConstraintLayout.GONE
        }
    }

    companion object {
        const val RIGHTACTION = "delete"
    }

    override fun onPaymentFragmentWithData(payment: Payment) {
        val fragment = PaymentCreateEdit()
        val bundle = Bundle()
        bundle.putParcelable("payment", payment)
        bundle.putString("calling_fragment", "TaskPaymentPayments")
        fragment.arguments = bundle
        activity?.supportFragmentManager?.beginTransaction()
            ?.replace(
                R.id.fragment_container,
                fragment
            )
            ?.addToBackStack(null)
            ?.commit()
    }

    fun callPaymentCreateFragment(){
        val fragment = PaymentCreateEdit()
        val bundle = Bundle()
        bundle.putString("calling_fragment", "EmptyState")
        fragment.arguments = bundle
        activity?.supportFragmentManager?.beginTransaction()
            ?.replace(
                R.id.fragment_container,
                fragment
            )
            ?.addToBackStack(null)
            ?.commit()
    }
}

interface TPP_PaymentFragmentActionListener {
    fun onPaymentFragmentWithData(payment: Payment)
}
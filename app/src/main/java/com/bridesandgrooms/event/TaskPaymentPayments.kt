package com.bridesandgrooms.event

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bridesandgrooms.event.Model.Payment
import kotlinx.android.synthetic.main.empty_state.view.*
import kotlinx.android.synthetic.main.onboardingcard.view.*
import kotlinx.android.synthetic.main.taskpayment_payments.view.*

class TaskPaymentPayments : Fragment(), TaskPaymentPaymentsPresenter.TPPayments {

    private var category: String = ""

    private lateinit var inf: View
    private lateinit var presenterpayment: TaskPaymentPaymentsPresenter
    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        category = if (this.requireArguments().get("category") != null) {
            this.requireArguments().get("category").toString()
        } else {
            ""
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        inf = inflater.inflate(R.layout.taskpayment_payments, container, false)
        presenterpayment = TaskPaymentPaymentsPresenter(requireContext(), this, inf, category)
        return inf
    }

    override fun onTPPayments(inflatedView: View, list: ArrayList<Payment>) {
        if (list.size != 0) {
                recyclerView = inflatedView.PaymentsRecyclerView
            recyclerView.apply {
                    layoutManager = LinearLayoutManager(inflatedView.context).apply {
                        stackFromEnd = true
                        reverseLayout = true
                    }
                }
                val rvAdapter = Rv_PaymentAdapter(lifecycleScope, list)
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
            inflatedView.withnodatataskpaymentp.visibility = ConstraintLayout.VISIBLE
            //inflatedView.onboardingmessage.text = getString(R.string.emptystate_nopaymentsmsg)
            inflatedView.withnodatataskpaymentp.empty_card.onboardingmessage.text =
                getString(R.string.emptystate_nopaymentsmsg)
            inflatedView.activepaymentslayout.visibility = ConstraintLayout.GONE
            inflatedView.withnodatataskpaymentp.newtaskbutton.hide()
        }
    }

    override fun onTPPaymentsError(inflatedView: View, errcode: String) {
        inflatedView.withnodatataskpaymentp.visibility = ConstraintLayout.VISIBLE
        //inflatedView.onboardingmessage.text = getString(R.string.emptystate_nopaymentsmsg)
        inflatedView.withnodatataskpaymentp.empty_card.onboardingmessage.text =
            getString(R.string.emptystate_nopaymentsmsg)
        inflatedView.activepaymentslayout.visibility = ConstraintLayout.GONE
        inflatedView.withnodatataskpaymentp.newtaskbutton.hide()
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onResume() {
        super.onResume()
        try {
            presenterpayment = TaskPaymentPaymentsPresenter(requireContext(), this, inf, category)
        } catch (e: Exception) {
            println(e.message)
        }
//        recyclerViewActive.adapter = null
//        recyclerViewActive.adapter = rvAdapter
    }

    fun reloadRecyclerView() {
        // Replace `yourRecyclerView` with the actual reference to your RecyclerView
        recyclerView.adapter?.notifyDataSetChanged()
    }

    companion object {
        const val RIGHTACTION = "delete"
    }
}
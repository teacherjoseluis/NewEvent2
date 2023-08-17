package com.bridesandgrooms.event

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.widget.ViewPager2
import com.bridesandgrooms.event.Model.Payment
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout
import com.kcrimi.tooltipdialog.ToolTipDialog
import kotlinx.android.synthetic.main.empty_state.view.*
import kotlinx.android.synthetic.main.my_notes.view.*
import kotlinx.android.synthetic.main.onboardingcard.view.*
import kotlinx.android.synthetic.main.taskpayment_list.*
import kotlinx.android.synthetic.main.taskpayment_payments.view.*

class TaskPaymentPayments : Fragment(), TaskPaymentPaymentsPresenter.TPPayments,
    ItemSwipeListenerPayment {

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
        try {
            presenterpayment = TaskPaymentPaymentsPresenter(requireContext(), this, inf, category)
        } catch (e: Exception) {
            println(e.message)
        }
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
            val rvAdapter = Rv_PaymentAdapter(lifecycleScope, list, this)
            recyclerView.adapter = rvAdapter

            val swipeController =
                SwipeControllerTasks(
                    inflatedView.context,
                    rvAdapter,
                    recyclerView,
                    null,
                    RIGHTACTION
                )
            //----------------------------------------------------------------
            val itemTouchHelper = ItemTouchHelper(swipeController)
            itemTouchHelper.attachToRecyclerView(recyclerView)
            //----------------------------------------------------------------
            inf.activepaymentslayout.visibility = ConstraintLayout.VISIBLE
            val emptystateLayout = inf.findViewById<ConstraintLayout>(R.id.withnodatataskpaymentp)
            emptystateLayout.visibility = ConstraintLayout.GONE
            //----------------------------------------------------------------
        } else if (list.size == 0) {
            inflatedView.activepaymentslayout.visibility = ConstraintLayout.GONE
            val emptystateLayout =
                inflatedView.findViewById<ConstraintLayout>(R.id.withnodatataskpaymentp)
            //----------------------------------------------------------------
            val topMarginInPixels = resources.getDimensionPixelSize(R.dimen.emptystate_topmargin)
            val bottomMarginInPixels =
                resources.getDimensionPixelSize(R.dimen.emptystate_marginbottom)
            val params = emptystateLayout.layoutParams as ViewGroup.MarginLayoutParams
            params.topMargin = topMarginInPixels
            params.bottomMargin = bottomMarginInPixels
            emptystateLayout.layoutParams = params
            //----------------------------------------------------------------
            emptystateLayout.visibility = ConstraintLayout.VISIBLE
            emptystateLayout.empty_card.onboardingmessage.text =
                getString(R.string.emptystate_nopaymentsmsg)
            val fadeAnimation = AnimationUtils.loadAnimation(context, R.anim.blinking_animation)
            emptystateLayout.newtaskbutton.startAnimation(fadeAnimation)
            emptystateLayout.newtaskbutton.setOnClickListener {
                val newPayment = Intent(context, PaymentCreateEdit::class.java)
                newPayment.putExtra("userid", "")
                startActivity(newPayment)
            }
        }
    }

    override fun onTPPaymentsError(inflatedView: View, errcode: String) {
        inflatedView.activepaymentslayout.visibility = ConstraintLayout.GONE
        val emptystateLayout =
            inflatedView.findViewById<ConstraintLayout>(R.id.withnodatataskpaymentp)
        //----------------------------------------------------------------
        val topMarginInPixels = resources.getDimensionPixelSize(R.dimen.emptystate_topmargin)
        val bottomMarginInPixels =
            resources.getDimensionPixelSize(R.dimen.emptystate_marginbottom)
        val params = emptystateLayout.layoutParams as ViewGroup.MarginLayoutParams
        params.topMargin = topMarginInPixels
        params.bottomMargin = bottomMarginInPixels
        emptystateLayout.layoutParams = params
        //----------------------------------------------------------------
        emptystateLayout.visibility = ConstraintLayout.VISIBLE
        emptystateLayout.empty_card.onboardingmessage.text =
            getString(R.string.emptystate_nopaymentsmsg)
        val fadeAnimation = AnimationUtils.loadAnimation(context, R.anim.blinking_animation)
        emptystateLayout.newtaskbutton.startAnimation(fadeAnimation)
        emptystateLayout.newtaskbutton.setOnClickListener {
            val newPayment = Intent(context, PaymentCreateEdit::class.java)
            newPayment.putExtra("userid", "")
            startActivity(newPayment)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onResume() {
        super.onResume()
        try {
            presenterpayment = TaskPaymentPaymentsPresenter(requireContext(), this, inf, category)
        } catch (e: Exception) {
            println(e.message)
        }
    }

    override fun onItemSwiped(paymentList: MutableList<Payment>) {
        if (paymentList.isEmpty()) {
            inf.activepaymentslayout.visibility = ConstraintLayout.GONE
            val emptystateLayout =
                inf.findViewById<ConstraintLayout>(R.id.withnodatataskpaymentp)
            //----------------------------------------------------------------
            val topMarginInPixels = resources.getDimensionPixelSize(R.dimen.emptystate_topmargin)
            val bottomMarginInPixels =
                resources.getDimensionPixelSize(R.dimen.emptystate_marginbottom)
            val params = emptystateLayout.layoutParams as ViewGroup.MarginLayoutParams
            params.topMargin = topMarginInPixels
            params.bottomMargin = bottomMarginInPixels
            emptystateLayout.layoutParams = params
            //----------------------------------------------------------------
            emptystateLayout.visibility = ConstraintLayout.VISIBLE
            emptystateLayout.empty_card.onboardingmessage.text =
                getString(R.string.emptystate_nopaymentsmsg)
            val fadeAnimation = AnimationUtils.loadAnimation(context, R.anim.blinking_animation)
            emptystateLayout.newtaskbutton.startAnimation(fadeAnimation)
            emptystateLayout.newtaskbutton.setOnClickListener {
                val newPayment = Intent(context, PaymentCreateEdit::class.java)
                newPayment.putExtra("userid", "")
                startActivity(newPayment)
            }
        } else {
            //----------------------------------------------------------------
            inf.activepaymentslayout.visibility = ConstraintLayout.VISIBLE
            val emptystateLayout = inf.findViewById<ConstraintLayout>(R.id.withnodatataskpaymentp)
            emptystateLayout.visibility = ConstraintLayout.GONE
            //----------------------------------------------------------------
        }
    }

    companion object {
        const val RIGHTACTION = "delete"
    }
}
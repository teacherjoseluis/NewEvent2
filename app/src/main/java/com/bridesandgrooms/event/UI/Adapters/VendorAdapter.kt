package com.bridesandgrooms.event.UI.Adapters

import Application.AnalyticsManager
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bridesandgrooms.event.UI.Fragments.FragmentActionListener
import com.bridesandgrooms.event.Functions.sumStrings
import com.bridesandgrooms.event.Model.Category
import com.bridesandgrooms.event.Model.PaymentDBHelper
import com.bridesandgrooms.event.Model.VendorPayment
import com.bridesandgrooms.event.R
import com.bridesandgrooms.event.UI.Adapters.TaskAdapter.Companion.SCREEN_NAME
import com.bridesandgrooms.event.UI.LetterAvatar

class VendorAdapter(
    private val fragmentActionListener: FragmentActionListener,
    private val contactlist: ArrayList<VendorPayment>,
    val context: Context
) :
    RecyclerView.Adapter<VendorAdapter.VendorViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VendorViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.vendor_cardview_contracted, parent, false)
        return VendorViewHolder(view)
    }

    override fun getItemCount(): Int {
        return contactlist.size
    }

    override fun onBindViewHolder(holder: VendorViewHolder, position: Int) {
        val vendorPayment = contactlist[position]
        holder.bind(vendorPayment)
    }

    /**
     * This particular ViewHolder can handle a contracted and an expanded state. The expanded state can display more information about the Vendor such as the address or payments made to him
     */
    inner class VendorViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val customVendorCardView: ConstraintLayout = itemView.findViewById(R.id.cardLayout)
        private val transitionDuration = 300L

        init {
            customVendorCardView.setOnClickListener {
                AnalyticsManager.getInstance().trackUserInteraction(SCREEN_NAME, "customVendorCardView", "click")
                handleClick()
            }

            customVendorCardView.setOnLongClickListener {
                toggleState()
                true
            }
        }

        fun bind(vendorPayment: VendorPayment) {
            // Clear all views first
            customVendorCardView.removeAllViews()

            // Decide which view to inflate based on the expanded state
            val layoutId =
                if (vendorPayment.isExpanded) R.layout.vendor_cardview_expanded else R.layout.vendor_cardview_contracted
            val view =
                LayoutInflater.from(itemView.context).inflate(layoutId, customVendorCardView, false)

            // Common setup for both expanded and contracted views
            view.findViewById<ImageView>(R.id.vendorImageView).apply {
                val textColor = ContextCompat.getColor(context, R.color.OnSecondaryContainer_cream)
                val circleColor = ContextCompat.getColor(context, R.color.SecondaryContainer_cream)
                val density = context.resources.displayMetrics.density
                setImageDrawable(
                    LetterAvatar(
                        textColor,
                        circleColor,
                        vendorPayment.vendor.name.substring(0, 2),
                        10,
                        density
                    )
                )
            }

            // Set common fields
            view.findViewById<TextView>(R.id.vendorName).text = vendorPayment.vendor.name
            view.findViewById<TextView>(R.id.vendorCategory).text = Category.getCategoryName(vendorPayment.vendor.category)

            // Handle specific logic for expanded view
            if (vendorPayment.isExpanded) {
                if (vendorPayment.vendor.location.isNotEmpty()) {
                    view.findViewById<TextView>(R.id.vendorAddress).text =
                        vendorPayment.vendor.location
                } else {
                    view.findViewById<TextView>(R.id.vendorAddress).visibility =
                        ConstraintLayout.GONE
                }
                view.findViewById<TextView>(R.id.vendorPhone).text = vendorPayment.vendor.phone
                val amountMsg =
                    "${context.getString(R.string.paid)}: ${sumStrings(vendorPayment.amountlist)}"
                view.findViewById<TextView>(R.id.amountpaid).text = amountMsg

                setupPaymentsRecyclerView(view, vendorPayment)
            }

            // Add the prepared view
            customVendorCardView.addView(view)
            animateView(view)
        }

        private fun setupPaymentsRecyclerView(view: View, vendorPayment: VendorPayment) {
            val paymentDB = PaymentDBHelper()
            val paymentlist = paymentDB.getVendorPaymentList(vendorPayment.vendor.key)!!
            if (paymentlist.isNotEmpty()) {
                val cardRecycler = view.findViewById<RecyclerView>(R.id.PaymentsRecyclerView)
                cardRecycler.visibility = View.VISIBLE
                cardRecycler.layoutManager =
                    LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                val rvAdapter = Rv_PaymentAdapter3(paymentlist)
                cardRecycler.adapter = rvAdapter
            }
        }

        private fun animateView(view: View) {
            view.alpha = 0f // Set initial alpha to 0
            view.animate()
                .alpha(1f) // Animate alpha to 1
                .setDuration(transitionDuration)
                .start()
        }

        private fun handleClick() {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                val vendorPayment = contactlist[position]
                fragmentActionListener.onVendorFragmentWithData(vendorPayment.vendor)
            }
        }

        private fun toggleState() {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                val vendorPayment = contactlist[position]
                vendorPayment.isExpanded = !vendorPayment.isExpanded
                notifyItemChanged(position)
            }
        }
    }
}

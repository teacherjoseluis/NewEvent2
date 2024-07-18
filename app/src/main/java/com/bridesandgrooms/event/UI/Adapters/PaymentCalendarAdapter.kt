package com.bridesandgrooms.event.UI.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bridesandgrooms.event.Model.Category.Companion.getCategory
import com.bridesandgrooms.event.Model.Payment
import com.bridesandgrooms.event.Model.Task
import com.bridesandgrooms.event.R

class PaymentCalendarAdapter(
    private val paymentList: ArrayList<Payment>,
    val context: Context
) : RecyclerView.Adapter<PaymentCalendarAdapter.PaymentViewHolder>() {

    private lateinit var view: View

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PaymentViewHolder {
        view =
            LayoutInflater.from(parent.context).inflate(R.layout.task_cardview_expanded, parent, false)
        return PaymentViewHolder(view)
    }

    override fun getItemCount(): Int {
        return paymentList.size
    }

    override fun onBindViewHolder(holder: PaymentViewHolder, position: Int) {
        val payment = paymentList[position]
        holder.bind(payment)
    }

    /**
     * This particular ViewHolder can handle a contracted and an expanded state. The expanded state can display more information about the Guest such as the RSVP and companions
     */
    inner class PaymentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val paymentImageView: ImageView = itemView.findViewById(R.id.taskImageView)
        private val paymentNameTextView: TextView = itemView.findViewById(R.id.taskName)
        private val paymentCategoryTextView: TextView = itemView.findViewById(R.id.taskCategory)
        private val paymentDateTextView: TextView = itemView.findViewById(R.id.taskDate)
        private val paymentAmountTextView: TextView = itemView.findViewById(R.id.taskBudget)

        fun bind(payment: Payment) {
            val resourceId = context.resources.getIdentifier(
                getCategory(payment.category).drawable, "drawable",
                context.packageName
            )

            paymentImageView.setImageResource(resourceId)
            paymentNameTextView.text = payment.name
            paymentCategoryTextView.text = payment.category
            paymentDateTextView.text = context.getString(R.string.date_text, payment.date ?: "")
            paymentAmountTextView.text = context.getString(R.string.budget_text, payment.amount ?: "")
        }
    }
}


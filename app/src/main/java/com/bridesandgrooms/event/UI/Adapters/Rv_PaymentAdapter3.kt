package com.bridesandgrooms.event.UI.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bridesandgrooms.event.Model.Payment
import com.bridesandgrooms.event.R

class Rv_PaymentAdapter3(
    private val paymentList: MutableList<Payment>
) : RecyclerView.Adapter<Rv_PaymentAdapter3.ViewHolder>() {  // Use the specific ViewHolder class

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.cardview_payment_item_layout, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {  // Use specific ViewHolder in the parameter
        //holder.paymentname?.text = paymentList[position].name
        holder.paymentdate?.text = paymentList[position].date
        holder.paymentamount?.text = paymentList[position].amount.toString()
    }

    override fun getItemCount(): Int {
        return paymentList.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        //val paymentname: TextView? = itemView.findViewById(R.id.payment_name)
        val paymentdate: TextView? = itemView.findViewById(R.id.payment_date)
        val paymentamount: TextView? = itemView.findViewById(R.id.payment_amount)
    }

    companion object {
        const val TAG = "Rv_PaymentAdapter"
        const val SCREEN_NAME = "Rv PaymentAdapter"
    }
}

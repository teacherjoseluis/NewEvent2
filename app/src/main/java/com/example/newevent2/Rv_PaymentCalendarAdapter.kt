package com.example.newevent2

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar

class Rv_PaymentCalendarAdapter(val paymentList: MutableList<Payment>) :
        RecyclerView.Adapter<Rv_PaymentCalendarAdapter.ViewHolder>() {
        // ViewGroup - Views container

        lateinit var context: Context

        override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
            // Instantiates a layout XML file into its corresponding View objects
            val v = LayoutInflater.from(p0?.context).inflate(R.layout.payment_item_layout, p0, false)
            context = p0.context
            return ViewHolder(v)
        }

        override fun getItemCount(): Int {
            return paymentList.size
        }

        // public abstract void onBindViewHolder (VH holder, int position)
        @SuppressLint("SetTextI18n")
        @RequiresApi(Build.VERSION_CODES.O)
        override fun onBindViewHolder(p0: ViewHolder, p1: Int) {
            p0.paymentname?.text = paymentList[p1].name
            //p0.paymentdate?.text = paymentList[p1].date
            p0.paymentamount?.text = paymentList[p1].amount
            p0.paymentcategory?.text = when (paymentList[p1].category) {
                "flowers" -> "Flowers & Deco"
                "venue" -> "Venue"
                "photo" -> "Photo & Video"
                "entertainment" -> "Entertainment"
                "transport" -> "Transportation"
                "ceremony" -> "Ceremony"
                "accesories" -> "Attire & Accessories"
                "beauty" -> "Health & Beauty"
                "food" -> "Food & Drink"
                "guests" -> "Guests"
                else -> "none"
            }

            p0.itemView.setOnClickListener {
//                val paymentdetail = Intent(context, Payment_EditDetail::class.java)
//                paymentdetail.putExtra("paymentkey", paymentList[p1].key)
//                paymentdetail.putExtra("eventid", paymentList[p1].eventid)
//                paymentdetail.putExtra("name", paymentList[p1].name)
//                paymentdetail.putExtra("date", paymentList[p1].date)
//                paymentdetail.putExtra("category", paymentList[p1].category)
//                paymentdetail.putExtra("amount", paymentList[p1].amount)
//
//                context.startActivity(paymentdetail)
            }
        }


        // A ViewHolder describes an item view and metadata about its place within the RecyclerView.
        //class ViewHolder(itemView: View, context: Context) : RecyclerView.ViewHolder(itemView) {
        class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val paymentname: TextView? = itemView.findViewById<TextView>(R.id.paymentname)
            val paymentdate: TextView? = itemView.findViewById<TextView>(R.id.paymentdate)
            val paymentamount: TextView? = itemView.findViewById<TextView>(R.id.paymentamount)
            val paymentcategory: TextView? = itemView.findViewById<TextView>(R.id.paymentcategory)
        }
    }


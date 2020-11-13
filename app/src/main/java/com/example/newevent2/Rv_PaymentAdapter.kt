package com.example.newevent2

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.Registry
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.module.AppGlideModule
import com.firebase.ui.storage.images.FirebaseImageLoader
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.*


// Recyclerview - Displays a scrolling list of elements based on large datasets
// The view holder objects are managed by an adapter, which you create by extending RecyclerView.Adapter.
// The adapter creates view holders as needed. The adapter also binds the view holders to their data.
// It does this by assigning the view holder to a position, and calling the adapter's onBindViewHolder() method.

class Rv_PaymentAdapter(val paymentList: MutableList<Payment>) :
    RecyclerView.Adapter<Rv_PaymentAdapter.ViewHolder>(), ItemTouchAdapterAction {
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
        p0.paymentdate?.text = paymentList[p1].date
        p0.paymentamount?.text = paymentList[p1].amount

        p0.itemView.setOnClickListener {
            val paymentdetail = Intent(context, Payment_EditDetail::class.java)
            paymentdetail.putExtra("paymentkey", paymentList[p1].key)
            paymentdetail.putExtra("eventid", paymentList[p1].eventid)
            paymentdetail.putExtra("name", paymentList[p1].name)
            paymentdetail.putExtra("date", paymentList[p1].date)
            paymentdetail.putExtra("category", paymentList[p1].category)
            paymentdetail.putExtra("amount", paymentList[p1].amount)

            context.startActivity(paymentdetail)
        }
    }


    // A ViewHolder describes an item view and metadata about its place within the RecyclerView.
    //class ViewHolder(itemView: View, context: Context) : RecyclerView.ViewHolder(itemView) {
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val paymentname: TextView? = itemView.findViewById<TextView>(R.id.paymentname)
        val paymentdate: TextView? = itemView.findViewById<TextView>(R.id.paymentdate)
        val paymentamount: TextView? = itemView.findViewById<TextView>(R.id.paymentamount)
    }

    override fun onItemSwiftLeft(position: Int, recyclerView: RecyclerView, action: String) {
        TODO("Not yet implemented")
    }

    override fun onItemSwiftRight(position: Int, recyclerView: RecyclerView, action: String) {
        val payment = PaymentEntity()
        payment.key = paymentList[position].key
        payment.eventid = paymentList[position].eventid
        payment.name = paymentList[position].name
        payment.date = paymentList[position].date
        payment.category = paymentList[position].category
        payment.amount = paymentList[position].amount

        paymentList.removeAt(position)
        notifyItemRemoved(position)

        if (action == "delete") {
            payment.deletePayment()

            Snackbar.make(recyclerView, "Payment deleted", Snackbar.LENGTH_LONG)
                .setAction("UNDO") {
                    paymentList.add(payment)
                    notifyItemInserted(paymentList.lastIndex)
                    payment.addPayment()
                }.show()
        }
    }
}
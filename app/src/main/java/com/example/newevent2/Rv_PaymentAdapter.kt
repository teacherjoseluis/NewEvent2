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
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.*


// Recyclerview - Displays a scrolling list of elements based on large datasets
// The view holder objects are managed by an adapter, which you create by extending RecyclerView.Adapter.
// The adapter creates view holders as needed. The adapter also binds the view holders to their data.
// It does this by assigning the view holder to a position, and calling the adapter's onBindViewHolder() method.

class Rv_PaymentAdapter(val paymentList: MutableList<Payment>) : RecyclerView.Adapter<Rv_PaymentAdapter.ViewHolder>() {
    // ViewGroup - Views container

    lateinit var context: Context

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        // Instantiates a layout XML file into its corresponding View objects
        val v = LayoutInflater.from(p0?.context).inflate(R.layout.payment_item_layout, p0, false)
        context = p0.context
        //return ViewHolder(v, context);
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

        //var dateformatter = DateFormat.parse("dd / MM / yyyy")

        p0.itemView.setOnClickListener {
            //Toast.makeText(p0.itemView.context, p0.eventname?.text, Toast.LENGTH_SHORT).show()
            //val paymentdetail = Intent(context, EventDetail::class.java)
            //paymentdetail.putExtra("paymentkey", eventList[p1].key)
            //context.startActivity(taskdetail)
        }
    }


    // A ViewHolder describes an item view and metadata about its place within the RecyclerView.
    //class ViewHolder(itemView: View, context: Context) : RecyclerView.ViewHolder(itemView) {
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val paymentname: TextView? = itemView.findViewById<TextView>(R.id.paymentname)
        val paymentdate: TextView? = itemView.findViewById<TextView>(R.id.paymentdate)
        val paymentamount: TextView? = itemView.findViewById<TextView>(R.id.paymentamount)
    }
}
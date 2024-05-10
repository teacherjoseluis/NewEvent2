package com.bridesandgrooms.event

import Application.AnalyticsManager
import android.annotation.SuppressLint
import android.app.Activity
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
import androidx.recyclerview.widget.RecyclerView
import com.bridesandgrooms.event.Functions.PermissionUtils
import com.bridesandgrooms.event.Functions.RemoteConfigSingleton
import com.bridesandgrooms.event.Functions.deletePayment
import com.bridesandgrooms.event.Model.Category
import com.bridesandgrooms.event.Model.Payment
import com.bridesandgrooms.event.Model.User
import com.bridesandgrooms.event.Model.UserModel
import com.bridesandgrooms.event.UI.ItemTouchAdapterAction
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdView
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.CoroutineScope

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

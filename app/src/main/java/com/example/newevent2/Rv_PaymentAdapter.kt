package com.example.newevent2

import Application.Cache
import android.annotation.SuppressLint
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
import com.example.newevent2.Functions.addPayment
import com.example.newevent2.Functions.addTask
import com.example.newevent2.Functions.deletePayment
import com.example.newevent2.Model.*
import com.google.android.material.snackbar.Snackbar
import java.util.*

class Rv_PaymentAdapter(
    val paymentList: MutableList<Payment>
) : RecyclerView.Adapter<Rv_PaymentAdapter.ViewHolder>(), ItemTouchAdapterAction {

    lateinit var context: Context
    var paymentmodel = PaymentModel()
    lateinit var paymentdbhelper: PaymentDBHelper
    lateinit var usermodel: UserModel

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        // Instantiates a layout XML file into its corresponding View objects
        val v = LayoutInflater.from(p0?.context).inflate(R.layout.payment_item_layout, p0, false)
        context = p0.context
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return paymentList.size
    }

    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(p0: ViewHolder, p1: Int) {
        p0.paymentname?.text = paymentList[p1].name
        p0.paymentdate?.text = paymentList[p1].date
        p0.paymentamount?.text = paymentList[p1].amount
        val resourceId = context.resources.getIdentifier(
            Category.getCategory(paymentList[p1].category).drawable, "drawable",
            context.packageName
        )
        p0.categoryavatar?.setImageResource(resourceId)

        p0.itemView.setOnClickListener {
            val paymentdetail = Intent(context, PaymentCreateEdit::class.java)
            paymentdetail.putExtra("payment", paymentList[p1])
            context.startActivity(paymentdetail)
        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val paymentname: TextView? = itemView.findViewById<TextView>(R.id.paymentname)
        val paymentdate: TextView? = itemView.findViewById<TextView>(R.id.paymentdate)
        val paymentamount: TextView? = itemView.findViewById<TextView>(R.id.paymentamount)
        val categoryavatar = itemView.findViewById<ImageView>(R.id.categoryavatar)!!
    }

    override fun onItemSwiftLeft(position: Int, recyclerView: RecyclerView, action: String) {
        TODO("Not yet implemented")
    }

    override fun onItemSwiftRight(position: Int, recyclerView: RecyclerView, action: String) {
        val paymentswift = paymentList[position]
        val paymentbackup = Payment().apply {
            name = paymentswift.name
            amount = paymentswift.amount
            date = paymentswift.date
            category = paymentswift.category
            createdatetime = paymentswift.createdatetime
        }

        if (action == DELETEACTION) {
            paymentList.removeAt(position)
            notifyItemRemoved(position)
            deletePayment(context, paymentswift)

            val snackbar = Snackbar.make(recyclerView, "Payment deleted", Snackbar.LENGTH_LONG)
//                .setAction("UNDO") {
//                    paymentList.add(paymentswift)
//                    notifyItemInserted(paymentList.lastIndex)
//                    addPayment(context, paymentbackup)
//                }
            snackbar.show()
        }
    }

    companion object {
        const val TAG = "Rv_PaymentAdapter"
        const val PAYMENTENTITY = "Payment"
        const val DELETEACTION = "delete"
    }
}
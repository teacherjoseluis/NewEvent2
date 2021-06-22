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
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.example.newevent2.Model.Payment
import com.example.newevent2.Model.PaymentModel
import com.google.android.material.snackbar.Snackbar
import java.util.*

class Rv_PaymentAdapter(
    val userid: String,
    val eventid: String,
    val paymentList: MutableList<Payment>
) : RecyclerView.Adapter<Rv_PaymentAdapter.ViewHolder>(), ItemTouchAdapterAction {

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

    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(p0: ViewHolder, p1: Int) {
        p0.paymentname?.text = paymentList[p1].name
        p0.paymentdate?.text = paymentList[p1].date
        p0.paymentamount?.text = paymentList[p1].amount

        p0.itemView.setOnClickListener {
            val paymentdetail = Intent(context, PaymentCreateEdit::class.java)
            paymentdetail.putExtra("payment", paymentList[p1])
            paymentdetail.putExtra("userid", userid)
            paymentdetail.putExtra("eventid", eventid)
            context.startActivity(paymentdetail)
        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val paymentname: TextView? = itemView.findViewById<TextView>(R.id.paymentname)
        val paymentdate: TextView? = itemView.findViewById<TextView>(R.id.paymentdate)
        val paymentamount: TextView? = itemView.findViewById<TextView>(R.id.paymentamount)
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
        paymentList.removeAt(position)
        notifyItemRemoved(position)

        val usersession =
            context.getSharedPreferences("USER_SESSION", Context.MODE_PRIVATE)
        val paymentactive = usersession.getInt("payments", 0)
        val sessionEditor = usersession!!.edit()

        if (action == DELETEACTION) {
            val paymentmodel = PaymentModel()
            paymentmodel.deletePayment(
                userid,
                eventid,
                paymentswift,
                paymentactive,
                object : PaymentModel.FirebaseDeletePaymentSuccess {
                    override fun onPaymentDeleted(flag: Boolean) {
                        if (flag) {
                            //Deleting all instances of Payment from cache
                            Log.i(TAG, "Payment ${paymentswift.key} was Deleted")
                            sessionEditor.putInt("payments", paymentactive - 1)
                            sessionEditor.apply()
                            Log.d(TAG, "User has currently ${paymentactive - 1} active payments")
                            Cache.deletefromStorage(TaskCreateEdit.TASKENTITY, context)
                        }
                    }

                })

            Snackbar.make(recyclerView, "Payment deleted", Snackbar.LENGTH_LONG)
                .setAction("UNDO") {
                    paymentList.add(paymentbackup)
                    notifyItemInserted(paymentList.lastIndex)
                    paymentmodel.addPayment(
                        userid,
                        eventid,
                        paymentbackup,
                        paymentactive,
                        object : PaymentModel.FirebaseAddEditPaymentSuccess {
                            override fun onPaymentAddedEdited(flag: Boolean) {
                                if (flag) {
                                    Log.i(
                                        TAG,
                                        "Undo action and Payment ${paymentswift.key} was added back"
                                    )
                                    sessionEditor.putInt("payments", paymentactive + 1)
                                    sessionEditor.apply()
                                    Log.d(
                                        TAG,
                                        "User has currently ${paymentactive + 1} active payments"
                                    )
                                    //Deleting all instances of Payment from cache
                                    Cache.deletefromStorage(PAYMENTENTITY, context)
                                }
                            }

                        })
                }.show()
        }
    }
    
    companion object {
        const val TAG = "Rv_PaymentAdapter"
        const val PAYMENTENTITY = "Payment"
        const val DELETEACTION = "delete"
    }
}
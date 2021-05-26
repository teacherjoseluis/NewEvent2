package com.example.newevent2.Model

import android.os.Build
import androidx.annotation.RequiresApi
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.sql.Time
import java.text.SimpleDateFormat
import java.util.*

class PaymentModel {

    private var database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private var myRef = database.reference

    fun getPaymentStats(
        userid: String,
        eventid: String,
        category: String,
        dataFetched: FirebaseSuccessStatsPayment
    ) {
        var sumpayment: Float
        var countpayment: Int

        val postRef =
            myRef.child("User").child(userid).child("Event").child(eventid)
                .child("Payment")

        val paymentListenerActive = object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                countpayment = 0 // Number of Payments made
                sumpayment = 0.0f // Amount of Payments

                val re = Regex("[^A-Za-z0-9 ]")
                for (snapshot in p0.children) {
                    val paymentitem = snapshot.getValue(Payment::class.java)!!
                    if (category != "") {
                        if (paymentitem.category == category) {
                            val paidamount = re.replace(paymentitem.amount, "").dropLast(2)
                            sumpayment += paidamount.toFloat()
                            countpayment += 1
                        }
                    } else {
                        val paidamount = re.replace(paymentitem.amount, "").dropLast(2)
                        sumpayment += paidamount.toFloat()
                        countpayment += 1
                    }
                }
                dataFetched.onPaymentStats(countpayment, sumpayment)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                println("loadPost:onCancelled ${databaseError.toException()}")
            }
        }
        postRef.addValueEventListener(paymentListenerActive)
    }

    fun getPaymentsList(
        userid: String,
        eventid: String,
        dataFetched: FirebaseSuccessPaymentList
    ) {
        val postRef =
            myRef.child("User").child(userid).child("Event").child(eventid)
                .child("Payment")

        var paymentlist = ArrayList<Payment>()

        val paymentlListenerActive = object : ValueEventListener {
            @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
            override fun onDataChange(p0: DataSnapshot) {
                paymentlist.clear()

                for (snapshot in p0.children) {
                    val paymentitem = snapshot.getValue(Payment::class.java)
                    paymentitem!!.key = snapshot.key.toString()
                    paymentlist.add(paymentitem!!)
                }
                dataFetched.onPaymentList(paymentlist)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                println("loadPost:onCancelled ${databaseError.toException()}")
            }
        }
        postRef.addValueEventListener(paymentlListenerActive)
    }

    fun addPayment(userid: String, eventid: String, payment: Payment) {
        val postRef =
            myRef.child("User").child(userid).child("Event").child(eventid)
                .child("Payment").push()

        //---------------------------------------
        // Getting the time and date to record in the recently created payment
        val timestamp = Time(System.currentTimeMillis())
        val paymentdatetime = Date(timestamp.time)
        val sdf = SimpleDateFormat("MM/dd/yyyy h:mm:ss a")
        //---------------------------------------

        val paymentadd = hashMapOf(
            "name" to payment.name,
            "amount" to payment.amount,
            "date" to payment.date,
            "category" to payment.category,
            "createdatetime" to sdf.format(paymentdatetime)
        )

        postRef.setValue(paymentadd as Map<String, Any>)
            .addOnFailureListener {
            }
            .addOnSuccessListener {
            }
    }

    fun editPayment(userid: String, eventid: String, payment: Payment) {
        val postRef =
            myRef.child("User").child(userid).child("Event").child(eventid)
                .child("Payment").child(payment.key)

        val paymentedit = hashMapOf(
            "name" to payment.name,
            "amount" to payment.amount,
            "date" to payment.date,
            "category" to payment.category
        )

        postRef.setValue(paymentedit as Map<String, Any>)
            .addOnFailureListener {
            }
            .addOnSuccessListener {
            }
    }

    fun deletePayment(userid: String, eventid: String, payment: Payment) {
        val postRef =
            myRef.child("User").child(userid).child("Event").child(eventid)
                .child("Payment").child(payment.key)
                .removeValue()
    }

    interface FirebaseSuccessStatsPayment {
        fun onPaymentStats(countpayment: Int, sumpayment: Float)
    }

    interface FirebaseSuccessPaymentList {
        fun onPaymentList(list: ArrayList<Payment>)
    }
}

package com.example.newevent2.Model

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
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

    interface FirebaseSuccessStatsPayment {
        fun onPaymentStats(countpayment: Int, sumpayment: Float)
    }
}

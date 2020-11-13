package com.example.newevent2

import android.os.Build
import androidx.annotation.RequiresApi
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class PaymentEntity : Payment() {

    var database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private var myRef = database.reference

    fun getPaymentEvent(dataFetched: FirebaseSuccessListenerPayment) {
        val postRef = myRef.child("User").child("Event").child(this.eventid).child("Payment")

        var sumpayment = 0.0F

        val paymentlListenerActive = object : ValueEventListener {
            @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
            override fun onDataChange(p0: DataSnapshot) {
                val re = Regex("[^A-Za-z0-9 ]")
                for (snapshot in p0.children) {
                    val paymentitem = snapshot.getValue(Payment::class.java)!!
                    sumpayment += re.replace(paymentitem.amount, "").dropLast(2).toFloat()
                }
                dataFetched.onPaymentEvent(sumpayment)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                println("loadPost:onCancelled ${databaseError.toException()}")
            }
        }
        postRef.addValueEventListener(paymentlListenerActive)
    }

    fun getPaymentsList(dataFetched: FirebaseSuccessListenerPayment) {
        val postRef = myRef.child("User").child("Event").child(this.eventid).child("Payment")
        var paymentlist = ArrayList<Payment>()

        val paymentlListenerActive = object : ValueEventListener {
            @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
            override fun onDataChange(p0: DataSnapshot) {
                paymentlist.clear()

                for (snapshot in p0.children) {
                    val paymentitem = snapshot.getValue(Payment::class.java)

                    if (paymentitem!!.category == category) {
                        paymentitem!!.key = snapshot.key.toString()
                        paymentlist.add(paymentitem!!)
                    }
                }
                dataFetched.onPaymentList(paymentlist)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                println("loadPost:onCancelled ${databaseError.toException()}")
            }
        }
        postRef.addValueEventListener(paymentlListenerActive)
    }

    fun getPaymentStats(dataFetched: FirebaseSuccessListenerPayment) {
        var sumpayment: Float
        var countpayment: Int

        val postRef = myRef.child("User").child("Event").child(this.eventid).child("Payment")
        val paymentListenerActive = object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                countpayment = 0
                sumpayment = 0.0f

                val re = Regex("[^A-Za-z0-9 ]")
                for (snapshot in p0.children) {
                    val paymentitem = snapshot.getValue(Payment::class.java)

                    if (paymentitem!!.category == category) {
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


    fun editPayment(action: String) {
        if (action == "complete") {
            myRef.child("User").child("Event").child(this.eventid).child("Payment").child(this.key)
                .child("status").setValue("C")
        }
        if (action == "active") {
            myRef.child("User").child("Event").child(this.eventid).child("Payment").child(this.key)
                .child("status").setValue("A")
        }
    }

    fun deletePayment() {
        myRef.child("User").child("Event").child(this.eventid).child("Payment").child(this.key)
            .removeValue()
    }

    fun addPayment() {
        val postRef = myRef.child("User").child("Event").child(this.eventid).child("Payment").push()

        val payment = hashMapOf(
            "name" to name,
            "amount" to amount,
            "date" to date,
            "category" to category,
            "eventid" to eventid
        )

        postRef.setValue(payment as Map<String, Any>)
            .addOnFailureListener {
            }
            .addOnSuccessListener {
            }
    }
}


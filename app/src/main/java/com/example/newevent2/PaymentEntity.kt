package com.example.newevent2

import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.new_task_paymentdetail.*
import kotlinx.android.synthetic.main.new_task_paymentdetail.view.*
import kotlinx.android.synthetic.main.new_task_paymentdetail.view.pyamount
import kotlinx.android.synthetic.main.new_task_paymentdetail.view.pydate

class PaymentEntity : Payment() {

    var database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private var myRef = database.reference

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
            "status" to "A",
            "eventid" to eventid
        )

        postRef.setValue(payment as Map<String, Any>)
            .addOnFailureListener {
            }
            .addOnSuccessListener {
            }
    }
}


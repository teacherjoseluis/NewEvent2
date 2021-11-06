package com.example.newevent2.Model

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.newevent2.CoRAddEditPayment
import com.example.newevent2.CoRDeletePayment
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.sql.Time
import java.text.SimpleDateFormat
import java.util.*

class PaymentModel : CoRAddEditPayment, CoRDeletePayment {

    private var database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private var myRef = database.reference
    var nexthandler: CoRAddEditPayment? = null
    var nexthandlerdel: CoRDeletePayment? = null

    var userid = ""
    var eventid = ""

//    fun getPaymentStats(
//        userid: String,
//        eventid: String,
//        category: String,
//        dataFetched: FirebaseSuccessStatsPayment
//    ) {
//        var sumpayment: Float
//        var countpayment: Int
//
//        val postRef =
//            myRef.child("User").child(userid).child("Event").child(eventid)
//                .child("Payment")
//
//        val paymentListenerActive = object : ValueEventListener {
//            override fun onDataChange(p0: DataSnapshot) {
//                countpayment = 0 // Number of Payments made
//                sumpayment = 0.0f // Amount of Payments
//
//                val re = Regex("[^A-Za-z0-9 ]")
//                for (snapshot in p0.children) {
//                    val paymentitem = snapshot.getValue(Payment::class.java)!!
//                    if (category != "") {
//                        if (paymentitem.category == category) {
//                            val paidamount = re.replace(paymentitem.amount, "").dropLast(2)
//                            sumpayment += paidamount.toFloat()
//                            countpayment += 1
//                        }
//                    } else {
//                        val paidamount = re.replace(paymentitem.amount, "").dropLast(2)
//                        sumpayment += paidamount.toFloat()
//                        countpayment += 1
//                    }
//                }
//                Log.d(
//                    TAG,
//                    "Current payment stats consists of Number($countpayment), Total($sumpayment)"
//                )
//                dataFetched.onPaymentStats(countpayment, sumpayment)
//            }
//            override fun onCancelled(databaseError: DatabaseError) {
//                println("loadPost:onCancelled ${databaseError.toException()}")
//            }
//        }
//        postRef.addValueEventListener(paymentListenerActive)
//    }

    fun getPaymentsList(
        userid: String,
        eventid: String,
        dataFetched: FirebaseSuccessPaymentList
    ) {
        val postRef =
            myRef.child("User").child(userid).child("Event").child(eventid)
                .child("Payment").orderByChild("date")
        val paymentlist = ArrayList<Payment>()

        val paymentlListenerActive = object : ValueEventListener {
            @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
            override fun onDataChange(p0: DataSnapshot) {
                paymentlist.clear()

                for (snapshot in p0.children) {
                    val paymentitem = snapshot.getValue(Payment::class.java)
                    paymentitem!!.key = snapshot.key.toString()
                    paymentlist.add(paymentitem)
                }
                Log.d(TAG, "Number of payments retrieved ${paymentlist.count()}")
                dataFetched.onPaymentList(paymentlist)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                println("loadPost:onCancelled ${databaseError.toException()}")
            }
        }
        postRef.addValueEventListener(paymentlListenerActive)
    }

    private fun addPayment(
        userid: String,
        eventid: String,
        payment: Payment,
        paymentaddedflag: FirebaseAddEditPaymentSuccess
    ) {
        val postRef =
            myRef.child("User").child(userid).child("Event").child(eventid)
                .child("Payment").push()

        //---------------------------------------
        // Getting the time and date to record in the recently created payment
        val timestamp = Time(System.currentTimeMillis())
        val paymentdatetime = Date(timestamp.time)
        val sdf = SimpleDateFormat("MM/dd/yyyy h:mm:ss a")
        //---------------------------------------
        payment.createdatetime = sdf.format(paymentdatetime)

        val paymentadd = hashMapOf(
            "name" to payment.name,
            "amount" to payment.amount,
            "date" to payment.date,
            "category" to payment.category,
            "createdatetime" to payment.createdatetime,
            "vendorid" to payment.vendorid
        )

        postRef.setValue(paymentadd as Map<String, Any>)
            .addOnSuccessListener {
                payment.key = postRef.key.toString()
                paymentaddedflag.onPaymentAddedEdited(true, payment)
                Log.d(
                    TAG,
                    "Payment ${payment.name} successfully added on ${sdf.format(paymentdatetime)}"
                )
            }
            .addOnFailureListener {
                paymentaddedflag.onPaymentAddedEdited(false, payment)
                Log.e(TAG, "Payment ${payment.name} failed to be added")
            }
    }

    private fun editPayment(
        userid: String,
        eventid: String,
        payment: Payment,
        paymenteditedflag: FirebaseAddEditPaymentSuccess
    ) {
        val postRef =
            myRef.child("User").child(userid).child("Event").child(eventid)
                .child("Payment").child(payment.key)

        val paymentedit = hashMapOf(
            "name" to payment.name,
            "amount" to payment.amount,
            "date" to payment.date,
            "category" to payment.category,
            "vendorid" to payment.vendorid
        )

        postRef.setValue(paymentedit as Map<String, Any>)
            .addOnSuccessListener {
                paymenteditedflag.onPaymentAddedEdited(true, payment)
                Log.d(TAG, "Payment ${payment.name} successfully edited")
            }
            .addOnFailureListener {
                paymenteditedflag.onPaymentAddedEdited(false, payment)
                Log.e(TAG, "Payment ${payment.name} failed to be edited")
            }
    }

    private fun deletePayment(
        userid: String,
        eventid: String,
        payment: Payment,
        paymentdeletedflag: FirebaseDeletePaymentSuccess
    ) {
        val postRef =
            myRef.child("User").child(userid).child("Event").child(eventid)
                .child("Payment").child(payment.key)
                .removeValue()
                .addOnSuccessListener {
                    paymentdeletedflag.onPaymentDeleted(true, payment)
                    Log.d(TAG, "Payment ${payment.name} successfully deleted")
                }
                .addOnFailureListener {
                    paymentdeletedflag.onPaymentDeleted(false, payment)
                    Log.e(TAG, "Payment ${payment.name} failed to be deleted")
                }
    }

    override fun onAddEditPayment(payment: Payment) {
        if (payment.key == "") {
            addPayment(
                userid,
                eventid,
                payment,
                object : FirebaseAddEditPaymentSuccess {
                    override fun onPaymentAddedEdited(flag: Boolean, payment: Payment) {
                        if (flag) {
                            nexthandler?.onAddEditPayment(payment)
                        }
                    }
                })
        } else if (payment.key != "") {
            editPayment(
                userid, eventid, payment, object : FirebaseAddEditPaymentSuccess {
                    override fun onPaymentAddedEdited(flag: Boolean, payment: Payment) {
                        if (flag) {
                            nexthandler?.onAddEditPayment(payment)
                        }
                    }
                }
            )
        }
    }

    override fun onDeletePayment(payment: Payment) {
        deletePayment(
            userid,
            eventid,
            payment,
            object : FirebaseDeletePaymentSuccess {
                override fun onPaymentDeleted(flag: Boolean, payment: Payment) {
                    if (flag) {
                        nexthandlerdel?.onDeletePayment(payment)
                    }
                }
            })
    }


    interface FirebaseSuccessPaymentList {
        fun onPaymentList(list: ArrayList<Payment>)
    }

    interface FirebaseAddEditPaymentSuccess {
        fun onPaymentAddedEdited(flag: Boolean, payment: Payment)
    }

    interface FirebaseDeletePaymentSuccess {
        fun onPaymentDeleted(flag: Boolean, payment: Payment)
    }

    companion object {
        const val ACTIVEFLAG = "Y"
        const val TAG = "PaymentModel"
    }

}

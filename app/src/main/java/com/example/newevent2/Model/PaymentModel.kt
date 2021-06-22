package com.example.newevent2.Model

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.lang.Exception
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
                Log.d(
                    TAG,
                    "Current payment stats consists of Number($countpayment), Total($sumpayment)"
                )
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
                Log.d(TAG, "Number of payments retrieved ${paymentlist.count()}")
                dataFetched.onPaymentList(paymentlist)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                println("loadPost:onCancelled ${databaseError.toException()}")
            }
        }
        postRef.addValueEventListener(paymentlListenerActive)
    }

    fun getPaymentdetail(
        userid: String,
        eventid: String,
        paymentid: String,
        dataFetched: PaymentModel.FirebaseSuccessPayment
    ) {
        val postRef =
            myRef.child("User").child(userid).child("Event").child(eventid)
                .child("Payment").child(paymentid)

        val paymentListenerActive = object : ValueEventListener {
            @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
            override fun onDataChange(p0: DataSnapshot) {
                val paymentitem = p0.getValue(Payment::class.java)!!
                paymentitem!!.key = p0.key.toString()
                Log.d(TAG, "Detail retrieved for payment ${paymentitem.key}")
                dataFetched.onPayment(paymentitem)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                println("loadPost:onCancelled ${databaseError.toException()}")
            }
        }
        postRef.addValueEventListener(paymentListenerActive)
    }

    fun addPayment(
        userid: String,
        eventid: String,
        payment: Payment,
        payments: Int,
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

        val paymentadd = hashMapOf(
            "name" to payment.name,
            "amount" to payment.amount,
            "date" to payment.date,
            "category" to payment.category,
            "createdatetime" to sdf.format(paymentdatetime)
        )

        postRef.setValue(paymentadd as Map<String, Any>)
            .addOnFailureListener {
                paymentaddedflag.onPaymentAddedEdited(false)
                Log.e(TAG, "Payment ${payment.name} failed to be added")
            }
            .addOnSuccessListener {
                val usermodel = UserModel(userid)
                usermodel.editUserPaymentflag(ACTIVEFLAG)
                paymentaddedflag.onPaymentAddedEdited(true)
                usermodel.editUserAddPayment(payments + 1)
                Log.d(
                    TAG,
                    "Payment ${payment.name} successfully added on ${sdf.format(paymentdatetime)}"
                )
            }
    }

    fun editPayment(
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
            "category" to payment.category
        )

        postRef.setValue(paymentedit as Map<String, Any>)
            .addOnFailureListener {
                paymenteditedflag.onPaymentAddedEdited(false)
                Log.e(TAG, "Payment ${payment.name} failed to be edited")
            }
            .addOnSuccessListener {
                paymenteditedflag.onPaymentAddedEdited(true)
                Log.d(TAG, "Payment ${payment.name} successfully edited")
            }
    }

    fun deletePayment(
        userid: String,
        eventid: String,
        payment: Payment,
        paymentactive: Int,
        paymentdeletedflag: FirebaseDeletePaymentSuccess
    ) {
        val postRef =
            myRef.child("User").child(userid).child("Event").child(eventid)
                .child("Payment").child(payment.key)
                .removeValue()
                .addOnSuccessListener {
                    val usermodel = UserModel(userid)
                    usermodel.editUserAddPayment(paymentactive - 1)
                    if ((paymentactive - 1) == 0) usermodel.editUserPaymentflag(INACTIVEFLAG)
                    paymentdeletedflag.onPaymentDeleted(true)
                    Log.d(TAG, "Payment ${payment.name} successfully deleted")
                }
                .addOnFailureListener {
                    paymentdeletedflag.onPaymentDeleted(false)
                    Log.e(TAG, "Payment ${payment.name} failed to be deleted")
                }

    }

    interface FirebaseSuccessStatsPayment {
        fun onPaymentStats(countpayment: Int, sumpayment: Float)
    }

    interface FirebaseSuccessPayment {
        fun onPayment(payment: Payment)
    }

    interface FirebaseSuccessPaymentList {
        fun onPaymentList(list: ArrayList<Payment>)
    }

    interface FirebaseAddEditPaymentSuccess {
        fun onPaymentAddedEdited(flag: Boolean)
    }

    interface FirebaseDeletePaymentSuccess {
        fun onPaymentDeleted(flag: Boolean)
    }

    companion object {
        const val ACTIVEFLAG = "Y"
        const val INACTIVEFLAG = "N"
        const val TAG = "PaymentModel"
    }
}

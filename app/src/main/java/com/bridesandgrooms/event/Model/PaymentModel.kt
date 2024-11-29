package com.bridesandgrooms.event.Model

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.bridesandgrooms.event.Functions.CoRAddEditPayment
import com.bridesandgrooms.event.Functions.CoRDeletePayment
import com.bridesandgrooms.event.Functions.UserSessionHelper
import com.google.firebase.FirebaseException
import com.google.firebase.database.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.suspendCancellableCoroutine
import java.sql.Time
import java.text.SimpleDateFormat
import java.util.*
import kotlin.Exception
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class PaymentModel : CoRAddEditPayment, CoRDeletePayment {

    private var database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private var myRef = database.reference
    var nexthandler: CoRAddEditPayment? = null
    var nexthandlerpdel: CoRDeletePayment? = null

    @ExperimentalCoroutinesApi
    suspend fun getPayments(): ArrayList<Payment> {
        val userId =
            UserSessionHelper.getUserSession("user_id") as String
        val eventId =
            UserSessionHelper.getUserSession("event_id") as String

        val postRef =
            myRef.child("User").child(userId).child("Event").child(eventId)
                .child("Payment").orderByChild("date")
        val paymentList = ArrayList<Payment>()

        try {
            for (snapChild in postRef.awaitsSingle()?.children!!) {
                val paymentItem = snapChild.getValue(Payment::class.java)
                paymentItem!!.key = snapChild.key.toString()
                paymentList.add(paymentItem)
            }
        } catch (e: Exception) {
            Log.e(TAG, e.message.toString())
        }
        return paymentList
    }

    fun getPaymentsList(
        dataFetched: FirebaseSuccessPaymentList
    ) {
        val userId =
            UserSessionHelper.getUserSession("user_id") as String
        val eventId =
            UserSessionHelper.getUserSession("event_id") as String

        val postRef =
            myRef.child("User").child(userId).child("Event").child(eventId)
                .child("Payment").orderByChild("date")
        val paymentlist = ArrayList<Payment>()

        try {
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
        } catch (e: Exception) {
            Log.e(TAG, e.message.toString())
        }
    }

    private fun addPayment(
        payment: Payment,
        paymentaddedflag: FirebaseAddEditPaymentSuccess
    ) {
        val userId =
            UserSessionHelper.getUserSession("user_id") as String
        val eventId =
            UserSessionHelper.getUserSession("event_id") as String

        val postRef =
            myRef.child("User").child(userId).child("Event").child(eventId)
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

        try {
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
        } catch (e: Exception) {
            Log.e(TAG, e.message.toString())
        }
    }

    private fun editPayment(
        payment: Payment,
        paymenteditedflag: FirebaseAddEditPaymentSuccess
    ) {
        val userId =
            UserSessionHelper.getUserSession("user_id") as String
        val eventId =
            UserSessionHelper.getUserSession("event_id") as String

        val postRef =
            myRef.child("User").child(userId).child("Event").child(eventId)
                .child("Payment").child(payment.key)

        val paymentedit = hashMapOf(
            "name" to payment.name,
            "amount" to payment.amount,
            "date" to payment.date,
            "category" to payment.category,
            "vendorid" to payment.vendorid
        )

        try {
            postRef.setValue(paymentedit as Map<String, Any>)
                .addOnSuccessListener {
                    paymenteditedflag.onPaymentAddedEdited(true, payment)
                    Log.d(TAG, "Payment ${payment.name} successfully edited")
                }
                .addOnFailureListener {
                    paymenteditedflag.onPaymentAddedEdited(false, payment)
                    Log.e(TAG, "Payment ${payment.name} failed to be edited")
                }
        } catch (e: Exception) {
            Log.e(TAG, e.message.toString())
        }
    }

    private fun deletePayment(
        paymentId: String,
        paymentdeletedflag: FirebaseDeletePaymentSuccess
    ) {
        val userId =
            UserSessionHelper.getUserSession("user_id") as String
        val eventId =
            UserSessionHelper.getUserSession("event_id") as String

        try {
            myRef.child("User").child(userId).child("Event").child(eventId)
                .child("Payment").child(paymentId)
                .removeValue()
                .addOnSuccessListener {
                    paymentdeletedflag.onPaymentDeleted(true, paymentId)
                    Log.d(TAG, "Payment $paymentId successfully deleted")
                }
                .addOnFailureListener {
                    paymentdeletedflag.onPaymentDeleted(false, paymentId)
                    Log.e(TAG, "Payment $paymentId failed to be deleted")
                }
        } catch (e: Exception) {
            Log.e(TAG, e.message.toString())
        }
    }

    @ExperimentalCoroutinesApi
    suspend fun Query.awaitsSingle(): DataSnapshot? =
        suspendCancellableCoroutine { continuation ->
            val listener = object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    try {
                        continuation.resume(snapshot)
                    } catch (exception: Exception) {
                        continuation.resumeWithException(exception)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    val exception = when (error.toException()) {
                        is FirebaseException -> error.toException()
                        else -> Exception("The Firebase call for reference $this was cancelled")
                    }
                    continuation.resumeWithException(exception)
                }
            }
            continuation.invokeOnCancellation { this.removeEventListener(listener) }
            this.addListenerForSingleValueEvent(listener)
        }

    override fun onAddEditPayment(payment: Payment) {
        if (payment.key.isEmpty()) {
            addPayment(
                payment,
                object : FirebaseAddEditPaymentSuccess {
                    override fun onPaymentAddedEdited(flag: Boolean, payment: Payment) {
                        if (flag) {
                            nexthandler?.onAddEditPayment(payment)
                        }
                    }
                })
        } else if (payment.key.isNotEmpty()) {
            editPayment(
                payment, object : FirebaseAddEditPaymentSuccess {
                    override fun onPaymentAddedEdited(flag: Boolean, payment: Payment) {
                        if (flag) {
                            nexthandler?.onAddEditPayment(payment)
                        }
                    }
                }
            )
        }
    }

    override fun onDeletePayment(paymentId: String) {
        deletePayment(
            paymentId,
            object : FirebaseDeletePaymentSuccess {
                override fun onPaymentDeleted(flag: Boolean, paymentId: String) {
                    if (flag) {
                        nexthandlerpdel?.onDeletePayment(paymentId)
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
        fun onPaymentDeleted(flag: Boolean, paymentId: String)
    }

    companion object {
        const val ACTIVEFLAG = "Y"
        const val INACTIVEFLAG = "N"
        const val TAG = "PaymentModel"
    }
}

package com.example.newevent2

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.sql.Time
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class PaymentEntity() : Payment() {

    var database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private var myRef = database.reference
    //val usersessionlist = getUserSession(context)
    //At some point when the refactor on all of the PaymentEntity applications ends, I'll be making the call to the
    //usersession function here and avoid repeating the calling code in each of the methods of this class

    fun getPaymentEvent(context: Context, dataFetched: FirebaseSuccessListenerPayment) {
        //val postRef = myRef.child("User").child("Event").child(this.eventid).child("Payment")
        val usersessionlist = getUserSession(context)
        val postRef =
            myRef.child("User").child(usersessionlist[0]).child("Event").child(usersessionlist[3])
                .child("Payment")
                .orderByChild("createdatetime")

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

    fun getPaymentsList(context: Context, dataFetched: FirebaseSuccessListenerPayment) {
        val usersessionlist = getUserSession(context)
        val postRef =
            myRef.child("User").child(usersessionlist[0]).child("Event").child(usersessionlist[3])
                .child("Payment")
        var paymentlist = ArrayList<com.example.newevent2.Model.Payment>()

        val paymentlListenerActive = object : ValueEventListener {
            @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
            override fun onDataChange(p0: DataSnapshot) {
                paymentlist.clear()

                for (snapshot in p0.children) {
                    val paymentitem = snapshot.getValue(com.example.newevent2.Model.Payment::class.java)

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

    fun getPaymentStats(context: Context, dataFetched: FirebaseSuccessListenerPayment) {
        var sumpayment: Float
        var countpayment: Int

        val usersessionlist = getUserSession(context)
        val postRef =
            myRef.child("User").child(usersessionlist[0]).child("Event").child(usersessionlist[3])
                .child("Payment")

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

    @RequiresApi(Build.VERSION_CODES.O)
    fun getPaymentsDatesEvent(month: Int, dataFetched: FirebaseSuccessListenerPaymentCalendar) {
        val postRef = myRef.child("User").child("Event").child(this.eventid).child("Payment")
        val paymentdates = ArrayList<Date>()

        val paymentlListenerActive = object : ValueEventListener {
            @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
            override fun onDataChange(p0: DataSnapshot) {

                for (snapshot in p0.children) {
                    val paymentitem = snapshot.getValue(Payment::class.java)!!
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                        val str = paymentitem.date.split("/")
                        if (month == Integer.parseInt(str[1]) - 1) {
                            paymentdates.add(SimpleDateFormat("dd/MM/yyyy").parse(paymentitem.date))
                        }
                    }
                }
                dataFetched.onPaymentsDatesEvent(paymentdates)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                println("loadPost:onCancelled ${databaseError.toException()}")
            }
        }
        postRef.addValueEventListener(paymentlListenerActive)
    }

    fun getPaymentsperDay(calendar: Calendar, dataFetched: FirebaseSuccessListenerPaymentCalendar) {
        val postRef = myRef.child("User").child("Event").child(this.eventid).child("Payment")
        var paymentlist = ArrayList<Payment>()

        val paymentListenerActive = object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                for (snapshot in p0.children) {
                    val paymentitem = snapshot.getValue(Payment::class.java)!!
                    if (SimpleDateFormat("dd/MM/yyyy").parse(paymentitem.date) == calendar.time) {
                        paymentlist.add(paymentitem)
                    }
                }
                dataFetched.onPaymentsperDay(paymentlist)
            }

            override fun onCancelled(p0: DatabaseError) {
                TODO("Not yet implemented")
            }
        }
        postRef.addValueEventListener(paymentListenerActive)
    }

    fun getRecentCreatedPayment(
        context: Context,
        dataFetched: FirebaseSuccessListenerPaymentWelcome
    ) {
        val usersessionlist = getUserSession(context)
        //val postRef = myRef.child("User").child("Event").child(this.eventid).child("Payment")
        val postRef =
            myRef.child("User").child(usersessionlist[0]).child("Event").child(usersessionlist[3])
                .child("Payment")
                .orderByChild("createdatetime")
        val paymentListenerActive = object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                //val todaydate= SimpleDateFormat("dd/MM/yyyy").format(Calendar.getInstance().time)
                var paymentcreated = Payment()
                loop@ for (snapshot in p0.children) {
                    val paymentitem = snapshot.getValue(Payment::class.java)
                    if (paymentitem!!.createdatetime.isNotEmpty()) {
                        paymentcreated = paymentitem!!
                        break@loop
                    }
                }
                dataFetched.onPayment(paymentcreated)
            }

            override fun onCancelled(p0: DatabaseError) {
                TODO("Not yet implemented")
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

    fun editPayment(context: Context) {
        val usersessionlist = getUserSession(context)
        //val postRef = myRef.child("User").child("Event").child(this.eventid).child("Task")
        val postRef =
            myRef.child("User").child(usersessionlist[0]).child("Event").child(usersessionlist[3])
                .child("Payment").child(this.key)

        val payment = hashMapOf(
            "name" to name,
            "amount" to amount,
            "date" to date,
            "category" to category
        )

        postRef.setValue(payment as Map<String, Any>)
            .addOnFailureListener {
                //return@addOnFailureListener
            }
            .addOnSuccessListener {
                //saveLog(context, "UPDATE", "payment", key, name)
                //return@addOnSuccessListener
            }
    }

    fun deletePayment(context: Context) {
        val usersessionlist = getUserSession(context)
        //val postRef = myRef.child("User").child("Event").child(this.eventid).child("Payment")
        val postRef =
            myRef.child("User").child(usersessionlist[0]).child("Event").child(usersessionlist[3])
                .child("Payment").child(this.key)
            .removeValue()
    }

    fun addPayment(context: Context) {
        val usersessionlist = getUserSession(context)
        //val postRef = myRef.child("User").child("Event").child(this.eventid).child("Payment")
        val postRef =
            myRef.child("User").child(usersessionlist[0]).child("Event").child(usersessionlist[3])
                .child("Payment").push()

        //---------------------------------------
        // Getting the time and date to record in the recently created payment
        val timestamp = Time(System.currentTimeMillis())
        val paymentdatetime = Date(timestamp.time)
        val sdf = SimpleDateFormat("MM/dd/yyyy h:mm:ss a")
        //---------------------------------------


        val payment = hashMapOf(
            "name" to name,
            "amount" to amount,
            "date" to date,
            "category" to category,
            //"eventid" to eventid,
            "createdatetime" to sdf.format(paymentdatetime)
        )

        postRef.setValue(payment as Map<String, Any>)
            .addOnFailureListener {
            }
            .addOnSuccessListener {
                val paymentkey = postRef.key.toString()
                //saveLog(context, "INSERT", "payment", paymentkey, name)
            }
    }
}


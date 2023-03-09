package com.example.newevent2.Model

import Application.FirebaseDataImportException
import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import com.example.newevent2.CoRAddEditPayment
import com.example.newevent2.CoRDeletePayment
import kotlinx.coroutines.ExperimentalCoroutinesApi
import java.text.DecimalFormat

class PaymentDBHelper(context: Context) : CoRAddEditPayment, CoRDeletePayment {

    private val db: SQLiteDatabase = DatabaseHelper(context).writableDatabase
    lateinit var payment: Payment
    var key = ""
    var nexthandler: CoRAddEditPayment? = null
    var nexthandlerpdel: CoRDeletePayment? = null

    @ExperimentalCoroutinesApi
    suspend fun firebaseImport(userid: String) : Boolean {
        val paymentList: ArrayList<Payment>
        val eventModel = EventModel()
        try {
            val eventKey = eventModel.getEventKey(userid)
            val paymentModel = PaymentModel()

            paymentList = paymentModel.getPayments(userid, eventKey)
            db.execSQL("DELETE FROM PAYMENT")

            for (paymentItem in paymentList){
                insert(paymentItem)
            }
        } catch (e: Exception){
            println(e.message)
            throw FirebaseDataImportException("Error importing Payment data: $e")
        }
        return true
    }

    fun insert(payment: Payment) {
        val values = ContentValues()
        values.put("paymentid", payment.key)
        values.put("name", payment.name)
        values.put("date", payment.date)
        values.put("category", payment.category)
        values.put("amount", payment.amount)
        values.put("eventid", payment.eventid)
        values.put("createdatetime", payment.createdatetime)
        values.put("vendorid", payment.vendorid)
        db.insert("PAYMENT", null, values)
        Log.d(TAG, "Payment record inserted")
    }

    private fun getPaymentexists(key: String): Boolean {
        var existsflag = false
        val cursor: Cursor = db.rawQuery("SELECT * FROM PAYMENT WHERE paymentid = '$key'", null)
        if (cursor != null) {
            if (cursor.count > 0) {
                existsflag = true
            }
        }
        cursor.close()
        return existsflag
    }

    fun getPayments(): ArrayList<Payment> {
        val list = ArrayList<Payment>()
        val cursor: Cursor = db.rawQuery("SELECT * FROM PAYMENT ORDER BY createdatetime DESC", null)
        if (cursor != null) {
            if (cursor.count > 0) {
                cursor.moveToFirst()
                do {
                    val paymentid = cursor.getString(cursor.getColumnIndex("paymentid"))
                    val name = cursor.getString(cursor.getColumnIndex("name"))
                    val date = cursor.getString(cursor.getColumnIndex("date"))
                    val category = cursor.getString(cursor.getColumnIndex("category"))
                    val amount = cursor.getString(cursor.getColumnIndex("amount"))
                    val eventid = cursor.getString(cursor.getColumnIndex("eventid"))
                    val createdatetime = cursor.getString(cursor.getColumnIndex("createdatetime"))
                    val vendorid = cursor.getString(cursor.getColumnIndex("vendorid"))
                    val payment =
                        Payment(
                            paymentid,
                            name,
                            date,
                            category,
                            amount,
                            eventid,
                            createdatetime,
                            vendorid
                        )
                    list.add(payment)
                    Log.d(TAG, "Task $paymentid record obtained from local DB")
                } while (cursor.moveToNext())
            }
        }
        cursor.close()
        return list
    }

    fun getVendorPayments(vendorkey: String): ArrayList<Float> {
        val list = ArrayList<Float>()
        val cursor: Cursor =
            db.rawQuery("SELECT amount FROM PAYMENT where vendorid ='$vendorkey'", null)
        if (cursor != null) {
            if (cursor.count > 0) {
                cursor.moveToFirst()
                val re = Regex("[^A-Za-z0-9 ]")
                do {
                    val amount = cursor.getString(cursor.getColumnIndex("amount"))
                    val amountdeformatted = re.replace(amount, "").dropLast(2)
                    list.add(amountdeformatted.toFloat())
                    Log.d(TAG, "Amount $amount record obtained for vendor $vendorkey")
                } while (cursor.moveToNext())
            }
        }
        cursor.close()
        return list
    }

    fun getVendorPaymentList(vendorkey: String): ArrayList<Payment> {
        val list = ArrayList<Payment>()
        val cursor: Cursor =
            db.rawQuery("SELECT * FROM PAYMENT where vendorid ='$vendorkey'", null)
        if (cursor != null) {
            if (cursor.count > 0) {
                cursor.moveToFirst()
                do {
                    val paymentid = cursor.getString(cursor.getColumnIndex("paymentid"))
                    val name = cursor.getString(cursor.getColumnIndex("name"))
                    val date = cursor.getString(cursor.getColumnIndex("date"))
                    val category = cursor.getString(cursor.getColumnIndex("category"))
                    val amount = cursor.getString(cursor.getColumnIndex("amount"))
                    val eventid = cursor.getString(cursor.getColumnIndex("eventid"))
                    val createdatetime = cursor.getString(cursor.getColumnIndex("createdatetime"))
                    val vendorid = cursor.getString(cursor.getColumnIndex("vendorid"))
                    val payment =
                        Payment(
                            paymentid,
                            name,
                            date,
                            category,
                            amount,
                            eventid,
                            createdatetime,
                            vendorid
                        )
                    list.add(payment)
                    Log.d(TAG, "Task $paymentid record obtained from local DB")
                } while (cursor.moveToNext())
            }
        }
        cursor.close()
        return list
    }

    @SuppressLint("Range")
    fun getCategoryStats(category: String): PaymentStatsToken {
        val paymentstats = PaymentStatsToken()
        var sumpayments = 0.0F
        var cursor: Cursor = db.rawQuery(
            "SELECT COUNT(*) as paymentcompleted FROM PAYMENT WHERE category='$category'",
            null
        )
        if (cursor.count > 0) {
            cursor.moveToFirst()
            do {
                paymentstats.paymentcompleted =
                    cursor.getInt(cursor.getColumnIndex("paymentcompleted"))
            } while (cursor.moveToNext())
        }
        cursor =
            db.rawQuery("SELECT amount FROM PAYMENT WHERE category='$category'", null)
        if (cursor != null) {
            if (cursor.count > 0) {
                cursor.moveToFirst()
                val re = Regex("[^A-Za-z0-9 ]")
                do {
                    val payment = cursor.getString(cursor.getColumnIndex("amount"))
                    val paymentamount = re.replace(payment, "").dropLast(2)
                    sumpayments += paymentamount.toFloat()
                } while (cursor.moveToNext())
                val formatter = DecimalFormat("$#,###.00")
                paymentstats.sumpayments = formatter.format(sumpayments)
            }
        }
        cursor.close()
        return paymentstats
    }

    fun hasVendorPayments(vendorkey: String): Int {
        var paymentcount = 0
        val cursor: Cursor = db.rawQuery(
            "SELECT COUNT(*) as vendorpayment FROM PAYMENT where vendorid ='$vendorkey'",
            null
        )
        if (cursor != null) {
            if (cursor.count > 0) {
                cursor.moveToFirst()
                do {
                    paymentcount = cursor.getInt(cursor.getColumnIndex("vendorpayment"))
                } while (cursor.moveToNext())
            }
        }
        cursor.close()
        return paymentcount
    }

    fun update(payment: Payment) {
        val values = ContentValues()
        values.put("paymentid", payment.key)
        values.put("name", payment.name)
        values.put("date", payment.date)
        values.put("category", payment.category)
        values.put("amount", payment.amount)
        values.put("eventid", payment.eventid)
        values.put("createdatetime", payment.createdatetime)
        values.put("vendorid", payment.vendorid)

        val retVal = db.update("PAYMENT", values, "paymentid = '${payment.key}'", null)
        if (retVal >= 1) {
            Log.d(TAG, "Payment ${payment.key} updated")
        } else {
            Log.d(TAG, "Payment ${payment.key} not updated")
        }
        //db.close()
    }

    fun delete(payment: Payment) {
        val retVal = db.delete("PAYMENT", "paymentid = '${payment.key}'", null)
        if (retVal >= 1) {
            Log.d(TAG, "Payment ${payment.key} deleted")
        } else {
            Log.d(TAG, "Payment ${payment.key} not deleted")
        }
        //db.close()
    }

    override fun onAddEditPayment(payment: Payment) {
        if (!getPaymentexists(payment.key)) {
            insert(payment)
        } else {
            update(payment)
        }
        nexthandler?.onAddEditPayment(payment)
    }

    override fun onDeletePayment(payment: Payment) {
        delete(payment)
        nexthandlerpdel?.onDeletePayment(payment)
    }

    companion object {
        const val TAG = "PaymentDBHelper"
    }
}

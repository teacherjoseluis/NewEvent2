package com.example.newevent2.Model

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.example.newevent2.rvCategoryAdapter
import java.lang.Exception

class PaymentDBHelper(context: Context) {

    private val db: SQLiteDatabase = DatabaseHelper(context).writableDatabase

    fun insert(payment: Payment) {
        var values = ContentValues()
        values.put("paymentid", payment.key)
        values.put("name", payment.name)
        values.put("date", payment.date)
        values.put("category", payment.category)
        values.put("amount", payment.amount)
        values.put("eventid", payment.eventid)
        values.put("createdatetime", payment.createdatetime)
        db.insert("PAYMENT", null, values)
        Log.d(TAG, "Payment record inserted")
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
                    val payment =
                        Payment(paymentid, name, date, category, amount, eventid, createdatetime)
                    list.add(payment)
                    Log.d(TAG, "Task $paymentid record obtained from local DB")
                } while (cursor.moveToNext())
            }
        }
        return list
    }

    fun update(payment: Payment) {
        var values = ContentValues()
        values.put("paymentid", payment.key)
        values.put("name", payment.name)
        values.put("date", payment.date)
        values.put("category", payment.category)
        values.put("amount", payment.amount)
        values.put("eventid", payment.eventid)
        values.put("createdatetime", payment.createdatetime)

        val retVal = db.update("PAYMENT", values, "paymentid = ${payment.key}'",  null)
        if (retVal >= 1) {
            Log.d(TAG, "Payment ${payment.key} updated")
        } else {
            Log.d(TAG, "Payment ${payment.key} not updated")
        }
        db.close()
    }

    fun delete(payment: Payment) {
        val retVal = db.delete("PAYMENT", "paymentid = ${payment.key}'", null)
        if (retVal >= 1) {
            Log.d(TAG, "Payment ${payment.key} deleted")
        } else {
            Log.d(TAG, "Payment ${payment.key} not deleted")
        }
        db.close()
    }

    companion object {
        const val TAG = "PaymentDBHelper"
    }
}

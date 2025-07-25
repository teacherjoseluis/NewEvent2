package com.bridesandgrooms.event.Model

import Application.FirebaseDataImportException
import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import com.applandeo.materialcalendarview.CalendarDay
import com.bridesandgrooms.event.Functions.CoRAddEditPayment
import com.bridesandgrooms.event.Functions.CoRDeletePayment
import com.bridesandgrooms.event.Functions.convertToDBString
import kotlinx.coroutines.ExperimentalCoroutinesApi
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Date
import java.util.Locale

class PaymentDBHelper : CoRAddEditPayment, CoRDeletePayment {

    var nexthandler: CoRAddEditPayment? = null
    var nexthandlerpdel: CoRDeletePayment? = null

    @ExperimentalCoroutinesApi
    suspend fun firebaseImport(uid: String): Boolean {
        val db: SQLiteDatabase = DatabaseHelper.getInstance().writableDatabase
        val paymentList: ArrayList<Payment>
        val eventModel = EventModel()
        try {
            val eventKey = eventModel.getEventKey(uid)
            val paymentModel = PaymentModel()

            paymentList = paymentModel.getPayments()
            db.execSQL("DELETE FROM PAYMENT")

            for (paymentItem in paymentList) {
                insert(paymentItem)
            }
        } catch (e: Exception) {
            println(e.message)
            throw FirebaseDataImportException("Error importing Payment data: $e")
        } finally {
            db.close()
        }
        return true
    }

    fun insert(payment: Payment) {
        val db: SQLiteDatabase = DatabaseHelper.getInstance().writableDatabase
        val values = ContentValues()
        values.put("paymentid", payment.key)
        values.put("name", payment.name)
        values.put("date", payment.date)
        values.put("category", payment.category)
        values.put("amount", payment.amount)
        values.put("eventid", payment.eventid)
        values.put("createdatetime", payment.createdatetime)
        values.put("vendorid", payment.vendorid)
        try {
            db.insert("PAYMENT", null, values)
            Log.d(TAG, "Payment record inserted")
        } catch (e: Exception) {
            Log.e(TAG, e.message.toString())
        } finally {
            db.close()
        }
    }

    private fun getPaymentexists(key: String): Boolean {
        val db: SQLiteDatabase = DatabaseHelper.getInstance().writableDatabase
        var existsflag = false
        try {
            val cursor: Cursor = db.rawQuery("SELECT * FROM PAYMENT WHERE paymentid = '$key'", null)
            if (cursor.count > 0) {
                existsflag = true
            }
            cursor.close()
            return existsflag
        } catch (e: Exception) {
            Log.e(TAG, e.message.toString())
            return false
        } finally {
            db.close()
        }
    }

    fun getPayments(): ArrayList<Payment>? {
        val db: SQLiteDatabase = DatabaseHelper.getInstance().writableDatabase
        val list = ArrayList<Payment>()
        try {
            val cursor: Cursor =
                db.rawQuery("SELECT * FROM PAYMENT ORDER BY createdatetime DESC", null)
            if (cursor.count > 0) {
                cursor.moveToFirst()
                do {
                    val paymentid = cursor.getString(cursor.getColumnIndexOrThrow("paymentid"))
                    val name = cursor.getString(cursor.getColumnIndexOrThrow("name"))
                    val date = cursor.getString(cursor.getColumnIndexOrThrow("date"))
                    val category = cursor.getString(cursor.getColumnIndexOrThrow("category"))
                    val amount = cursor.getString(cursor.getColumnIndexOrThrow("amount"))
                    val eventid = cursor.getString(cursor.getColumnIndexOrThrow("eventid"))
                    val createdatetime =
                        cursor.getString(cursor.getColumnIndexOrThrow("createdatetime"))
                    val vendorid = cursor.getString(cursor.getColumnIndexOrThrow("vendorid"))
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
            cursor.close()
            return list
        } catch (e: Exception) {
            Log.e(TAG, e.message.toString())
            return null
//        } finally {
//            db.close()
        }
    }

    fun getVendorPayments(vendorkey: String): ArrayList<Float>? {
        val db: SQLiteDatabase = DatabaseHelper.getInstance().writableDatabase
        val list = ArrayList<Float>()
        try {
            val cursor: Cursor =
                db.rawQuery("SELECT amount FROM PAYMENT where vendorid ='$vendorkey'", null)
            if (cursor.count > 0) {
                cursor.moveToFirst()
                val re = Regex("[^A-Za-z0-9 ]")
                do {
                    val amount = cursor.getString(cursor.getColumnIndexOrThrow("amount"))
                    val amountdeformatted = re.replace(amount, "").dropLast(2)
                    list.add(amountdeformatted.toFloat())
                    Log.d(TAG, "Amount $amount record obtained for vendor $vendorkey")
                } while (cursor.moveToNext())
            }
            cursor.close()
            return list
        } catch (e: Exception) {
            Log.e(TAG, e.message.toString())
            return null
//        } finally {
//            db.close()
        }
    }

    fun getVendorPaymentList(vendorkey: String): ArrayList<Payment>? {
        val db: SQLiteDatabase = DatabaseHelper.getInstance().writableDatabase
        val list = ArrayList<Payment>()
        try {
            val cursor: Cursor =
                db.rawQuery("SELECT * FROM PAYMENT where vendorid ='$vendorkey'", null)
            if (cursor.count > 0) {
                cursor.moveToFirst()
                do {
                    val paymentid = cursor.getString(cursor.getColumnIndexOrThrow("paymentid"))
                    val name = cursor.getString(cursor.getColumnIndexOrThrow("name"))
                    val date = cursor.getString(cursor.getColumnIndexOrThrow("date"))
                    val category = cursor.getString(cursor.getColumnIndexOrThrow("category"))
                    val amount = cursor.getString(cursor.getColumnIndexOrThrow("amount"))
                    val eventid = cursor.getString(cursor.getColumnIndexOrThrow("eventid"))
                    val createdatetime =
                        cursor.getString(cursor.getColumnIndexOrThrow("createdatetime"))
                    val vendorid = cursor.getString(cursor.getColumnIndexOrThrow("vendorid"))
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
            cursor.close()
            return list
        } catch (e: Exception) {
            Log.e(TAG, e.message.toString())
            return null
//        } finally {
//            db.close()
        }
    }

    fun getPaymentfromDate(date: Date) : ArrayList<String>? {
        val db: SQLiteDatabase = DatabaseHelper.getInstance().writableDatabase
        val dateString = convertToDBString(date)
        val nameList = arrayListOf<String>()
        try {
            val cursor: Cursor = db.rawQuery(
                "SELECT name FROM PAYMENT WHERE paymentid IS NOT NULL AND paymentid !='' AND date='$dateString' ORDER BY createdatetime DESC",
                null
            )
            if (cursor.count > 0) {
                cursor.moveToFirst()
                do {
                    val name = cursor.getString(cursor.getColumnIndexOrThrow("name"))
                    nameList.add(name)
                } while (cursor.moveToNext())
            }
            cursor.close()
            return nameList
        } catch (e: Exception) {
            Log.e(TAG, e.message.toString())
            return null
//        } finally {
//            db.close()
        }
    }

    @SuppressLint("Range")
    fun getDatePaymentArray(date: Date) : ArrayList<Payment>? {
        val db: SQLiteDatabase = DatabaseHelper.getInstance().writableDatabase
        val dateString = convertToDBString(date)
        val list = arrayListOf<Payment>()
        try {
            val cursor: Cursor = db.rawQuery(
                "SELECT * FROM PAYMENT WHERE paymentid IS NOT NULL AND paymentid !='' AND date='$dateString' ORDER BY createdatetime DESC",
                null
            )
            if (cursor.count > 0) {
                cursor.moveToFirst()
                do {
                    val paymentid = cursor.getString(cursor.getColumnIndexOrThrow("paymentid"))
                    val name = cursor.getString(cursor.getColumnIndexOrThrow("name"))
                    val date = cursor.getString(cursor.getColumnIndexOrThrow("date"))
                    val category = cursor.getString(cursor.getColumnIndexOrThrow("category"))
                    val amount = cursor.getString(cursor.getColumnIndexOrThrow("amount"))
                    val eventid = cursor.getString(cursor.getColumnIndexOrThrow("eventid"))
                    val createdatetime =
                        cursor.getString(cursor.getColumnIndexOrThrow("createdatetime"))
                    val vendorid = cursor.getString(cursor.getColumnIndexOrThrow("vendorid"))
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
                } while (cursor.moveToNext())
            }
            cursor.close()
            return list
        } catch (e: Exception) {
            Log.e(TAG, e.message.toString())
            return null
//        } finally {
//            db.close()
        }
    }

    fun getPaymentsFromMonthYear(month: Int, year: Int): List<Date>? {
        val db: SQLiteDatabase = DatabaseHelper.getInstance().readableDatabase
        val calendarDayList = mutableListOf<Date>()

        try {
            // Calculate start and end dates for the given month and year
            val startDate = LocalDate.of(year, month + 1, 1) // month is zero-based, so we add 1
            val endDate = startDate.plusMonths(1).minusDays(1) // end of the month
            val dateFormat = DateTimeFormatter.ofPattern("d/MM/yyyy", Locale.getDefault())

            val cursor: Cursor = db.rawQuery(
                "SELECT DISTINCT date FROM PAYMENT " +
                        "WHERE paymentid IS NOT NULL AND paymentid != '' " +
                        "AND date BETWEEN '${startDate.format(dateFormat)}' AND '${endDate.format(dateFormat)}' " +// Assuming month has 31 days max
                        "ORDER BY date ASC",
                null
            )

            if (cursor.count > 0) {
                cursor.moveToFirst()
                do {
                    val dateString = cursor.getString(cursor.getColumnIndexOrThrow("date"))

                    // Define your date format matching "d/m/yyyy"
                    val dateFormat = SimpleDateFormat("d/M/yyyy", Locale.getDefault())
                    val date = dateFormat.parse(dateString)
                    calendarDayList.add(date)
                } while (cursor.moveToNext())
            }
            cursor.close()
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching payments from month and year: ${e.message}")
            return null
//        } finally {
//            db.close()
        }
        return calendarDayList
    }

    @SuppressLint("Range")
    fun getCategoryStats(category: String): PaymentStatsToken? {
        val db: SQLiteDatabase = DatabaseHelper.getInstance().writableDatabase
        val paymentstats = PaymentStatsToken()
        var sumpayments = 0.0F
        try {
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
        } catch (e: Exception) {
            Log.e(TAG, e.message.toString())
            return null
//        } finally {
//            db.close()
        }
    }

    fun hasVendorPayments(vendorkey: String): Int? {
        val db: SQLiteDatabase = DatabaseHelper.getInstance().writableDatabase
        var paymentcount = 0
        try {
            val cursor: Cursor = db.rawQuery(
                "SELECT COUNT(*) as vendorpayment FROM PAYMENT where vendorid ='$vendorkey'",
                null
            )
            if (cursor.count > 0) {
                cursor.moveToFirst()
                do {
                    paymentcount = cursor.getInt(cursor.getColumnIndexOrThrow("vendorpayment"))
                } while (cursor.moveToNext())
            }
            cursor.close()
            return paymentcount
        } catch (e: Exception) {
            Log.e(TAG, e.message.toString())
            return null
//        } finally {
//            db.close()
        }
    }

    fun update(payment: Payment) {
        val db: SQLiteDatabase = DatabaseHelper.getInstance().writableDatabase
        val values = ContentValues()
        values.put("paymentid", payment.key)
        values.put("name", payment.name)
        values.put("date", payment.date)
        values.put("category", payment.category)
        values.put("amount", payment.amount)
        values.put("eventid", payment.eventid)
        values.put("createdatetime", payment.createdatetime)
        values.put("vendorid", payment.vendorid)

        try {
            val retVal = db.update("PAYMENT", values, "paymentid = '${payment.key}'", null)
            if (retVal >= 1) {
                Log.d(TAG, "Payment ${payment.key} updated")
            } else {
                Log.d(TAG, "Payment ${payment.key} not updated")
            }
            //db.close()
        } catch (e: Exception) {
            Log.e(TAG, e.message.toString())
//        } finally {
//            db.close()
        }
    }

    fun delete(paymentId: String) {
        val db: SQLiteDatabase = DatabaseHelper.getInstance().writableDatabase
        try {
            val retVal = db.delete("PAYMENT", "paymentid = '$paymentId'", null)
            if (retVal >= 1) {
                Log.d(TAG, "Payment $paymentId deleted")
            } else {
                Log.d(TAG, "Payment $paymentId not deleted")
            }
            //db.close()
        } catch (e: Exception) {
            Log.e(TAG, e.message.toString())
//        } finally {
//            db.close()
        }
    }

    override fun onAddEditPayment(payment: Payment) {
        if (!getPaymentexists(payment.key)) {
            insert(payment)
        } else {
            update(payment)
        }
        nexthandler?.onAddEditPayment(payment)
    }

    override fun onDeletePayment(paymentId: String) {
        delete(paymentId)
        nexthandlerpdel?.onDeletePayment(paymentId)
    }

    companion object {
        const val TAG = "PaymentDBHelper"
    }
}

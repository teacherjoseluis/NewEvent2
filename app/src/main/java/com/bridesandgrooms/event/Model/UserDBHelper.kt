package com.bridesandgrooms.event.Model

import Application.UserCreationException
import Application.UserEditionException
import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import com.bridesandgrooms.event.Functions.CoRAddEditGuest
import com.bridesandgrooms.event.Functions.CoRAddEditPayment
import com.bridesandgrooms.event.Functions.CoRAddEditTask
import com.bridesandgrooms.event.Functions.CoRAddEditUser
import com.bridesandgrooms.event.Functions.CoRAddEditVendor
import com.bridesandgrooms.event.Functions.CoRDeleteGuest
import com.bridesandgrooms.event.Functions.CoRDeletePayment
import com.bridesandgrooms.event.Functions.CoRDeleteTask
import com.bridesandgrooms.event.Functions.CoRDeleteVendor
import com.bridesandgrooms.event.Functions.CoROnboardUser
import com.bridesandgrooms.event.Functions.UserSessionHelper

class UserDBHelper : CoRAddEditUser, CoRAddEditTask, CoRDeleteTask,
    CoRAddEditPayment, CoRDeletePayment, CoRAddEditGuest,
    CoRDeleteGuest, CoRAddEditVendor, CoRDeleteVendor, CoROnboardUser {

    private var nexthandleru: CoRAddEditUser? = null
    private var nexthandlerv: CoRAddEditVendor? = null
    private var nexthandlerdelv: CoRDeleteVendor? = null

    var nexthandlert: CoRAddEditTask? = null
    var nexthandlerdelt: CoRDeleteTask? = null
    var nexthandlerp: CoRAddEditPayment? = null
    var nexthandlerpdel: CoRDeletePayment? = null
    var nexthandlerg: CoRAddEditGuest? = null
    var nexthandlerdelg: CoRDeleteGuest? = null
    var nexthandleron: CoROnboardUser? = null

    fun editUserStatus(status: String){
        val userId =
            UserSessionHelper.getUserSession("user_id") as String

        val user = getUser(userId)!!
        user.status = status
        update(user)
    }

    fun editUserEvent(eventId: String){
        val userId =
            UserSessionHelper.getUserSession("user_id") as String

        val user = getUser(userId)!!
        user.eventid = eventId
        update(user)
    }

    private fun insert(user: User?) {
        val db: SQLiteDatabase = DatabaseHelper.getInstance().writableDatabase
        val values = ContentValues()
        values.put("userid", user!!.userid)
        values.put("eventid", user.eventid)
        values.put("shortname", user.shortname)
        values.put("email", user.email)
        values.put("country", user.country)
        values.put("language", user.language)
        values.put("createdatetime", user.createdatetime)
        values.put("authtype", user.authtype)
        values.put("imageurl", user.imageurl)
        values.put("role", user.role)
        values.put("hasevent", "Y")
        values.put("hastask", user.hastask)
        values.put("haspayment", user.haspayment)
        values.put("hasguest", user.hasguest)
        values.put("hasvendor", user.hasvendor)
        values.put("tasksactive", user.tasksactive)
        values.put("taskscompleted", user.taskscompleted)
        values.put("payments", user.payments)
        values.put("guests", user.guests)
        values.put("status", user.status)
        values.put("vendors", user.vendors)
        values.put("eventbudget", user.eventbudget)
        values.put("numberguests", user.numberguests)
        values.put("distanceunit", user.distanceunit)
        try {
            db.insert("USER", null, values)
            Log.d(TAG, "User record inserted")
        } catch (e: Exception) {
            throw UserCreationException(e.toString())
//        } finally {
//            db.close()
        }
    }

    private fun getUserexists(userId: String): Boolean {
        val db: SQLiteDatabase = DatabaseHelper.getInstance().writableDatabase
        var existsflag = false
        try {
            val cursor: Cursor = db.rawQuery("SELECT * FROM USER WHERE userid = '$userId'", null)
            if (cursor.count > 0) {
                existsflag = true
            }
            cursor.close()
            return existsflag
        } catch (e: Exception) {
            Log.e(TAG, e.message.toString())
            return false
//        } finally {
//            db.close()
        }
    }

    fun getUser(userId: String?): User? {
        val db: SQLiteDatabase = DatabaseHelper.getInstance().writableDatabase
        var user = User()
        try {
            val cursor: Cursor = db.rawQuery("SELECT * FROM USER WHERE userid = '$userId'", null)
            if (cursor.count > 0) {
                cursor.moveToFirst()
                do {
                    val userid = cursor.getString(cursor.getColumnIndexOrThrow("userid"))
                    val eventid = cursor.getString(cursor.getColumnIndexOrThrow("eventid"))
                    val shortname = cursor.getString(cursor.getColumnIndexOrThrow("shortname"))
                    val email = cursor.getString(cursor.getColumnIndexOrThrow("email"))
                    val country = cursor.getString(cursor.getColumnIndexOrThrow("country"))
                    val language = cursor.getString(cursor.getColumnIndexOrThrow("language"))
                    val createdatetime =
                        cursor.getString(cursor.getColumnIndexOrThrow("createdatetime"))
                    val authtype = cursor.getString(cursor.getColumnIndexOrThrow("authtype"))
                    val status = cursor.getString(cursor.getColumnIndexOrThrow("status"))
                    val imageurl = cursor.getString(cursor.getColumnIndexOrThrow("imageurl"))
                    val role = cursor.getString(cursor.getColumnIndexOrThrow("role"))
                    val hasevent = cursor.getString(cursor.getColumnIndexOrThrow("hasevent"))
                    val hastask = cursor.getString(cursor.getColumnIndexOrThrow("hastask"))
                    val haspayment = cursor.getString(cursor.getColumnIndexOrThrow("haspayment"))
                    val hasguest = cursor.getString(cursor.getColumnIndexOrThrow("hasguest"))
                    val hasvendor = cursor.getString(cursor.getColumnIndexOrThrow("hasvendor"))
                    val tasksactive = cursor.getInt(cursor.getColumnIndexOrThrow("tasksactive"))
                    val taskscompleted =
                        cursor.getInt(cursor.getColumnIndexOrThrow("taskscompleted"))
                    val payments = cursor.getInt(cursor.getColumnIndexOrThrow("payments"))
                    val guests = cursor.getInt(cursor.getColumnIndexOrThrow("guests"))
                    val vendors = cursor.getInt(cursor.getColumnIndexOrThrow("vendors"))
                    val eventbudget = cursor.getString(cursor.getColumnIndexOrThrow("eventbudget"))
                    val numberguests = cursor.getInt(cursor.getColumnIndexOrThrow("numberguests"))
                    val distanceunit = cursor.getString(cursor.getColumnIndexOrThrow("distanceunit"))
                    user =
                        User(
                            userid,
                            eventid,
                            shortname,
                            email,
                            country,
                            language,
                            createdatetime,
                            authtype,
                            status,
                            imageurl,
                            role,
                            hasevent,
                            hastask,
                            haspayment,
                            hasguest,
                            hasvendor,
                            tasksactive,
                            taskscompleted,
                            payments,
                            guests,
                            vendors,
                            eventbudget,
                            numberguests,
                            distanceunit
                        )
                    Log.d(TAG, "User $userid record obtained from local DB")
                } while (cursor.moveToNext())
            }
            cursor.close()
            return user
        } catch (e: Exception) {
            Log.e(TAG, e.message.toString())
            return null
//        } finally {
//            db.close()
        }
    }

    private fun update(user: User) {
        val db: SQLiteDatabase = DatabaseHelper.getInstance().writableDatabase
        val values = ContentValues()
        values.put("userid", user.userid)
        values.put("eventid", user.eventid)
        values.put("shortname", user.shortname)
        values.put("email", user.email)
        values.put("country", user.country)
        values.put("language", user.language)
        values.put("createdatetime", user.createdatetime)
        values.put("authtype", user.authtype)
        values.put("imageurl", user.imageurl)
        values.put("role", user.role)
        values.put("hasevent", "Y")
        values.put("hastask", user.hastask)
        values.put("haspayment", user.haspayment)
        values.put("hasguest", user.hasguest)
        values.put("hasvendor", user.hasvendor)
        values.put("tasksactive", user.tasksactive)
        values.put("taskscompleted", user.taskscompleted)
        values.put("payments", user.payments)
        values.put("guests", user.guests)
        values.put("status", user.status)
        values.put("vendors", user.vendors)
        values.put("eventbudget", user.eventbudget)
        values.put("numberguests", user.numberguests)
        values.put("distanceunit", user.distanceunit)

        try {
            val retVal = db.update("USER", values, "userid = '${user.userid}'", null)
            if (retVal >= 1) {
                Log.d(TAG, "User ${user.userid} updated")
            } else {
                Log.d(TAG, "User ${user.userid} not updated")
            }
        } catch (e: Exception) {
            throw UserEditionException(e.toString())
//        } finally {
//            db.close()
        }
    }

    companion object {
        const val TAG = "UserDBHelper"
    }

    override suspend fun onAddEditUser(user: User) {
        if (!getUserexists(user.userid!!)) {
            insert(user)
        } else {
            update(user)
        }
        nexthandleru?.onAddEditUser(user)
    }

    override fun onAddEditTask(task: Task) {
        val userId =
            UserSessionHelper.getUserSession("user_id") as String

        val user = getUser(userId)!!
        user.tasksactive += 1
        user.hastask = TaskModel.ACTIVEFLAG
        update(user)

        Log.d(TAG, "There are currently ${user.tasksactive} active tasks associated to the User")
        Log.d(TAG, "Flag hastask for the User has been set to ${TaskModel.ACTIVEFLAG}")

        nexthandlert?.onAddEditTask(task)
    }

    override fun onDeleteTask(taskId: String) {
        val userId =
            UserSessionHelper.getUserSession("user_id") as String

        val user = getUser(userId)!!
        user.tasksactive -= 1
        if (user.tasksactive == 0) user.hastask = TaskModel.INACTIVEFLAG
        update(user)

        nexthandlerdelt?.onDeleteTask(taskId)
    }

    override fun onAddEditPayment(payment: Payment) {
        val userId =
            UserSessionHelper.getUserSession("user_id") as String

        val user = getUser(userId)!!
        user.payments += 1
        user.haspayment = PaymentModel.ACTIVEFLAG
        update(user)

        Log.d(TAG, "There are currently ${user.payments} payments associated to the User")
        Log.d(TAG, "Flag haspayment for the User has been set to ${user.haspayment}")

        nexthandlerp?.onAddEditPayment(payment)
    }

    override fun onDeletePayment(paymentId: String) {
        val userId =
            UserSessionHelper.getUserSession("user_id") as String

        val user = getUser(userId)!!
        user.payments -= 1
        if (user.payments == 0) user.haspayment = PaymentModel.INACTIVEFLAG
        update(user)

        nexthandlerpdel?.onDeletePayment(paymentId)
    }

    override fun onAddEditGuest(guest: Guest) {
        val userId =
            UserSessionHelper.getUserSession("user_id") as String

        val user = getUser(userId)!!
        user.guests += 1
        user.hasguest = GuestModel.ACTIVEFLAG
        update(user)
        nexthandlerg?.onAddEditGuest(guest)
        Log.i("UserDBHelper", "onAddEditGuest reached")
    }

    override fun onDeleteGuest(guestId: String) {
        val userId =
            UserSessionHelper.getUserSession("user_id") as String

        val user = getUser(userId)!!
        user.guests -= 1
        if (user.guests == 0) user.hasguest = GuestModel.INACTIVEFLAG
        update(user)

        nexthandlerdelg?.onDeleteGuest(guestId)
    }

    override fun onAddEditVendor(vendor: Vendor) {
        val userId =
            UserSessionHelper.getUserSession("user_id") as String

        val user = getUser(userId)!!
        user.vendors += 1
        user.hasvendor = VendorModel.ACTIVEFLAG
        update(user)
        nexthandlerv?.onAddEditVendor(vendor)
    }

    override fun onDeleteVendor(vendorId: String) {
        val userId =
            UserSessionHelper.getUserSession("user_id") as String

        val user = getUser(userId)!!
        user.vendors -= 1
        if (user.vendors == 0) user.hasvendor = VendorModel.INACTIVEFLAG
        update(user)

        nexthandlerdelv?.onDeleteVendor(vendorId)
    }

    override suspend fun onOnboardUser(user: User, event: Event) {
        if (!getUserexists(user.userid!!)) {
            insert(user)
        } else {
            update(user)
        }
        nexthandleron?.onOnboardUser(user, event)
    }
}

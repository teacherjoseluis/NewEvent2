package com.example.newevent2.Model

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import com.example.newevent2.*
import com.example.newevent2.Functions.userdbhelper

class UserDBHelper(val context: Context) : CoRAddEditUser, CoRAddEditTask, CoRDeleteTask,
    CoRAddEditPayment, CoRDeletePayment, CoRAddEditGuest,
    CoRDeleteGuest, CoRAddEditVendor, CoRDeleteVendor, CoROnboardUser {

    private val db: SQLiteDatabase = DatabaseHelper(context).writableDatabase
    var nexthandleru: CoRAddEditUser? = null
    private var nexthandlert: CoRAddEditTask? = null
    private var nexthandlerdelt: CoRDeleteTask? = null
    private var nexthandlerp: CoRAddEditPayment? = null
    var nexthandlerdelp: CoRDeletePayment? = null
    var nexthandlerg: CoRAddEditGuest? = null
    var nexthandlerdelg: CoRDeleteGuest? = null
    var nexthandlerv: CoRAddEditVendor? = null
    var nexthandlerdelv: CoRDeleteVendor? = null
    var nexthandleron: CoROnboardUser? = null

    private lateinit var user: User

    fun insert(user: User?) {
        val values = ContentValues()
        values.put("userid", user!!.key)
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
        db.insert("USER", null, values)
        Log.d(TAG, "User record inserted")
    }

    private fun getUserexists(key: String): Boolean {
        var existsflag = false
        val cursor: Cursor = db.rawQuery("SELECT * FROM USER WHERE userid = '$key'", null)
        if (cursor.count > 0) {
            existsflag = true
        }
        cursor.close()
        return existsflag
    }

    fun getUserKey(): String {
        var key = ""
        val cursor: Cursor = db.rawQuery("SELECT userid FROM USER LIMIT 1", null)
        if (cursor.count > 0) {
            cursor.moveToFirst()
            do {
                key = cursor.getString(cursor.getColumnIndex("userid"))
            } while (cursor.moveToNext())
        }
        cursor.close()
        return key
    }

    fun getUser(key: String): User {
        var user = User()
        val cursor: Cursor = db.rawQuery("SELECT * FROM USER WHERE userid = '$key'", null)
        if (cursor.count > 0) {
            cursor.moveToFirst()
            do {
                val userid = cursor.getString(cursor.getColumnIndex("userid"))
                val eventid = cursor.getString(cursor.getColumnIndex("eventid"))
                val shortname = cursor.getString(cursor.getColumnIndex("shortname"))
                val email = cursor.getString(cursor.getColumnIndex("email"))
                val country = cursor.getString(cursor.getColumnIndex("country"))
                val language = cursor.getString(cursor.getColumnIndex("language"))
                val createdatetime = cursor.getString(cursor.getColumnIndex("createdatetime"))
                val authtype = cursor.getString(cursor.getColumnIndex("authtype"))
                val status = cursor.getString(cursor.getColumnIndex("status"))
                val imageurl = cursor.getString(cursor.getColumnIndex("imageurl"))
                val role = cursor.getString(cursor.getColumnIndex("role"))
                val hasevent = cursor.getString(cursor.getColumnIndex("hasevent"))
                val hastask = cursor.getString(cursor.getColumnIndex("hastask"))
                val haspayment = cursor.getString(cursor.getColumnIndex("haspayment"))
                val hasguest = cursor.getString(cursor.getColumnIndex("hasguest"))
                val hasvendor = cursor.getString(cursor.getColumnIndex("hasvendor"))
                val tasksactive = cursor.getInt(cursor.getColumnIndex("tasksactive"))
                val taskscompleted = cursor.getInt(cursor.getColumnIndex("taskscompleted"))
                val payments = cursor.getInt(cursor.getColumnIndex("payments"))
                val guests = cursor.getInt(cursor.getColumnIndex("guests"))
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
                        guests
                    )
                Log.d(TAG, "User $userid record obtained from local DB")
            } while (cursor.moveToNext())
        }
        cursor.close()
        return user
    }

    fun update(user: User) {
        val values = ContentValues()
        values.put("userid", user.key)
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

        val retVal = db.update("USER", values, "userid = '${user.key}'", null)
        if (retVal >= 1) {
            Log.d(TAG, "User ${user.key} updated")
        } else {
            Log.d(TAG, "User ${user.key} not updated")
        }
        db.close()
    }

    companion object {
        const val TAG = "UserDBHelper"
    }

    override suspend fun onAddEditUser(user: User) {
        if (!getUserexists(user.key)) {
            insert(user)
        } else {
            update(user)
        }
        nexthandleru?.onAddEditUser(user)
    }

    override fun onAddEditTask(task: Task) {
        // Updating User information in Session
        val user = getUser(userdbhelper.getUserKey())
        user.tasksactive = user.tasksactive + 1
        user.hastask = TaskModel.ACTIVEFLAG
        update(user)

        Log.d(TAG, "There are currently ${user.tasksactive} active tasks associated to the User")
        Log.d(TAG, "Flag hastask for the User has been set to ${TaskModel.ACTIVEFLAG}")

        nexthandlert?.onAddEditTask(task)
    }

    override fun onDeleteTask(task: Task) {
        user.tasksactive = user.tasksactive - 1
        if (user.tasksactive == 0) user.hastask = TaskModel.INACTIVEFLAG
        update(user)

        nexthandlerdelt?.onDeleteTask(task)
    }

    override fun onAddEditPayment(payment: Payment) {
        user.payments = user.payments + 1
        user.haspayment = PaymentModel.ACTIVEFLAG
        update(user)

        Log.d(TAG, "There are currently ${user.payments} payments associated to the User")
        Log.d(TAG, "Flag haspayment for the User has been set to ${user.haspayment}")

        nexthandlerp?.onAddEditPayment(payment)
    }

    override fun onDeletePayment(payment: Payment) {
        user.payments = user.payments - 1
        if (user.payments == 0) user.haspayment = PaymentModel.INACTIVEFLAG
        update(user)

        nexthandlerdelp?.onDeletePayment(payment)
    }

    override fun onAddEditGuest(guest: Guest) {
        user.guests = user.guests + 1
        user.hasguest = GuestModel.ACTIVEFLAG
        update(user)

        nexthandlerg?.onAddEditGuest(guest)
    }

    override fun onDeleteGuest(guest: Guest) {
        user.guests = user.guests - 1
        if (user.guests == 0) user.hasguest = GuestModel.INACTIVEFLAG
        update(user)

        nexthandlerdelg?.onDeleteGuest(guest)
    }

    override fun onAddEditVendor(vendor: Vendor) {
        user.vendors = user.vendors + 1
        user.hasvendor = VendorModel.ACTIVEFLAG
        update(user)

        nexthandlerv?.onAddEditVendor(vendor)
    }

    override fun onDeleteVendor(vendor: Vendor) {
        user.vendors = user.vendors - 1
        if (user.vendors == 0) user.hasvendor = VendorModel.INACTIVEFLAG
        update(user)

        nexthandlerdelv?.onDeleteVendor(vendor)
    }

    override suspend fun onOnboardUser(user: User, event: Event) {
        if (!getUserexists(user.key)) {
            insert(user)
        } else {
            update(user)
        }
        nexthandleron?.onOnboardUser(user, event)
    }
}

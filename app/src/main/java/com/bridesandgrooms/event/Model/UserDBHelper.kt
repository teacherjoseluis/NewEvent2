package com.bridesandgrooms.event.Model

import Application.FirebaseDataImportException
import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import com.bridesandgrooms.event.*
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
import com.bridesandgrooms.event.Functions.getUserSession
import com.bridesandgrooms.event.Functions.userdbhelper
import kotlinx.coroutines.ExperimentalCoroutinesApi

class UserDBHelper(val context: Context) : CoRAddEditUser, CoRAddEditTask, CoRDeleteTask,
    CoRAddEditPayment, CoRDeletePayment, CoRAddEditGuest,
    CoRDeleteGuest, CoRAddEditVendor, CoRDeleteVendor, CoROnboardUser {

    var nexthandleru: CoRAddEditUser? = null
    var nexthandlert: CoRAddEditTask? = null
    var nexthandlerdelt: CoRDeleteTask? = null
    var nexthandlerp: CoRAddEditPayment? = null
    var nexthandlerpdel: CoRDeletePayment? = null
    var nexthandlerg: CoRAddEditGuest? = null
    var nexthandlerdelg: CoRDeleteGuest? = null
    private var nexthandlerv: CoRAddEditVendor? = null
    private var nexthandlerdelv: CoRDeleteVendor? = null
    var nexthandleron: CoROnboardUser? = null

    private var useremail: String

    init {
        useremail = getUserSession(context, "email").toString()
    }

    fun insert(user: User?) {
        val db: SQLiteDatabase = DatabaseHelper(context).writableDatabase
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
        try {
            db.insert("USER", null, values)
            Log.d(TAG, "User record inserted")
        } catch (e: Exception) {
            Log.e(TAG, e.message.toString())
        } finally {
            db.close()
        }
    }

    @ExperimentalCoroutinesApi
    suspend fun firebaseImport(user: User): Boolean {
        val db: SQLiteDatabase = DatabaseHelper(context).writableDatabase
        Log.d(TAG, "Starting UserDB record import ${user.userid}")
        //val user: User
        try {
            val userModel = UserModel(user)
            Log.d(TAG, "Start getting User record from FireBase")
            val user = userModel.getUser()
            Log.d(TAG, "User record obtained from FireBase ${user}")
            db.execSQL("DELETE FROM USER")
            insert(user)
        } catch (e: Exception) {
            Log.e(TAG, e.message.toString())
            throw FirebaseDataImportException("Error importing User data: $e")
        } finally {
            db.close()
        }
        return true
    }

    private fun getUserexists(key: String): Boolean {
        val db: SQLiteDatabase = DatabaseHelper(context).writableDatabase
        var existsflag = false
        try {
            val cursor: Cursor = db.rawQuery("SELECT * FROM USER WHERE userid = '$key'", null)
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

    @SuppressLint("Range")
    fun getUserKey(): String? {
        val db: SQLiteDatabase = DatabaseHelper(context).writableDatabase
        var key = ""
        val cursor: Cursor = db.rawQuery("SELECT userid FROM USER WHERE email = '$useremail'", null)
        try {
            if (cursor.count > 0) {
                cursor.moveToFirst()
                do {
                    key = cursor.getString(cursor.getColumnIndex("userid"))
                } while (cursor.moveToNext())
            }
            return key
        } catch (e: Exception) {
            Log.e(TAG, e.message.toString())
            return null
        } finally {
            cursor.close()
            db.close()
        }
    }

    @SuppressLint("Range")
    fun getUser(key: String?): User? {
        val db: SQLiteDatabase = DatabaseHelper(context).writableDatabase
        var user = User()
        try {
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
                    val vendors = cursor.getInt(cursor.getColumnIndex("vendors"))
                    val eventbudget = cursor.getString(cursor.getColumnIndex("eventbudget"))
                    val numberguests = cursor.getInt(cursor.getColumnIndex("numberguests"))
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
                            numberguests
                        )
                    Log.d(TAG, "User $userid record obtained from local DB")
                } while (cursor.moveToNext())
            }
            cursor.close()
            return user
        } catch (e: Exception) {
            Log.e(TAG, e.message.toString())
            return null
        } finally {
            db.close()
        }
    }

    fun update(user: User) {
        val db: SQLiteDatabase = DatabaseHelper(context).writableDatabase
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


        try {
            val retVal = db.update("USER", values, "userid = '${user.userid}'", null)
            if (retVal >= 1) {
                Log.d(TAG, "User ${user.userid} updated")
            } else {
                Log.d(TAG, "User ${user.userid} not updated")
            }
        } catch (e: Exception) {
            Log.e(TAG, e.message.toString())
        } finally {
            db.close()
        }
        //db.close()
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
        // Updating User information in Session
        val user = getUser(userdbhelper.getUserKey())!!
        user.tasksactive = user.tasksactive + 1
        user.hastask = TaskModel.ACTIVEFLAG
        update(user)

        Log.d(TAG, "There are currently ${user.tasksactive} active tasks associated to the User")
        Log.d(TAG, "Flag hastask for the User has been set to ${TaskModel.ACTIVEFLAG}")

        nexthandlert?.onAddEditTask(task)
    }

    override fun onDeleteTask(task: Task) {
        val user = getUser(userdbhelper.getUserKey())!!
        user.tasksactive = user.tasksactive - 1
        if (user.tasksactive == 0) user.hastask = TaskModel.INACTIVEFLAG
        update(user)

        nexthandlerdelt?.onDeleteTask(task)
    }

    override fun onAddEditPayment(payment: Payment) {
        val user = getUser(userdbhelper.getUserKey())!!
        user.payments = user.payments + 1
        user.haspayment = PaymentModel.ACTIVEFLAG
        update(user)

        Log.d(TAG, "There are currently ${user.payments} payments associated to the User")
        Log.d(TAG, "Flag haspayment for the User has been set to ${user.haspayment}")

        nexthandlerp?.onAddEditPayment(payment)
    }

    override fun onDeletePayment(payment: Payment) {
        val user = getUser(userdbhelper.getUserKey())!!
        user.payments = user.payments - 1
        if (user.payments == 0) user.haspayment = PaymentModel.INACTIVEFLAG
        update(user)

        nexthandlerpdel?.onDeletePayment(payment)
    }

    override fun onAddEditGuest(guest: Guest) {
        val user = getUser(userdbhelper.getUserKey())!!
        user.guests = user.guests + 1
        user.hasguest = GuestModel.ACTIVEFLAG
        update(user)
        nexthandlerg?.onAddEditGuest(guest)
        Log.i("UserDBHelper", "onAddEditGuest reached")
    }

    override fun onDeleteGuest(guest: Guest) {
        val user = getUser(userdbhelper.getUserKey())!!
        user.guests = user.guests - 1
        if (user.guests == 0) user.hasguest = GuestModel.INACTIVEFLAG
        update(user)

        nexthandlerdelg?.onDeleteGuest(guest)
    }

    override fun onAddEditVendor(vendor: Vendor) {
        val user = getUser(userdbhelper.getUserKey())!!
        user.vendors = user.vendors + 1
        user.hasvendor = VendorModel.ACTIVEFLAG
        update(user)

        nexthandlerv?.onAddEditVendor(vendor)
    }

    override fun onDeleteVendor(vendor: Vendor) {
        val user = getUser(userdbhelper.getUserKey())!!
        user.vendors = user.vendors - 1
        if (user.vendors == 0) user.hasvendor = VendorModel.INACTIVEFLAG
        update(user)

        nexthandlerdelv?.onDeleteVendor(vendor)
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

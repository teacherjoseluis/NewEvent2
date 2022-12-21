package com.example.newevent2.Model

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.newevent2.*
import com.example.newevent2.Functions.converttoString
import com.example.newevent2.Functions.currentDateTime
import kotlinx.coroutines.tasks.await
import java.lang.Exception
import java.text.DateFormat
import androidx.annotation.NonNull
import com.google.firebase.FirebaseException
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import java.util.concurrent.Flow
import kotlin.coroutines.resumeWithException
import com.google.firebase.database.DatabaseError

import com.google.firebase.database.DataSnapshot
import kotlinx.coroutines.*
import kotlin.coroutines.resume


class UserModel(
    //This user creates and edits Users into Firebase
    val key: String
) : CoRAddEditTask, CoRDeleteTask, CoRAddEditPayment, CoRDeletePayment, CoRAddEditGuest,
    CoRDeleteGuest, CoRAddEditVendor, CoRDeleteVendor, CoRAddEditUser, CoROnboardUser {

    private var database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private var myRef = database.reference
    private val postRef = myRef.child("User").child(this.key)
    private var firebaseUser = User(key)
    private val firebaseAuth = FirebaseAuth.getInstance()

    var tasksactive = 0
    var paymentsactive = 0
    var guestsactive = 0
    var vendorsactive = 0

    var nexthandleru: CoRAddEditUser? = null
    private var nexthandlert: CoRAddEditTask? = null
    var nexthandlerdelt: CoRDeleteTask? = null
    private var nexthandlerp: CoRAddEditPayment? = null
    var nexthandlerpdel: CoRDeletePayment? = null
    var nexthandlerg: CoRAddEditGuest? = null
    var nexthandlerdelg: CoRDeleteGuest? = null
    var nexthandlerv: CoRAddEditVendor? = null
    var nexthandlerdelv: CoRDeleteVendor? = null
    var nexthandlere: CoRAddEditEvent? = null
    var nexthandleron: CoROnboardUser? = null

    fun getUser(dataFetched: FirebaseSuccessUser) {
        val userListenerActive = object : ValueEventListener {
            @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()) {
                    firebaseUser.apply {
                        if (p0.child("eventid").getValue(String::class.java)!! == "") {
                            if (p0.hasChild("Event")) {

                            }
                        } else {
                            eventid = p0.child("eventid").getValue(String::class.java)!!
                        }
                        shortname = p0.child("shortname").getValue(String::class.java)!!
                        email = p0.child("email").getValue(String::class.java)!!
                        country = p0.child("country").getValue(String::class.java)!!
                        language = p0.child("language").getValue(String::class.java)!!
                        createdatetime = p0.child("createdatetime").getValue(String::class.java)!!
                        authtype = p0.child("authtype").getValue(String::class.java)!!
                        imageurl = p0.child("imageurl").getValue(String::class.java)!!
                        role = p0.child("role").getValue(String::class.java)!!
                        hasevent = if (p0.hasChild("Event")) {
                            "Y"
                        } else {
                            "N"
                        }
                        hastask = p0.child("hastask").getValue(String::class.java)!!
                        haspayment = p0.child("haspayment").getValue(String::class.java)!!
                        hasguest = p0.child("hasguest").getValue(String::class.java)!!
                        hasvendor = p0.child("hasvendor").getValue(String::class.java)!!
                        tasksactive = p0.child("tasksactive").getValue(Int::class.java)!!
                        taskscompleted = p0.child("taskscompleted").getValue(Int::class.java)!!
                        payments = p0.child("payments").getValue(Int::class.java)!!
                        guests = p0.child("guests").getValue(Int::class.java)!!
                        vendors = p0.child("vendors").getValue(Int::class.java)!!

                        Log.d(
                            TAG,
                            "Data associated to User $key ($email) has been retrieved from Firebase"
                        )
                    }
                }
                dataFetched.onUserexists(firebaseUser)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                println("loadPost:onCancelled ${databaseError.toException()}")
            }
        }
        postRef.addValueEventListener(userListenerActive)
    }

    @ExperimentalCoroutinesApi
    suspend fun getUser(): User {
        return try {
            postRef.awaitsSingle()?.getValue(User::class.java)!!
        } catch (e: Exception) {
            Log.d(
                TAG,
                "Data associated to User cannot ben retrieved from Firebase"
            )
            User()
        }
    }

    private fun editUser(user: User, savesuccessflag: FirebaseSaveSuccess) {
        //Commented entries correspond to values in the User entity that are not to be edited
        postRef.child("eventid").setValue(user.eventid)
        postRef.child("shortname").setValue(user.shortname)
        //postRef.child("email").setValue(user.email)
        //postRef.child("country").setValue(user.country)
        postRef.child("language").setValue(user.language)
        //postRef.child("createdatetime").setValue(user.createdatetime)
        //postRef.child("authtype").setValue(user.authtype)
        //postRef.child("imageurl").setValue(user.imageurl)
        postRef.child("role").setValue(user.role)
        postRef.child("hasevent").setValue(user.hasevent)
        //postRef.child("hastask").setValue(user.hastask)
        //postRef.child("haspayment").setValue(user.haspayment)
        //postRef.child("hasguest").setValue(user.hasguest)
        //postRef.child("hasvendor").setValue(user.hasvendor)
        Log.d(TAG, "Data associated to User ${user.key} has just been saved")
    }

    private fun editUserAddTask(value: Int) {
        postRef.child("tasksactive").setValue(value)
        Log.d(TAG, "There are currently $value active tasks associated to the User")
    }

    private fun editUserAddPayment(value: Int) {
        postRef.child("payments").setValue(value)
        Log.d(TAG, "There are currently $value payments associated to the User")
    }

    private fun editUserAddGuest(value: Int) {
        postRef.child("guests").setValue(value)
        Log.d(TAG, "There are currently $value guests associated to the User")
    }

    private fun editUserAddVendor(value: Int) {
        postRef.child("vendors").setValue(value)
        Log.d(TAG, "There are currently $value vendors associated to the User")
    }

    private fun editUserTaskflag(flag: String) {
        postRef.child("hastask").setValue(flag)
        Log.d(TAG, "Flag hastask for the User has been set to $flag")
    }

    private fun editUserPaymentflag(flag: String) {
        postRef.child("haspayment").setValue(flag)
        Log.d(TAG, "Flag haspayment for the User has been set to $flag")
    }

    private fun editUserGuestflag(flag: String) {
        postRef.child("hasguest").setValue(flag)
        Log.d(TAG, "Flag hasguest for the User has been set to $flag")
    }

    private fun editUserVendorflag(flag: String) {
        postRef.child("hasvendor").setValue(flag)
        Log.d(TAG, "Flag hasvendor for the User has been set to $flag")
    }

    private suspend fun addUser(user: User) {
        coroutineScope {
            val userfb = hashMapOf(
                "eventid" to user.eventid,
                "shortname" to user.shortname,
                "email" to user.email,
                "country" to user.country,
                "language" to user.language,
                "createdatetime" to converttoString(currentDateTime, DateFormat.MEDIUM),
                "authtype" to user.authtype,
                "imageurl" to "",
                "role" to user.role,
                "hasevent" to "Y",
                "hastask" to "",
                "haspayment" to "",
                "hasguest" to "",
                "hasvendor" to "",
                "tasksactive" to 0,
                "taskscompleted" to 0,
                "payments" to 0,
                "guests" to 0,
                "status" to "A",
                "vendors" to 0
            )
            postRef.setValue(
                userfb as Map<String, Any>
            ).await()
        }
    }
    //        { error, _ ->
//            if (error != null) {
//                Log.e(TAG, "There was an error saving the User (${error.message})")
//                //savesuccessflag.onSaveSuccess(false)
//            } else {
//                Log.d(TAG, "User was saved successfully")
//                //savesuccessflag.onSaveSuccess(true)
//            }
//        }
//    }

    @ExperimentalCoroutinesApi
    suspend fun DatabaseReference.awaitsSingle(): DataSnapshot? =
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

    override fun onAddEditTask(task: Task) {
        editUserTaskflag(TaskModel.ACTIVEFLAG)
        editUserAddTask(tasksactive + 1)
        nexthandlert?.onAddEditTask(task)
    }

    override fun onDeleteTask(task: Task) {
        editUserAddTask(tasksactive - 1)
        nexthandlerdelt?.onDeleteTask(task)
    }

    override fun onAddEditPayment(payment: Payment) {
        editUserPaymentflag(PaymentModel.ACTIVEFLAG)
        editUserAddPayment(paymentsactive + 1)
        nexthandlerp?.onAddEditPayment(payment)
    }

    override fun onDeletePayment(payment: Payment) {
        editUserAddPayment(paymentsactive - 1)
        nexthandlerpdel?.onDeletePayment(payment)
    }

    override suspend fun onAddEditGuest(guest: Guest) {
        editUserGuestflag(GuestModel.ACTIVEFLAG)
        editUserAddGuest(guestsactive + 1)
        nexthandlerg?.onAddEditGuest(guest)
    }

    override fun onDeleteGuest(guest: Guest) {
        nexthandlerdelg?.onDeleteGuest(guest)
    }

    override fun onAddEditVendor(vendor: Vendor) {
        editUserVendorflag(VendorModel.ACTIVEFLAG)
        editUserAddVendor(vendorsactive + 1)
        nexthandlerv?.onAddEditVendor(vendor)
    }

    override fun onDeleteVendor(vendor: Vendor) {
        nexthandlerdelv?.onDeleteVendor(vendor)
    }

    override suspend fun onAddEditUser(user: User) {
        addUser(user)
        nexthandleru?.onAddEditUser(user)
    }

    interface FirebaseSuccessUser {
        fun onUserexists(user: User)
    }

    interface FirebaseSaveSuccess {
        fun onSaveSuccess(flag: Boolean)
    }

    companion object {
        const val TAG = "UserModel"
    }

    override suspend fun onOnboardUser(user: User, event: Event) {
        addUser(user)
        nexthandleron?.onOnboardUser(user, event)
    }

}
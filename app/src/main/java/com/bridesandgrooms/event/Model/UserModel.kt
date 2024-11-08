package com.bridesandgrooms.event.Model

import Application.UserAuthenticationException
import Application.UserCreationException
import Application.UserEditionException
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
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
import com.bridesandgrooms.event.Functions.converttoString
import com.bridesandgrooms.event.Functions.currentDateTime
import kotlinx.coroutines.tasks.await
import java.text.DateFormat
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlin.coroutines.resumeWithException
import com.google.firebase.database.DatabaseError

import com.google.firebase.database.DataSnapshot
import kotlinx.coroutines.*
import kotlin.Exception
import kotlin.coroutines.resume


class UserModel(
    //This user creates and edits Users into Firebase
    var user: User?
) : CoRAddEditTask, CoRDeleteTask, CoRAddEditPayment, CoRDeletePayment, CoRAddEditGuest,
    CoRDeleteGuest, CoRAddEditVendor, CoRDeleteVendor, CoRAddEditUser, CoROnboardUser {

    private val key = user?.userid

    private var database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private var myRef = database.reference
    private val currentUserUID = FirebaseAuth.getInstance().currentUser?.uid
    private val userRef = FirebaseDatabase.getInstance().getReference("User/$currentUserUID")
    private val postRef = myRef.child("User").child(this.key!!)
    private var firebaseUser = User(key)
    private val coroutineScope = CoroutineScope(Dispatchers.Main)

    var nexthandleru: CoRAddEditUser? = null
    private var nexthandlert: CoRAddEditTask? = null
    var nexthandlerdelt: CoRDeleteTask? = null
    private var nexthandlerp: CoRAddEditPayment? = null
    var nexthandlerpdel: CoRDeletePayment? = null
    var nexthandlerg: CoRAddEditGuest? = null
    var nexthandlerdelg: CoRDeleteGuest? = null
    var nexthandlerv: CoRAddEditVendor? = null
    var nexthandlerdelv: CoRDeleteVendor? = null
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
                        eventbudget = p0.child("eventbudget").getValue(String::class.java)!!
                        numberguests = p0.child("numberguests").getValue(Int::class.java)!!
                        distanceunit = p0.child("distanceunit").getValue(String::class.java)!!

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
        //var user: User? = null
        try {
            Log.d(TAG, "Path where User information is found in Firebase: ${postRef}")
            //user = postRef.awaitsSingle()?.getValue(User::class.java)!!
            //user!!.userid = postRef.key.toString()
            //--------------------------------------------------------------------------------
            user!!.eventid =
                postRef.awaitsSingle()?.child("eventid")?.getValue(String::class.java)!!

            user!!.shortname =
                postRef.awaitsSingle()?.child("shortname")!!.getValue(String::class.java)!!
            user!!.email = postRef.awaitsSingle()?.child("email")!!.getValue(String::class.java)!!
            user!!.country =
                postRef.awaitsSingle()?.child("country")!!.getValue(String::class.java)!!
            user!!.language =
                postRef.awaitsSingle()?.child("language")!!.getValue(String::class.java)!!
            user!!.createdatetime =
                postRef.awaitsSingle()?.child("createdatetime")!!.getValue(String::class.java)!!
            user!!.authtype =
                postRef.awaitsSingle()?.child("authtype")!!.getValue(String::class.java)!!
            user!!.imageurl =
                postRef.awaitsSingle()?.child("imageurl")!!.getValue(String::class.java)!!
            user!!.role = postRef.awaitsSingle()?.child("role")!!.getValue(String::class.java)!!
            user!!.hasevent = if (postRef.awaitsSingle()?.hasChild("Event") == true) {
                "Y"
            } else {
                "N"
            }
            user!!.hastask =
                postRef.awaitsSingle()?.child("hastask")!!.getValue(String::class.java)!!
            user!!.haspayment =
                postRef.awaitsSingle()?.child("haspayment")!!.getValue(String::class.java)!!
            user!!.hasguest =
                postRef.awaitsSingle()?.child("hasguest")!!.getValue(String::class.java)!!
            user!!.hasvendor =
                postRef.awaitsSingle()?.child("hasvendor")!!.getValue(String::class.java)!!
            user!!.tasksactive =
                postRef.awaitsSingle()?.child("tasksactive")!!.getValue(Int::class.java)!!
            user!!.taskscompleted =
                postRef.awaitsSingle()?.child("taskscompleted")!!.getValue(Int::class.java)!!
            user!!.payments =
                postRef.awaitsSingle()?.child("payments")!!.getValue(Int::class.java)!!
            user!!.guests = postRef.awaitsSingle()?.child("guests")!!.getValue(Int::class.java)!!
            user!!.vendors = postRef.awaitsSingle()?.child("vendors")!!.getValue(Int::class.java)!!
            user!!.eventbudget =
                postRef.awaitsSingle()?.child("eventbudget")!!.getValue(Int::class.java)!!
                    .toString()
            user!!.numberguests =
                postRef.awaitsSingle()?.child("numberguests")!!.getValue(Int::class.java)!!
            user!!.status =
                postRef.awaitsSingle()?.child("status")!!.getValue(String::class.java)!!
            user!!.distanceunit =
                postRef.awaitsSingle()?.child("distanceunit")!!.getValue(String::class.java)!!
            //--------------------------------------------------------------------------------
            Log.d(TAG, "User Key found in Firebase: ${user!!.userid}")

            //val eventmodel = EventModel()
            //user.eventid = eventmodel.getEventKey(user.userid!!)
            //Log.d(TAG, "Event Key found in Firebase for the User: ${user.eventid}")
        } catch (e: Exception) {
            Log.e(TAG, e.message.toString())
        }
        return user!!
    }

    private fun editUserAddTask(value: Int) {
        coroutineScope.launch {
            //val user = getUser()
            try {
                userRef.child("tasksactive").setValue(user!!.tasksactive + value).await()
            } catch (e: Exception) {
                throw UserEditionException(e.toString())
            }
            Log.d(TAG, "There are currently $value active tasks associated to the User")
        }
    }

    private fun editUserAddPayment(value: Int) {
        coroutineScope.launch {
            //val user = getUser()
            try {
                userRef.child("payments").setValue(user!!.payments + value).await()
            } catch (e: Exception) {
                throw UserEditionException(e.toString())
            }
            Log.d(TAG, "There are currently $value payments associated to the User")
        }
    }

    private fun editUserAddGuest(value: Int) {
        coroutineScope.launch {
            //val user = getUser()
            try {
                userRef.child("guests").setValue(user!!.guests + value).await()
            } catch (e: Exception) {
                throw UserEditionException(e.toString())
            }
            Log.d(TAG, "There are currently $value guests associated to the User")
        }
    }

    private fun editUserAddVendor(value: Int) {
        coroutineScope.launch {
            //val user = getUser()
            try {
                userRef.child("vendors").setValue(user!!.vendors + value).await()
            } catch (e: Exception) {
                throw UserEditionException(e.toString())
            }
            Log.d(TAG, "There are currently $value vendors associated to the User")
        }
    }

    private fun editUserTaskflag(flag: String) {
        coroutineScope.launch {
            try {
                userRef.child("hastask").setValue(flag).await()
            } catch (e: Exception) {
                throw UserEditionException(e.toString())
            }
            Log.d(TAG, "Flag hastask for the User has been set to $flag")
        }
    }

    private fun editUserPaymentflag(flag: String) {
        coroutineScope.launch {
            try {
                userRef.child("haspayment").setValue(flag).await()
            } catch (e: Exception) {
                throw UserEditionException(e.toString())
            }
            Log.d(TAG, "Flag haspayment for the User has been set to $flag")
        }
    }

    private fun editUserGuestflag(flag: String) {
        coroutineScope.launch {
            try {
                userRef.child("hasguest").setValue(flag).await()
            } catch (e: Exception) {
                throw UserEditionException(e.toString())
            }
            Log.d(TAG, "Flag hasguest for the User has been set to $flag")
        }
    }

    private fun editUserVendorflag(flag: String) {
        coroutineScope.launch {
            try {
                userRef.child("hasvendor").setValue(flag).await()
            } catch (e: Exception) {
                throw UserEditionException(e.toString())
            }
            Log.d(TAG, "Flag hasvendor for the User has been set to $flag")
        }
    }

    private suspend fun editUserShortName(user: User) {
        coroutineScope.launch {
            try {
                userRef.child("shortname").setValue(user.shortname).await()
            } catch (e: Exception) {
                throw UserEditionException(e.toString())
            }
            Log.d(TAG, "Shortname for the User has been set to ${user.shortname}")
        }
    }

    private suspend fun editUserRole(user: User) {
        coroutineScope.launch {
            try {
                userRef.child("role").setValue(user.role).await()
            } catch (e: Exception) {
                throw UserEditionException(e.toString())
            }
            Log.d(TAG, "Role for the User has been set to ${user.role}")
        }
    }

    private suspend fun editDistanceUnit(user: User) {
        coroutineScope.launch {
            try {
                userRef.child("distanceunit").setValue(user.distanceunit).await()
            } catch (e: Exception) {
                throw UserEditionException(e.toString())
            }
            Log.d(TAG, "Distance Unit for the User has been set to ${user.distanceunit}")
        }
    }

    private suspend fun addUser(user: User) {
        val postRef = myRef.child("User").child(this.key!!)
        val currentUserUID = FirebaseAuth.getInstance().currentUser?.uid

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
            "vendors" to 0,
            "eventbudget" to user.eventbudget,
            "numberguests" to user.numberguests,
            "distanceunit" to user.distanceunit,
            // The below fields are of exclusive use of Firebase to police the authentication
            // and number of active sessions
            "session" to "",
            "last_signed_in_at" to ""
        )
        try {
            if (currentUserUID == this.key) {
                postRef.setValue(
                    userfb as Map<String, Any>
                ).await()
            } else {
                throw UserAuthenticationException("User ID does not match with the Auth User in Firebase")
            }
        } catch (e: Exception) {
            throw UserCreationException(e.toString())
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

    override fun onAddEditTask(context: Context, user: User, task: Task) {
        editUserTaskflag(TaskModel.ACTIVEFLAG)
        editUserAddTask(1)
        nexthandlert?.onAddEditTask(context, user, task)
    }

    override fun onDeleteTask(context: Context, user: User, task: Task) {
        editUserAddTask(-1)
        nexthandlerdelt?.onDeleteTask(context, user, task)
    }


    override fun onAddEditPayment(context: Context, user: User, payment: Payment) {
        editUserPaymentflag(PaymentModel.ACTIVEFLAG)
        editUserAddPayment(1)
        nexthandlerp?.onAddEditPayment(context, user, payment)
    }

    override fun onDeletePayment(context: Context, user: User, payment: Payment) {
        editUserAddPayment(-1)
        nexthandlerpdel?.onDeletePayment(context, user, payment)
    }

    override fun onAddEditGuest(context: Context, user: User, guest: Guest) {
        editUserGuestflag(GuestModel.ACTIVEFLAG)
        editUserAddGuest(+1)
        nexthandlerg?.onAddEditGuest(context, user, guest)
    }

    override fun onDeleteGuest(context: Context, user: User, guest: Guest) {
        nexthandlerdelg?.onDeleteGuest(context, user, guest)
    }

    override fun onAddEditVendor(context: Context, user: User, vendor: Vendor) {
        editUserVendorflag(VendorModel.ACTIVEFLAG)
        editUserAddVendor(+1)
        nexthandlerv?.onAddEditVendor(context, user, vendor)
    }

    override fun onDeleteVendor(context: Context, user: User, vendor: Vendor) {
        nexthandlerdelv?.onDeleteVendor(context, user, vendor)
    }

    override suspend fun onAddEditUser(user: User) {
        editUserShortName(user)
        editUserRole(user)
        editDistanceUnit(user)
        nexthandleru?.onAddEditUser(user)
        Log.i("UserModel", "onAddEditGuest reached")
    }

    interface FirebaseSuccessUser {
        fun onUserexists(user: User)
    }

    interface FirebaseSuccessSession {
        fun onSessionexists(sessionid: String)
    }

    interface FirebaseSaveSuccess

    companion object {
        const val TAG = "UserModel"
    }

    override suspend fun onOnboardUser(user: User, event: Event) {
        addUser(user)
        nexthandleron?.onOnboardUser(user, event)
    }

}
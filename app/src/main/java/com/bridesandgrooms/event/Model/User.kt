package com.bridesandgrooms.event.Model

import Application.*
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.os.Parcel
import android.os.Parcelable
import android.util.Log
import android.widget.Toast
import com.baoyachi.stepview.bean.StepBean
import com.bridesandgrooms.event.Functions.deleteUserSession
import com.bridesandgrooms.event.Functions.getUserSession
import com.bridesandgrooms.event.Functions.saveUserSession
import com.bridesandgrooms.event.R
import com.google.firebase.auth.*
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

@SuppressLint("ParcelCreator")
class User(
    var userid: String? = "",
    var eventid: String = "",
    var shortname: String = "",
    var email: String = "",
    var country: String = "",
    var language: String = "",
    var createdatetime: String = "",
    var authtype: String = "",
    var status: String = "",
    var imageurl: String = "",
    var role: String = "",
    var hasevent: String = "",
    var hastask: String = "",
    var haspayment: String = "",
    var hasguest: String = "",
    var hasvendor: String = "",
    var tasksactive: Int = 0,
    var taskscompleted: Int = 0,
    var payments: Int = 0,
    var guests: Int = 0,
    var vendors: Int = 0,
    var eventbudget: String = "",
    var numberguests: Int = 0
) : Parcelable {

    private val mAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance().reference
    private lateinit var activeSessionsRef: DatabaseReference
    private lateinit var lastSignedInAtRef: DatabaseReference

    constructor(parcel: Parcel) : this(
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readString().toString(),
        parcel.readInt()
    )

    suspend fun login(
        activity: Activity,
        authtype: String,
        UserEmail: String?,
        UserPassword: String?,
        credential: AuthCredential?
    ): AuthResult = coroutineScope {
        val authResult: AuthResult?

        //choosing the authentication method
        when (authtype) {
            "email" -> {
                try {
                    authResult =
                        mAuth.signInWithEmailAndPassword(UserEmail!!, UserPassword!!).await()

                    if (authResult?.user!!.isEmailVerified) {
                        Toast.makeText(
                            activity,
                            activity.getString(R.string.success_email_login),
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        Toast.makeText(
                            activity,
                            activity.getString(R.string.notverified_email_login),
                            Toast.LENGTH_SHORT
                        ).show()
                        authResult.user!!.sendEmailVerification().await()
                        Toast.makeText(
                            activity,
                            activity.getString(R.string.checkverification),
                            Toast.LENGTH_SHORT
                        ).show()
                        throw EmailVerificationException("Email account for user ${authResult.user!!} has not been verified")
                    }
                } catch (e: Exception) {
                    if (e is com.google.firebase.FirebaseNetworkException) {
                        throw NetworkConnectivityException(e.toString())
                    } else {
                        throw UserAuthenticationException(e.toString())
                    }
                }
            }
            else -> {
                //Trying to implement coroutines here. Email authentication will also need it
                //val authResult = loginWithSocialNetwork(mAuth, credential!!)!!
                try {
                    authResult = mAuth.signInWithCredential(credential!!).await()
                } catch (e: Exception) {
                    if (e is com.google.firebase.FirebaseNetworkException) {
                        throw NetworkConnectivityException(e.toString())
                    } else {
                        throw UserAuthenticationException(e.toString())
                    }
                }
            }
        }
        // reviewing if there is an already logged session for the user - Need to add a parent node
        activeSessionsRef = database.child("Session").child(authResult.user?.uid!!).child("session")
        lastSignedInAtRef = database.child("Session").child(authResult.user?.uid!!).child("last_signed_in_at")
        val activeSessionsSnapshot = try {
            activeSessionsRef.get().await()
        } catch (e: Exception) {
            throw SessionAccessException(e.toString())
        }

        //-----------------------------------------------------------------------------------------------------------------
        val eventIdRef = database.child("User").child(authResult.user?.uid!!).child("eventid")
        val eventSnapShot = try {
            eventIdRef.get().await()
        } catch (e: java.lang.Exception) {
            throw SessionAccessException(e.toString())
        }
        val eventId = eventSnapShot.getValue(String::class.java)
        //-----------------------------------------------------------------------------------------------------------------
        if (activeSessionsSnapshot.exists()) {
//            val storedSessionID =
//                activeSessionsSnapshot.getValue(String::class.java)
//            if (storedSessionID != "") {
//                // Session ID exists and therefore a user is signed in elsewhere
//                throw ExistingSessionException("There is an existing session $storedSessionID for user ${authResult?.user!!}")
//            } else {
                // extracting the session value from Firebase
                val currentTimeMillis = System.currentTimeMillis()
                lastSignedInAtRef.setValue(currentTimeMillis.toString()).await()
                // Session ID does not exist and user can login using this device
                activeSessionsRef.setValue(authResult.user!!.metadata?.lastSignInTimestamp.toString())
                    .await()

                // saving authentication variables in the shared preferences
                saveUserSession(activity, authResult!!.user!!.email.toString(), null, "email")
                saveUserSession(activity, authResult.user!!.uid, null, "user_id")
                saveUserSession(activity, eventId, null, "event_id")
                saveUserSession(
                    activity,
                    authResult.user!!.metadata?.lastSignInTimestamp.toString(), null,
                    "session_id"
                )
                saveUserSession(activity, null, currentTimeMillis, "last_signed_in_at")
                return@coroutineScope authResult
            //}
        } else {
            // extracting the session value from Firebase
            val currentTimeMillis = System.currentTimeMillis()
            lastSignedInAtRef.setValue(currentTimeMillis.toString()).await()
            // Session ID does not exist and user can login using this device
            activeSessionsRef.setValue(authResult.user!!.metadata?.lastSignInTimestamp.toString())
                .await()

            // saving authentication variables in the shared preferences
            saveUserSession(activity, authResult!!.user!!.email.toString(), null, "email")
            saveUserSession(activity, authResult.user!!.uid, null, "user_id")
            saveUserSession(activity, eventId, null, "event_id")
            saveUserSession(
                activity,
                authResult.user!!.metadata?.lastSignInTimestamp.toString(), null,
                "session_id"
            )
            saveUserSession(activity, null, currentTimeMillis, "last_signed_in_at")
            return@coroutineScope authResult
        }
    }

    fun getUser(context: Context) : User {
        val userId = try {
            getUserSession(context, "user_id") as String
        } catch (e: Exception) {
            println(e.message)
            ""
        }
        val userDB = UserDBHelper(context)
        var user = userDB.getUser(userId)

//        if (user?.userid.isNullOrEmpty()){
//            user = UserModel(userId).getUser()
//            userDB.update(user)
//        }
        return user!!
    }

    fun logout(activity: Activity) {
        val userId = getUserSession(activity, "user_id").toString()
        activeSessionsRef = database.child(userId).child("session")
        if (userId != null) {
            activeSessionsRef.setValue(null)
        }
        mAuth.signOut()
        deleteUserSession(activity)
        Toast.makeText(activity, activity.getString(R.string.success_logout), Toast.LENGTH_SHORT)
            .show()
    }

    fun softlogout(activity: Activity) {
        val userId = getUserSession(activity, "user_id").toString()
        activeSessionsRef = database.child(userId).child("session")
        if (userId != null) {
            activeSessionsRef.setValue(null)
        }
        mAuth.signOut()
        deleteUserSession(activity)
    }

    suspend fun signup(UserEmail: String, UserPassword: String): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                mAuth.createUserWithEmailAndPassword(UserEmail, UserPassword).await()
                mAuth.currentUser?.sendEmailVerification()?.await()
                true
            } catch (e: Exception) {
                when (e) {
                    is FirebaseAuthUserCollisionException -> {
                        throw e
                    }
                    is FirebaseAuthWeakPasswordException -> {
                        throw e
                    }
                    is FirebaseAuthInvalidCredentialsException -> {
                        throw e
                    }
                    is FirebaseAuthEmailException -> {
                        throw e
                    }
                    else -> {
                        throw e
                    }
                }
            }
            false
        }
    }

    suspend fun sendPasswordReset(UserEmail: String): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                mAuth.sendPasswordResetEmail(UserEmail).await()
                true
            } catch (e: Exception) {
                when (e) {
                    is FirebaseAuthInvalidUserException -> {
                        throw e
                    }
                    is FirebaseAuthInvalidCredentialsException -> {
                        throw e
                    }
                    is FirebaseAuthEmailException -> {
                        throw e
                    }
                    else -> {
                        throw e
                    }
                }
            }
            false
        }
    }

    fun onboardingprogress(context: Context): ArrayList<StepBean> {
        val stepsBeanList = arrayListOf<StepBean>()
        val stepBean0 =
            StepBean(context.getString(R.string.event), if (this.hasevent == "Y") 1 else -1)
        val stepBean1 =
            StepBean(context.getString(R.string.task), if (this.hastask == "Y") 1 else -1)
        val stepBean2 =
            StepBean(context.getString(R.string.payment), if (this.haspayment == "Y") 1 else -1)
        val stepBean3 =
            StepBean(context.getString(R.string.guest), if (this.hasguest == "Y") 1 else -1)
        val stepBean4 =
            StepBean(context.getString(R.string.vendor), if (this.hasvendor == "Y") 1 else -1)

        stepsBeanList.add(stepBean0)
        stepsBeanList.add(stepBean1)
        stepsBeanList.add(stepBean2)
        stepsBeanList.add(stepBean3)
        stepsBeanList.add(stepBean4)

        Log.i(
            TAG,
            "User hasevent(${this.hasevent}), hastask(${this.hastask}), haspayment(${this.haspayment}), hasguest(${this.hasguest}), hasvendor(${this.hasvendor})"
        )
        return stepsBeanList
    }

    interface SignUpActivity {
        fun onSignUpSuccess()
        fun onSignUpError()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(userid)
        parcel.writeString(eventid)
        parcel.writeString(shortname)
        parcel.writeString(email)
        parcel.writeString(country)
        parcel.writeString(language)
        parcel.writeString(createdatetime)
        parcel.writeString(authtype)
        parcel.writeString(status)
        parcel.writeString(imageurl)
        parcel.writeString(role)
        parcel.writeString(hasevent)
        parcel.writeString(hastask)
        parcel.writeString(haspayment)
        parcel.writeString(hasguest)
        parcel.writeString(hasvendor)
        parcel.writeInt(tasksactive)
        parcel.writeInt(taskscompleted)
        parcel.writeInt(payments)
        parcel.writeInt(guests)
        parcel.writeInt(vendors)
        parcel.writeString(eventbudget)
        parcel.writeInt(numberguests)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<User> {
        const val TAG = "User"
        override fun createFromParcel(parcel: Parcel): User {
            return User(parcel)
        }

        override fun newArray(size: Int): Array<User?> {
            return arrayOfNulls(size)
        }
    }
}


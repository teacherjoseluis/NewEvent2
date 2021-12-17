package com.example.newevent2.Model

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.os.Parcel
import android.os.Parcelable
import android.provider.Settings.Global.getString
import android.util.Log
import android.widget.Toast
import com.baoyachi.stepview.bean.StepBean
import com.example.newevent2.LoginView
import com.example.newevent2.R
import com.google.firebase.auth.*

@SuppressLint("ParcelCreator")
class User(
    var key: String = "",
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
    var vendors: Int = 0
) : Parcelable {

    private val mAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private lateinit var viewLogin: LoginView

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
        parcel.readInt()
    )

    fun login(
        activity: Activity,
        authtype: String,
        UserEmail: String?,
        UserPassword: String?,
        credential: AuthCredential?,
        dataFetched: FirebaseUserId
    ) {
        when (authtype) {
            "email" -> {
                mAuth.signInWithEmailAndPassword(UserEmail!!, UserPassword!!)
                    .addOnCompleteListener(activity) { task ->
                        if (task.isSuccessful) {
                            if (mAuth.currentUser!!.isEmailVerified) {
                                Toast.makeText(
                                    activity,
                                    activity.getString(R.string.success_email_login),
                                    Toast.LENGTH_SHORT
                                ).show()
                                //Upon login pass UserId to the Presenter
                                dataFetched.onUserId(mAuth.currentUser!!.uid, UserEmail)
                            } else {
                                Toast.makeText(
                                    activity,
                                    activity.getString(R.string.notverified_email_login),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        } else {
                            try {
                                throw task.exception!!
                            } catch (e: FirebaseAuthInvalidUserException) {
                                //ERROR_USER_NOT_FOUND
                                Toast.makeText(
                                    activity,
                                    activity.getString(R.string.error_emailaccountnotfound),
                                    Toast.LENGTH_SHORT
                                ).show()
                            } catch (e: FirebaseAuthInvalidCredentialsException) {
                                //ERROR_WRONG_PASSWORD
                                Toast.makeText(
                                    activity,
                                    activity.getString(R.string.error_emailpasswordincorrect),
                                    Toast.LENGTH_SHORT
                                ).show()
                            } catch (e: Exception) {
                                Toast.makeText(
                                    activity,
                                    activity.getString(R.string.failed_email_login),
                                    //e.message, //There are several errors that can be caught at this point
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
            }
            else -> {
                mAuth.signInWithCredential(credential!!)
                    .addOnCompleteListener(activity) { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(
                                activity,
                                activity.getString(R.string.success_sn_login),
                                Toast.LENGTH_SHORT
                            ).show()
                            //Upon login pass UserId to the Presenter
                            dataFetched.onUserId(
                                mAuth.currentUser!!.uid,
                                mAuth.currentUser!!.email.toString()
                            )
                        } else {
                            try {
                                throw task.exception!!
                            } catch (e: Exception) {
                                Toast.makeText(
                                    activity,
                                    activity.getString(R.string.failure_sn_login),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
            }
        }
    }

    fun saveUserSession(activity: Activity) {
        // Clearing User Session
        activity.getSharedPreferences("USER_SESSION", Context.MODE_PRIVATE).edit().clear().apply()

        //Creating User Session
        val usersession =
            activity.getSharedPreferences("USER_SESSION", Context.MODE_PRIVATE)

        val sessionEditor = usersession!!.edit()
        sessionEditor.putString("key", key)
        sessionEditor.putString("email", email)
        sessionEditor.putString("authtype", authtype)
        sessionEditor.putString("eventid", eventid)
        sessionEditor.putString("shortname", shortname)
        sessionEditor.putString("country", country)
        sessionEditor.putString("language", language)
        sessionEditor.putString("createdatetime", createdatetime)
        sessionEditor.putString("status", status)
        sessionEditor.putString("imageurl", imageurl)
        sessionEditor.putString("role", role)
        sessionEditor.putString("hasevent", hasevent)
        sessionEditor.putString("hastask", hastask)
        sessionEditor.putString("haspayment", haspayment)
        sessionEditor.putString("hasguest", hasguest)
        sessionEditor.putString("hasvendor", hasvendor)
        sessionEditor.putInt("tasksactive", tasksactive)
        sessionEditor.putInt("taskscompleted", taskscompleted)
        sessionEditor.putInt("payments", payments)
        sessionEditor.putInt("guests", guests)
        sessionEditor.putInt("vendors", vendors)
        sessionEditor.apply()
        Log.d(TAG, "Session for User $key has been updated")
    }

    fun saveUserSession(context: Context) {
        // Clearing User Session
        context.getSharedPreferences("USER_SESSION", Context.MODE_PRIVATE).edit().clear().apply()

        //Creating User Session
        val usersession =
            context.getSharedPreferences("USER_SESSION", Context.MODE_PRIVATE)

        val sessionEditor = usersession!!.edit()
        sessionEditor.putString("key", key)
        sessionEditor.putString("email", email)
        sessionEditor.putString("authtype", authtype)
        sessionEditor.putString("eventid", eventid)
        sessionEditor.putString("shortname", shortname)
        sessionEditor.putString("country", country)
        sessionEditor.putString("language", language)
        sessionEditor.putString("createdatetime", createdatetime)
        sessionEditor.putString("status", status)
        sessionEditor.putString("imageurl", imageurl)
        sessionEditor.putString("role", role)
        sessionEditor.putString("hasevent", hasevent)
        sessionEditor.putString("hastask", hastask)
        sessionEditor.putString("haspayment", haspayment)
        sessionEditor.putString("hasguest", hasguest)
        sessionEditor.putString("hasvendor", hasvendor)
        sessionEditor.putInt("tasksactive", tasksactive)
        sessionEditor.putInt("taskscompleted", taskscompleted)
        sessionEditor.putInt("payments", payments)
        sessionEditor.putInt("guests", guests)
        sessionEditor.putInt("vendors", vendors)
        sessionEditor.apply()
        Log.d(TAG, "Session for User $key has been updated")
    }

    fun deleteUserSession(context: Context){
        context.getSharedPreferences("USER_SESSION", Context.MODE_PRIVATE).edit().clear().apply()
    }

    fun logout(activity: Activity) {
        mAuth.signOut()
        Toast.makeText(activity, activity.getString(R.string.success_logout), Toast.LENGTH_SHORT)
            .show()
    }

    fun signup(view: LoginView, activity: Activity, UserEmail: String, UserPassword: String) {
        //viewLogin = LoginView()
        viewLogin = view
        mAuth.createUserWithEmailAndPassword(UserEmail, UserPassword)
            .addOnCompleteListener(activity) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(
                        activity,
                        activity.getString(R.string.email_signup_success),
                        Toast.LENGTH_SHORT
                    ).show()
                    verifyaccount(activity)
                    viewLogin.onSignUpSuccess()
                } else {
                    viewLogin.onSignUpError()
                    try {
                        throw task.exception!!
                    } catch (e: FirebaseAuthUserCollisionException) {
                        Toast.makeText(
                            activity,
                            activity.getString(R.string.error_emailaccountexists),
                            Toast.LENGTH_SHORT
                        ).show()
                    } catch (e: Exception) {
                        Toast.makeText(
                            activity,
                            activity.getString(R.string.error_emailsignup),
                            //e.message, //There are several errors that can be caught at this point
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
    }

    private fun verifyaccount(activity: Activity) {
        val user = mAuth.currentUser
        user!!.sendEmailVerification()
            .addOnCompleteListener(activity) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(
                        activity,
                        activity.getString(R.string.success_account_verification),
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Toast.makeText(
                        activity,
                        activity.getString(R.string.failed_account_verification),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }

    fun sendpasswordreset(activity: Activity, userEmail: String) {
        mAuth.sendPasswordResetEmail(userEmail)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(
                        activity,
                        activity.getString(R.string.success_password_reset_email),
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    try {
                        throw task.exception!!
                    } catch (e: FirebaseAuthInvalidUserException) {
                        //ERROR_USER_NOT_FOUND
                        Toast.makeText(
                            activity,
                            activity.getString(R.string.error_emailaccountnotfound),
                            Toast.LENGTH_SHORT
                        ).show()
                    } catch (e: Exception) {
                        Toast.makeText(
                            activity,
                            activity.getString(R.string.failed_email_login),
                            //e.message, //There are several errors that can be caught at this point
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
    }

    fun onboardingprogress(context: Context): ArrayList<StepBean> {
        val stepsBeanList = arrayListOf<StepBean>()
        val stepBean0 = StepBean(context.getString(R.string.event), if (this.hasevent == "Y") 1 else -1)
        val stepBean1 = StepBean(context.getString(R.string.task), if (this.hastask == "Y") 1 else -1)
        val stepBean2 = StepBean(context.getString(R.string.payment), if (this.haspayment == "Y") 1 else -1)
        val stepBean3 = StepBean(context.getString(R.string.guest), if (this.hasguest == "Y") 1 else -1)
        val stepBean4 = StepBean(context.getString(R.string.vendor), if (this.hasvendor == "Y") 1 else -1)

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

    interface FirebaseUserId {
        fun onUserId(userid: String, email: String)
    }

    interface SignUpActivity {
        fun onSignUpSuccess()
        fun onSignUpError()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(key)
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


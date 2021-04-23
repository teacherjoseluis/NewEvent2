package com.example.newevent2.Model

import android.app.Activity
import android.content.Context
import android.util.Log
import android.view.View
import android.widget.Toast
import com.example.newevent2.LoginEmailView
import com.example.newevent2.LoginView
import com.example.newevent2.R
import com.facebook.login.LoginManager
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.FirebaseError.ERROR_USER_NOT_FOUND
import com.google.firebase.auth.*

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
    var hasvendor: String = ""
) {

    private val mAuth: FirebaseAuth = FirebaseAuth.getInstance()
    lateinit var viewLogin: LoginView

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
                                dataFetched.onUserId(mAuth.currentUser!!.uid, UserEmail!!)
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
        //----------------------------------------------//
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
        sessionEditor.apply()
    }

    fun logout(
        activity: Activity,
        authtype: String,
        mGoogleSignInClient: GoogleSignInClient?,
        mFacebookLoginManager: LoginManager?
    ) {
        when (authtype) {
            "google" -> {
                mGoogleSignInClient!!.signOut().addOnCompleteListener(activity) {
                }
            }
            "facebook" -> {
                mFacebookLoginManager!!.logOut()
            }
        }
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

    interface FirebaseUserId {
        fun onUserId(userid: String, email: String)
    }

    interface SignUpActivity {
        fun onSignUpSuccess()
        fun onSignUpError()
    }
}


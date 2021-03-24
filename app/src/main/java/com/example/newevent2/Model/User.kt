package com.example.newevent2.Model

import android.app.Activity
import android.content.Context
import android.util.Log
import android.widget.Toast
import com.example.newevent2.R
import com.facebook.login.LoginManager
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth

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
                if (mAuth.currentUser!!.isEmailVerified) {
                    Toast.makeText(
                        activity,
                        activity.getString(R.string.success_email_login),
                        Toast.LENGTH_SHORT
                    ).show()
                    //Upon login pass UserId to the Presenter
                    dataFetched.onUserId(mAuth.currentUser!!.uid)
                } else {
                    Toast.makeText(
                        activity,
                        activity.getString(R.string.notverified_email_login),
                        Toast.LENGTH_SHORT
                    ).show()
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
                            dataFetched.onUserId(mAuth.currentUser!!.uid)
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

    fun saveUserSession (activity: Activity){
        // Clearing User Session
        activity.getSharedPreferences("USER_SESSION", Context.MODE_PRIVATE).edit().clear().apply()

        //Creating User Session
        val usersession =
            activity.getSharedPreferences("USER_SESSION", Context.MODE_PRIVATE)

        val sessionEditor = usersession!!.edit()
        sessionEditor.putString("UID", key)
        sessionEditor.putString("Email", email)
        sessionEditor.putString("Autentication", authtype)
        sessionEditor.putString("Eventid", eventid)
        sessionEditor.putString("Shortname", shortname)
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

    fun signup(activity: Activity, UserEmail: String, UserPassword: String) {
        mAuth.createUserWithEmailAndPassword(UserEmail, UserPassword)
            .addOnCompleteListener(activity) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(activity, activity.getString(R.string.email_signup_success), Toast.LENGTH_SHORT).show()
                    verifyaccount(activity)
                } else {
                    val errorcode = task.exception!!.message
                    Toast.makeText(activity, errorcode, Toast.LENGTH_SHORT).show()
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
                    Toast.makeText(
                        activity,
                        activity.getString(R.string.failed_password_reset_email),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }

    interface FirebaseUserId {
        fun onUserId(userid: String)
    }
}


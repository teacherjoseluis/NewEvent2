package com.example.newevent2

import android.app.Activity
import android.app.Application
import android.content.Intent
import android.util.Log
import androidx.core.content.ContextCompat.startActivity
import com.facebook.AccessToken
import com.facebook.login.LoginManager
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.android.synthetic.main.login_email.*

class UserAccount(
    var UserEmail: String = ""
) {

    var UserPassword: String = ""
    private var mAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private var loginresult = false
    private var logoutresult = false

    fun checklogin(): Boolean {
        return mAuth!!.currentUser != null
    }

    fun login(
        authtype: String,
        activity: Activity,
        acct: GoogleSignInAccount?,
        fbtoken: AccessToken?
    ): Boolean {

        when (authtype) {
            "email" -> {
                mAuth!!.createUserWithEmailAndPassword(UserEmail, UserPassword)
                    .addOnCompleteListener(activity) { task ->
                        if (task.isSuccessful) {
                            Log.i("Email Login", "Successful Login: $UserEmail")
                            loginresult = true
                        } else {
                            Log.e("Email Login", "Failed Login:" + task.exception.toString())
                        }
                    }
            }
            "google" -> {
                val credential = GoogleAuthProvider.getCredential(acct!!.idToken, null)
                mAuth!!.signInWithCredential(credential)
                    .addOnCompleteListener(activity) { task ->
                        if (task.isSuccessful) {
                            //val user = mAuth!!.currentUser
                            //Need to implement functionality to save user session. Class
                            Log.i("Google Login", "Successful Login: $UserEmail")
                            loginresult = true
                        } else {
                            Log.e("Google Login", "Failed Login:" + task.exception.toString())
                        }
                    }
            }
            "facebook" -> {
                val credential = FacebookAuthProvider.getCredential(fbtoken!!.token)
                mAuth!!.signInWithCredential(credential)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            //val user = mAuth!!.currentUser
                            //Need to implement functionality to save user session. Class
                            Log.i("Facebook Login", "Successful Login: $UserEmail")
                            loginresult = true
                        } else {
                            Log.e("Facebook Login", "Failed Login:" + task.exception.toString())
                        }
                    }
            }
        }
        return loginresult
    }

    fun logout(
        authtype: String,
        activity: Activity,
        mGoogleSignInClient: GoogleSignInClient?,
        mFacebookLoginManager: LoginManager
    ): Boolean {

        mAuth!!.signOut()

        when (authtype) {
            "google" -> {
                mGoogleSignInClient!!.signOut().addOnCompleteListener(activity) {
                    Log.i("Google Login", "Successful Logout: $UserEmail")
                    logoutresult = true
                }
            }
            "facebook" -> {
                mFacebookLoginManager.logOut()
                logoutresult = true
            }
        }
        return logoutresult
    }


    fun checkuser(emailaccount: String) {

    }

    fun getuser(emailaccount: String) {

    }

    fun saveuser(emailaccount: String) {

    }
}



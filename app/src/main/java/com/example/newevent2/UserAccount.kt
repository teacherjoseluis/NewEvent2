package com.example.newevent2

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import com.facebook.AccessToken
import com.facebook.login.LoginManager
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider


class UserAccount(var UserEmail: String = "") {
    var UserPassword: String = ""
    var authtype: String = ""
    private var mAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private var logoutresult = false
    private var userexistsflag = false

    private var UserSession = User()

    fun checklogin(): Boolean {
        return mAuth.currentUser != null
    }

    fun signup(activity: Activity) {
        Log.i("signUpWithEmail", "Sign Up with Email")
        mAuth.createUserWithEmailAndPassword(UserEmail, UserPassword)
            .addOnCompleteListener(activity) { task ->
                if (task.isSuccessful) {
                    Log.i("Email Signup", "Successful Signup: $UserEmail")
                    Toast.makeText(activity, "Account created", Toast.LENGTH_SHORT).show()
                    verifyaccount(activity)
                } else {
                    Log.e("Email Signup", "Failed SignUp:" + task.exception.toString())
                    val errorcode = task.exception!!.message
                    Toast.makeText(activity, errorcode, Toast.LENGTH_SHORT).show()
                }
            }
    }

    fun login(
        activity: Activity,
        acct: GoogleSignInAccount?,
        fbtoken: AccessToken?
    ) {
        when (authtype) {
            "email" -> {
                Log.i("signInWithEmail", "Authentication with Email")
                mAuth.signInWithEmailAndPassword(UserEmail, UserPassword)
                    .addOnCompleteListener(activity) { task ->
                        if (task.isSuccessful) {
                            if (mAuth.currentUser!!.isEmailVerified) {
                                Log.i("Email Verification", "Email Verified: $UserEmail")
                                Log.i("Email Login", "Successful Login: $UserEmail")
                                //savesession(authtype, activity, UserEmail)
                                // Need to implement also the decision between going to the onboarding flow or the Welcome page
                                // Through the checkuser, I think it needs to be called through an interface to keep the logic here
                                // Logic from Login_Email
                                // Can't get out of this listener as it's a asynch call

                                val userEntity = UserEntity()
                                userEntity.key = mAuth.currentUser!!.uid
                                userEntity.getUser(object : FirebaseSuccessListenerUser {
                                    //This is to be changed for a method that obtains the information for the user into a User Session
                                    //This will be then passed as a paramter to Welcome from which all data will be available
                                    override fun onUserexists(user: com.example.newevent2.Model.User) {
                                        if (user.email != "") {
//                                            UserSession.apply {
//                                                useremail = user.email
//                                                eventid = user.eventid
//                                                shortname = user.shortname
//                                                hasevent = user.hasevent
//                                                country = user.country
//                                                language = user.language
//                                            }
                                            val welcome =
                                                Intent(activity, Welcome::class.java)
                                            //welcome.putExtra("usersession", user)
                                            activity.startActivity(welcome)
                                            activity.finish()
                                        } else {
                                            UserSession.apply {
                                                key = mAuth.currentUser!!.uid
                                                email = UserEmail
                                                authtype = authtype
                                            }
                                            // Creating the user for the moment but the idea is to to to Onboarding process
//                                            userEntity.eventid = "-MLy-LKwd8RnRb-Bwesn"
//                                            userEntity.shortname = "Jose"
//                                            userEntity.email = UserEmail
//                                            userEntity.country = "MX"
//                                            userEntity.language = "SPA"
//                                            userEntity.addUser()
                                            val onboardingname =
                                                Intent(activity, Onboarding_Name::class.java)
//                                            onboardingname.putExtra("useremail", UserEmail)
//                                            onboardingname.putExtra("userkey", userEntity.key)
                                            onboardingname.putExtra("usersession", UserSession)
                                            activity.startActivity(onboardingname)
                                        }
                                    }
                                })
                            } else {
                                Log.e("Email Verification", "Email NOT Verified: $UserEmail")
                                Toast.makeText(
                                    activity,
                                    "Verify your email account",
                                    Toast.LENGTH_SHORT
                                ).show()
                                activity.onBackPressed()
                            }
                        } else {
                            Log.e("Task Exception", task.exception.toString())
                            Log.e("Email Login", "Failed Login:" + task.exception.toString())
                        }
                    }
            }
            "google" -> {
                val credential = GoogleAuthProvider.getCredential(acct!!.idToken, null)
                mAuth.signInWithCredential(credential)
                    .addOnCompleteListener(activity) { task ->
                        if (task.isSuccessful) {
                            //val user = mAuth!!.currentUser
                            //Need to implement functionality to save user session. Class
                            Log.i("Google Login", "Successful Login: $UserEmail")
//*************************************************************************************************
// Probably this is way too nested into the code. I might need to take it out to the Login class
//**************************************************************************************************
                            val userEntity = UserEntity()
                            userEntity.key = mAuth.currentUser!!.uid
                            userEntity.getUser(object : FirebaseSuccessListenerUser {
                                override fun onUserexists(user: com.example.newevent2.Model.User) {
                                    if (user.email != "") {
//                                        UserSession.apply {
//                                            useremail = user.email
//                                            eventid = user.eventid
//                                            shortname = user.shortname
//                                            hasevent = user.hasevent
//                                            country = user.country
//                                            language = user.language
//                                        }
                                        val welcome =
                                            Intent(activity, Welcome::class.java)
                                        //welcome.putExtra("usersession", user)
                                        activity.startActivity(welcome)
                                        activity.finish()
                                    } else {
                                        UserSession.apply {
                                            key = mAuth.currentUser!!.uid
                                            email = mAuth.currentUser!!.email.toString()
                                            authtype = authtype
                                        }
                                        val onboardingname =
                                            Intent(activity, Onboarding_Name::class.java)
//                                            onboardingname.putExtra("useremail", UserEmail)
//                                            onboardingname.putExtra("userkey", userEntity.key)
                                        onboardingname.putExtra("usersession", UserSession)
                                        activity.startActivity(onboardingname)
                                    }
                                }
                            })
//****************************************************************************************************                        } else {
                            Log.e("Task Exception", task.exception.toString())
                            Log.e("Google Login", "Failed Login:" + task.exception.toString())
                        }
                    }
            }
            "facebook" -> {
                val credential = FacebookAuthProvider.getCredential(fbtoken!!.token)
                mAuth.signInWithCredential(credential)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            //val user = mAuth!!.currentUser
                            //Need to implement functionality to save user session. Class
                            Log.i("Facebook Login", "Successful Login: $UserEmail")


                            val userEntity = UserEntity()
                            userEntity.key = mAuth.currentUser!!.uid
                            userEntity.getUser(object : FirebaseSuccessListenerUser {
                                override fun onUserexists(user: com.example.newevent2.Model.User) {
                                    if (user.email != "") {
//                                        UserSession.apply {
//                                            useremail = user.email
//                                            eventid = user.eventid
//                                            shortname = user.shortname
//                                            hasevent = user.hasevent
//                                            country = user.country
//                                            language = user.language
//                                        }
                                        val welcome =
                                            Intent(activity, Welcome::class.java)
                                        //welcome.putExtra("usersession", user)
                                        activity.startActivity(welcome)
                                        activity.finish()
                                    } else {
                                        UserSession.apply {
                                            key = mAuth.currentUser!!.uid
                                            email = mAuth.currentUser!!.email.toString()
                                            authtype = authtype
                                        }
                                        val onboardingname =
                                            Intent(activity, Onboarding_Name::class.java)
//                                            onboardingname.putExtra("useremail", UserEmail)
//                                            onboardingname.putExtra("userkey", userEntity.key)
                                        onboardingname.putExtra("usersession", UserSession)
                                        activity.startActivity(onboardingname)
                                    }
                                }
                            })
                        } else {
                            Log.e("Task Exception", task.exception.toString())
                            Log.e("Facebook Login", "Failed Login:" + task.exception.toString())
                        }
                    }
            }
        }
    }

    private fun verifyaccount(activity: Activity) {
        val user = mAuth.currentUser
        user!!.sendEmailVerification()
            .addOnCompleteListener(activity) { task ->
                if (task.isSuccessful) {
                    Log.i("Email Verification", "Successful Verification: $UserEmail")
                } else {
                    Log.e("Email Verification", "Failed Verification:" + task.exception.toString())
                }
            }
    }

    fun sendpasswordreset(activity: Activity) {
        mAuth.sendPasswordResetEmail(UserEmail)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    //val user = mAuth!!.currentUser
                    //Need to implement functionality to save user session. Class
                    Log.i("Send Password Reset", "Successfully Sent: $UserEmail")
                    Toast.makeText(activity, "Verify your email account", Toast.LENGTH_SHORT).show()
                } else {
                    Log.e("Task Exception", task.exception.toString())
                    Toast.makeText(
                        activity,
                        "There was a problem with your account",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }

    fun logout(
        authtype: String,
        activity: Activity,
        mGoogleSignInClient: GoogleSignInClient?,
        mFacebookLoginManager: LoginManager
    ): Boolean {

        mAuth.signOut()

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

        if (logoutresult) {
            //deletesession(activity)
        }

        return logoutresult
    }

    private fun savesession(authtype: String, activity: Activity, UserEmail: String): Boolean {
//        var userSession = activity.getSharedPreferences("USER_SESSION", Context.MODE_PRIVATE)
        return try {
//            val sessionEditor = userSession!!.edit()
//            sessionEditor.putString("UID", mAuth.currentUser!!.uid) // UID from Firebase
//            sessionEditor.putString("Email", UserEmail)
//            sessionEditor.putString("Autentication", authtype)
//            sessionEditor.apply()
//            UserSession.uid = mAuth.currentUser!!.uid
//            UserSession.useremail = UserEmail
//            UserSession.authtype = authtype
            true
        } catch (e: Exception) {
            false
        }
    }

    private fun deletesession(activity: Activity): Boolean {
        var userSession = activity.getSharedPreferences("USER_SESSION", Context.MODE_PRIVATE)
        return try {
            userSession.edit().clear().apply()
            true
        } catch (e: Exception) {
            false
        }
    }

}



//package com.example.newevent2
//
//import android.app.Activity
//import android.content.Context
//import android.content.Intent
//import android.os.Bundle
//import android.util.Log
//import android.widget.Toast
//import androidx.appcompat.app.AppCompatActivity
//import com.facebook.*
//import com.facebook.login.LoginManager
//import com.facebook.login.LoginResult
//import com.google.android.gms.auth.api.signin.GoogleSignIn
//import com.google.android.gms.auth.api.signin.GoogleSignInAccount
//import com.google.android.gms.auth.api.signin.GoogleSignInClient
//import com.google.android.gms.auth.api.signin.GoogleSignInOptions
//import com.google.android.gms.common.api.ApiException
//import com.google.firebase.auth.FacebookAuthProvider
//import com.google.firebase.auth.FirebaseAuth
//import com.google.firebase.auth.GoogleAuthProvider
//import kotlinx.android.synthetic.main.login.*
//
//
//class Login() : AppCompatActivity() {
//
//    private lateinit var mGoogleSignInClient: GoogleSignInClient
//    private var mCallbackManager: CallbackManager? = null
//    private var mAuth: FirebaseAuth = FirebaseAuth.getInstance()
//    //private var user: UserAccount? = null
//
//    // ----------------------------------------
//    // From User Account
//    var UserEmail: String = ""
//    var UserPassword: String = ""
//    var authtype: String = ""
//    private var UserSession = User()
//    private var logoutresult = false
//    //-----------------------------------------
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        FacebookSdk.sdkInitialize(applicationContext)
//        setContentView(R.layout.login)
//
//        //user = UserAccount()
//
//        // Initialize Facebook login button
//        mCallbackManager = CallbackManager.Factory.create()
//
//        signemail.setOnClickListener {
//
//            var inputvalflag = true
//            if (mailinputeditlogin.text.toString().isEmpty()) {
//                mailinputeditlogin.error = "Email is required!"
//                inputvalflag = false
//            }
//
//            if (inputvalflag) {
//                val loginemail = Intent(this, Login_Email::class.java)
//                loginemail.putExtra("email", mailinputeditlogin.text.toString())
//                startActivity(loginemail)
//            }
//        }
//
//        signgoogle.setOnClickListener {
//            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//                .requestIdToken(getString(R.string.default_web_client_id))
//                .requestEmail()
//                .build()
//
//            mGoogleSignInClient = GoogleSignIn.getClient(this, gso)
//
//            val signInIntent = mGoogleSignInClient.signInIntent
//            startActivityForResult(signInIntent, RC_SIGN_IN)
//        }
//
//        signfacebook.setOnClickListener {
//            LoginManager.getInstance().logInWithReadPermissions(this, listOf("email"))
//            LoginManager.getInstance()
//                .registerCallback(mCallbackManager, object : FacebookCallback<LoginResult> {
//                    override fun onSuccess(result: LoginResult?) {
//                        Log.d(TAGF, "facebook:onSuccess:$result")
//                        val token = result!!.accessToken
//                        authtype = "facebook"
//                        login(this@Login, null, token)
//                    }
//
//                    override fun onCancel() {
//                        Log.d(TAGF, "facebook:onCancel")
//                    }
//
//                    override fun onError(error: FacebookException?) {
//                        Log.d(TAGF, "facebook:onError", error)
//                    }
//
//                })
//        }
//
//        signemaillink.setOnClickListener {
//            val loginemail = Intent(this, Login_Email::class.java)
//            loginemail.putExtra("email", "")
//            startActivity(loginemail)
//        }
//    }
//
//    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        mCallbackManager!!.onActivityResult(requestCode, resultCode, data)
//        // Pass the activity result back to the Facebook SDK
//        super.onActivityResult(requestCode, resultCode, data)
//
//        if (requestCode == RC_SIGN_IN) {
//            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
//            try {
//                val account = task.getResult(ApiException::class.java)
//                authtype = "google"
//                login(this, account, null)
//            } catch (e: ApiException) {
//                Log.w(TAG, "Google sign in failed", e)
//            }
//        }
//    }
//
//    companion object {
//        private val TAG = "GoogleActivity"
//        private val TAGF = "FacebookLogin"
//        private val RC_SIGN_IN = 9001
//    }
//
//    //--------------------------------------------------------------------------------------------------------
//    // From User Account
//
//    fun signup(activity: Activity) {
//        Log.i("signUpWithEmail", "Sign Up with Email")
//        mAuth.createUserWithEmailAndPassword(UserEmail, UserPassword)
//            .addOnCompleteListener(activity) { task ->
//                if (task.isSuccessful) {
//                    Log.i("Email Signup", "Successful Signup: $UserEmail")
//                    Toast.makeText(activity, "Account created", Toast.LENGTH_SHORT).show()
//                    verifyaccount(activity)
//                } else {
//                    Log.e("Email Signup", "Failed SignUp:" + task.exception.toString())
//                    val errorcode = task.exception!!.message
//                    Toast.makeText(activity, errorcode, Toast.LENGTH_SHORT).show()
//                }
//            }
//    }
//
//    fun login(
//        activity: Activity,
//        acct: GoogleSignInAccount?,
//        fbtoken: AccessToken?
//    ) {
//        when (authtype) {
//            "email" -> {
//                Log.i("signInWithEmail", "Authentication with Email")
//                mAuth.signInWithEmailAndPassword(UserEmail, UserPassword)
//                    .addOnCompleteListener(activity) { task ->
//                        if (task.isSuccessful) {
//                            if (mAuth.currentUser!!.isEmailVerified) {
//                                Log.i("Email Verification", "Email Verified: $UserEmail")
//                                Log.i("Email Login", "Successful Login: $UserEmail")
//                                //savesession(authtype, activity, UserEmail)
//                                // Need to implement also the decision between going to the onboarding flow or the Welcome page
//                                // Through the checkuser, I think it needs to be called through an interface to keep the logic here
//                                // Logic from Login_Email
//                                // Can't get out of this listener as it's a asynch call
//
//                                val userEntity = UserEntity()
//                                userEntity.key = mAuth.currentUser!!.uid
//                                userEntity.getUser(object : FirebaseSuccessListenerUser {
//                                    //This is to be changed for a method that obtains the information for the user into a User Session
//                                    //This will be then passed as a paramter to Welcome from which all data will be available
//                                    override fun onUserexists(user: com.example.newevent2.Model.User) {
//                                        if (user.email != "") {
////                                            UserSession.apply {
////                                                useremail = user.email
////                                                eventid = user.eventid
////                                                shortname = user.shortname
////                                                hasevent = user.hasevent
////                                                country = user.country
////                                                language = user.language
////                                            }
//                                            val welcome =
//                                                Intent(activity, Welcome::class.java)
//                                            //welcome.putExtra("usersession", user)
//                                            activity.startActivity(welcome)
//                                            activity.finish()
//                                        } else {
//                                            UserSession.apply {
//                                                key = mAuth.currentUser!!.uid
//                                                email = UserEmail
//                                                authtype = authtype
//                                            }
//                                            // Creating the user for the moment but the idea is to to to Onboarding process
////                                            userEntity.eventid = "-MLy-LKwd8RnRb-Bwesn"
////                                            userEntity.shortname = "Jose"
////                                            userEntity.email = UserEmail
////                                            userEntity.country = "MX"
////                                            userEntity.language = "SPA"
////                                            userEntity.addUser()
//                                            val onboardingname =
//                                                Intent(activity, Onboarding_Name::class.java)
////                                            onboardingname.putExtra("useremail", UserEmail)
////                                            onboardingname.putExtra("userkey", userEntity.key)
//                                            onboardingname.putExtra("usersession", UserSession)
//                                            activity.startActivity(onboardingname)
//                                        }
//                                    }
//                                })
//                            } else {
//                                Log.e("Email Verification", "Email NOT Verified: $UserEmail")
//                                Toast.makeText(
//                                    activity,
//                                    "Verify your email account",
//                                    Toast.LENGTH_SHORT
//                                ).show()
//                                activity.onBackPressed()
//                            }
//                        } else {
//                            Log.e("Task Exception", task.exception.toString())
//                            Log.e("Email Login", "Failed Login:" + task.exception.toString())
//                        }
//                    }
//            }
//            "google" -> {
//                val credential = GoogleAuthProvider.getCredential(acct!!.idToken, null)
//                mAuth.signInWithCredential(credential)
//                    .addOnCompleteListener(activity) { task ->
//                        if (task.isSuccessful) {
//                            //val user = mAuth!!.currentUser
//                            //Need to implement functionality to save user session. Class
//                            Log.i("Google Login", "Successful Login: $UserEmail")
////*************************************************************************************************
//// Probably this is way too nested into the code. I might need to take it out to the Login class
////**************************************************************************************************
//                            val userEntity = UserEntity()
//                            userEntity.key = mAuth.currentUser!!.uid
//                            userEntity.getUser(object : FirebaseSuccessListenerUser {
//                                override fun onUserexists(user: com.example.newevent2.Model.User) {
//                                    if (user.email != "") {
////                                        UserSession.apply {
////                                            useremail = user.email
////                                            eventid = user.eventid
////                                            shortname = user.shortname
////                                            hasevent = user.hasevent
////                                            country = user.country
////                                            language = user.language
////                                        }
//                                        // Create user session
//                                        var userlocalsession =
//                                            applicationContext.getSharedPreferences("USER_SESSION", Context.MODE_PRIVATE)
//
//                                        val sessionEditor = userlocalsession!!.edit()
//                                        sessionEditor.putString("UID", userEntity.key) // UID from Firebase
//                                        sessionEditor.putString("Email", user!!.email)
//                                        sessionEditor.putString("Autentication", user!!.authtype)
//                                        sessionEditor.putString("Eventid", user!!.eventid)
//                                        sessionEditor.putString("Shortname", user!!.shortname)
//                                        sessionEditor.apply()
//
//                                        val welcome =
//                                            Intent(applicationContext, Welcome::class.java)
//                                        //welcome.putExtra("usersession", user)
//                                        welcome.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
//                                        startActivity(welcome)
//                                        Log.d("Activity Starts", "Welcome")
//                                        finish()
//                                        Log.d("Activity Ends", "Welcome")
//                                    } else {
//                                        UserSession.apply {
//                                            key = mAuth.currentUser!!.uid
//                                            email = mAuth.currentUser!!.email.toString()
//                                            authtype = authtype
//                                        }
//                                        val onboardingname =
//                                            Intent(activity, Onboarding_Name::class.java)
////                                            onboardingname.putExtra("useremail", UserEmail)
////                                            onboardingname.putExtra("userkey", userEntity.key)
//                                        onboardingname.putExtra("usersession", UserSession)
//                                        activity.startActivity(onboardingname)
//                                    }
//                                }
//                            })
////****************************************************************************************************                        } else {
//                            Log.e("Task Exception", task.exception.toString())
//                            Log.e("Google Login", "Failed Login:" + task.exception.toString())
//                        }
//                    }
//            }
//            "facebook" -> {
//                val credential = FacebookAuthProvider.getCredential(fbtoken!!.token)
//                mAuth.signInWithCredential(credential)
//                    .addOnCompleteListener { task ->
//                        if (task.isSuccessful) {
//                            //val user = mAuth!!.currentUser
//                            //Need to implement functionality to save user session. Class
//                            Log.i("Facebook Login", "Successful Login: $UserEmail")
//
//
//                            val userEntity = UserEntity()
//                            userEntity.key = mAuth.currentUser!!.uid
//                            userEntity.getUser(object : FirebaseSuccessListenerUser {
//                                override fun onUserexists(user: com.example.newevent2.Model.User) {
//                                    if (user.email != "") {
////                                        UserSession.apply {
////                                            useremail = user.email
////                                            eventid = user.eventid
////                                            shortname = user.shortname
////                                            hasevent = user.hasevent
////                                            country = user.country
////                                            language = user.language
////                                        }
//                                        val welcome =
//                                            Intent(activity, Welcome::class.java)
//                                        //welcome.putExtra("usersession", user)
//                                        activity.startActivity(welcome)
//                                        activity.finish()
//                                    } else {
//                                        UserSession.apply {
//                                            key = mAuth.currentUser!!.uid
//                                            email = mAuth.currentUser!!.email.toString()
//                                            authtype = authtype
//                                        }
//                                        val onboardingname =
//                                            Intent(activity, Onboarding_Name::class.java)
////                                            onboardingname.putExtra("useremail", UserEmail)
////                                            onboardingname.putExtra("userkey", userEntity.key)
//                                        onboardingname.putExtra("usersession", UserSession)
//                                        activity.startActivity(onboardingname)
//                                    }
//                                }
//                            })
//                        } else {
//                            Log.e("Task Exception", task.exception.toString())
//                            Log.e("Facebook Login", "Failed Login:" + task.exception.toString())
//                        }
//                    }
//            }
//        }
//    }
//
//    private fun verifyaccount(activity: Activity) {
//        val user = mAuth.currentUser
//        user!!.sendEmailVerification()
//            .addOnCompleteListener(activity) { task ->
//                if (task.isSuccessful) {
//                    Log.i("Email Verification", "Successful Verification: $UserEmail")
//                } else {
//                    Log.e("Email Verification", "Failed Verification:" + task.exception.toString())
//                }
//            }
//    }
//
//    fun sendpasswordreset(activity: Activity) {
//        mAuth.sendPasswordResetEmail(UserEmail)
//            .addOnCompleteListener { task ->
//                if (task.isSuccessful) {
//                    //val user = mAuth!!.currentUser
//                    //Need to implement functionality to save user session. Class
//                    Log.i("Send Password Reset", "Successfully Sent: $UserEmail")
//                    Toast.makeText(activity, "Verify your email account", Toast.LENGTH_SHORT).show()
//                } else {
//                    Log.e("Task Exception", task.exception.toString())
//                    Toast.makeText(
//                        activity,
//                        "There was a problem with your account",
//                        Toast.LENGTH_SHORT
//                    ).show()
//                }
//            }
//    }
//
//    fun logout(
//        authtype: String,
//        activity: Activity,
//        mGoogleSignInClient: GoogleSignInClient?,
//        mFacebookLoginManager: LoginManager
//    ): Boolean {
//
//        mAuth.signOut()
//
//        when (authtype) {
//            "google" -> {
//                mGoogleSignInClient!!.signOut().addOnCompleteListener(activity) {
//                    Log.i("Google Login", "Successful Logout: $UserEmail")
//                    logoutresult = true
//                }
//            }
//            "facebook" -> {
//                mFacebookLoginManager.logOut()
//                logoutresult = true
//            }
//        }
//
//        if (logoutresult) {
//            //deletesession(activity)
//        }
//
//        return logoutresult
//    }
//
//    //--------------------------------------------------------------------------------------------------------
//}
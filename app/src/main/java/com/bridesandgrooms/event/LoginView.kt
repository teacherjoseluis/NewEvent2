package com.bridesandgrooms.event

import Application.*
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.lifecycleScope
import com.bridesandgrooms.event.Functions.getUserSession
import com.bridesandgrooms.event.Functions.saveUserSession
import com.bridesandgrooms.event.MVP.LoginPresenter
import com.bridesandgrooms.event.Model.*
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.*
import kotlinx.android.synthetic.main.login0.*
import kotlinx.android.synthetic.main.login0.editPasswordlogin
import kotlinx.android.synthetic.main.login0.forgotemaillink
import kotlinx.android.synthetic.main.login_email.*
import kotlinx.coroutines.*
import java.util.regex.Matcher
import java.util.regex.Pattern


class LoginView : AppCompatActivity(), LoginPresenter.ViewLoginActivity, User.SignUpActivity {

    private val TIME_DELAY = 2000
    private var back_pressed: Long = 0
    val user = User()

    private lateinit var authResult: AuthResult

    //@OptIn(ExperimentalCoroutinesApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login0)

        // Facebook Initializations
//        FacebookSdk.setApplicationId("420436362323049")
//        FacebookSdk.setClientToken("2d0cd88a18ebf54c93cc70d64edfcddc")
//        FacebookSdk.sdkInitialize(applicationContext)
//        mCallbackManager = CallbackManager.Factory.create()

        val user = User()

        // Initial login layout is displayed while the others are hidden
        frame2.visibility = ConstraintLayout.INVISIBLE
        frame3.visibility = ConstraintLayout.INVISIBLE

        loginbuttonstart.setOnClickListener {
            //Maybe this is a good moment to implement an animation for the transition
            //Login layout becomes visible
            frame1.visibility = ConstraintLayout.INVISIBLE
            frame2.visibility = ConstraintLayout.VISIBLE

            loginbutton.setOnClickListener {
                var inputvalflag = true
                if (editEmaillogin.text.toString().isEmpty()) {
                    editEmaillogin.error = getString(R.string.error_valid_emailaccount)
                    inputvalflag = false
                }
                if (editPasswordlogin.text.toString()
                        .isEmpty() || editPasswordlogin.text.toString().length < 8 || !isValidPassword(
                        editPasswordlogin.text.toString()
                    )
                ) {
                    editPasswordlogin.error =
                        getString(R.string.password_requiredformat)
                    inputvalflag = false
                }
                if (inputvalflag) {
                    val userEmail = editEmaillogin.text.toString()
                    val userPassword = editPasswordlogin.text.toString()

                    lifecycleScope.launchWhenResumed {
                        try {
                            authResult =
                                user.login(this@LoginView, "email", userEmail, userPassword, null)

                            val firebaseUser = authResult.user
                            val eventId = getUserSession(this@LoginView, "event_id")
                            if (eventId == "") {
                                withContext(Dispatchers.Main) {
                                    val onboardActivity =
                                        Intent(this@LoginView, OnboardingView::class.java)
                                    onboardActivity.putExtra("userid", firebaseUser!!.uid)
                                    onboardActivity.putExtra("email", firebaseUser.email!!)
                                    onboardActivity.putExtra("authtype", "email")
                                    startActivity(onboardActivity)
                                    delay(1000)
//                                finish()
//                                overridePendingTransition(
//                                    android.R.anim.slide_out_right,
//                                    android.R.anim.slide_in_left
//                                )
                                }
                            } else {
                                val dbHelper = DatabaseHelper(this@LoginView)
                                dbHelper.updateLocalDB(firebaseUser!!.uid)
                            }
                            onLoginSuccess(firebaseUser!!.email!!)

                        } catch (e: EmailVerificationException) {
                            displayErrorMsg(getString(R.string.error_emailverification)/* + e.toString()*/)
                        } catch (e: UserAuthenticationException) {
                            displayErrorMsg(getString(R.string.failed_email_login)/* + e.toString()*/)
                        } catch (e: SessionAccessException) {
                            displayErrorMsg(getString(R.string.error_usersession)/* + e.toString()*/)
                        } catch (e: ExistingSessionException) {
                            displayErrorMsg(getString(R.string.error_usersession)/* + e.toString()*/)
                        } catch (e: NetworkConnectivityException) {
                            displayErrorMsg(getString(R.string.error_networkconnectivity)/* + e.toString()*/)
                        }
                    }
                }
            }

            signuplink.setOnClickListener {
                frame2.visibility = ConstraintLayout.INVISIBLE
                frame3.visibility = ConstraintLayout.VISIBLE
            }

// Google Sign In
            signgoogle.setOnClickListener {
                val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken("319634884697-ihokd8d4om17tsanagl74ife42c5n68f.apps.googleusercontent.com")
                    .requestEmail()
                    .build()

                val mGoogleSignInClient = GoogleSignIn.getClient(this, gso)
                val signInIntent = mGoogleSignInClient.signInIntent
                startActivityForResult(signInIntent, RC_SIGN_IN)
            }

// Facebook Sign In
//            signfacebook.setPermissions("email", "public_profile")
//            signfacebook.setOnClickListener {
////                LoginManager.getInstance().logInWithReadPermissions(this, listOf("email"))
////                LoginManager.getInstance()
////                    .registerCallback(mCallbackManager, object : FacebookCallback<LoginResult> {
////                        override fun onSuccess(result: LoginResult?) {
////                            val fbtoken = result!!.accessToken
////                            val credential = FacebookAuthProvider.getCredential(fbtoken.token)
////                            lifecycleScope.launchWhenResumed {
////                                val authResult =
////                                    user.login(
////                                        this@LoginView,
////                                        "facebook",
////                                        null,
////                                        null,
////                                        credential
////                                    )
////                                //------------------------------------------------------
////                                val firebaseUser = authResult.user
////                                //------------------------------------------------------
////                                val eventId = getUserSession(this@LoginView, "event_id")
////                                if (eventId == "") {
////                                    withContext(Dispatchers.Main) {
////                                        val onboardActivity =
////                                            Intent(this@LoginView, OnboardingView::class.java)
////                                        onboardActivity.putExtra("userid", firebaseUser!!.uid)
////                                        onboardActivity.putExtra("email", firebaseUser.email!!)
////                                        onboardActivity.putExtra("authtype", "facebook")
////                                        startActivity(onboardActivity)
////                                        delay(1000)
//////                                finish()
//////                                overridePendingTransition(
//////                                    android.R.anim.slide_out_right,
//////                                    android.R.anim.slide_in_left
//////                                )
////                                    }
////                                } else {
////                                    val dbHelper = DatabaseHelper(this@LoginView)
////                                    dbHelper.updateLocalDB(firebaseUser!!.uid)
////                                }
////                                onLoginSuccess(firebaseUser!!.email!!)
////
////                            }
////                        }
////
////                        override fun onCancel() {
////                            Log.d(TAGF, "facebook:onCancel")
////                        }
////
////                        override fun onError(error: FacebookException?) {
////                            Log.d(TAGF, "facebook:onError", error)
////                        }
////                    })
////                val loginManager = LoginManager.getInstance()
////                val callbackManager = CallbackManager.Factory.create()
//
//                // Call the registerCallback method on the LoginManager instance
//                loginManager.registerCallback(
//                    callbackManager,
//                    object : FacebookCallback<LoginResult> {
//                        override fun onSuccess(result: LoginResult) {
//                            result?.accessToken?.let { accessToken ->
//                                // Handle login success
//                                // You can access the user's access token with accessToken
//                                val credential =
//                                    FacebookAuthProvider.getCredential(accessToken.token)
//                                lifecycleScope.launchWhenResumed {
//                                    val authResult =
//                                        user.login(
//                                            this@LoginView,
//                                            "facebook",
//                                            null,
//                                            null,
//                                            credential
//                                        )
//                                    //------------------------------------------------------
//                                    val firebaseUser = authResult.user
//                                    //------------------------------------------------------
//                                    val eventId = getUserSession(this@LoginView, "event_id")
//                                    if (eventId == "") {
//                                        withContext(Dispatchers.Main) {
//                                            val onboardActivity =
//                                                Intent(
//                                                    this@LoginView,
//                                                    OnboardingView::class.java
//                                                )
//                                            onboardActivity.putExtra(
//                                                "userid",
//                                                firebaseUser!!.uid
//                                            )
//                                            onboardActivity.putExtra(
//                                                "email",
//                                                firebaseUser.email!!
//                                            )
//                                            onboardActivity.putExtra("authtype", "facebook")
//                                            startActivity(onboardActivity)
//                                            delay(1000)
//                                        }
//                                    } else {
//                                        val dbHelper = DatabaseHelper(this@LoginView)
//                                        dbHelper.updateLocalDB(firebaseUser!!.uid)
//                                    }
//                                    onLoginSuccess(firebaseUser!!.email!!)
////
////                            }
//                                }
//                            }
//                        }
//
//                        override fun onCancel() {
//                            // Handle login cancellation
//                        }
//
//                        override fun onError(error: FacebookException) {
//                            TODO("Not yet implemented")
//                        }
//                    })
//
//            }
        }

        signupbuttonstart.setOnClickListener {
            //Maybe this is a good moment to implement an animation for the transition
            //Signup layout becomes visible
            frame1.visibility = ConstraintLayout.INVISIBLE
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            frame3.visibility = ConstraintLayout.VISIBLE

            signupbutton.setOnClickListener {
                var inputvalflag = true

                if (editEmailsignup.text.toString().isEmpty()) {
                    editEmailsignup.error = getString(R.string.error_valid_emailaccount)
                    inputvalflag = false
                }
                if (editPasswordsignup1.text.toString()
                        .isEmpty() || editPasswordsignup1.text.toString().length < 8 || !isValidPassword(
                        editPasswordsignup1.text.toString()
                    )
                ) {
                    editPasswordsignup1.error =
                        getString(R.string.password_requiredformat)
                    inputvalflag = false
                }
                if (editPasswordsignup2.text.toString()
                        .isEmpty() && editPasswordsignup2.text != editPasswordsignup1.text
                ) {
                    editPasswordsignup2.error = getString(R.string.passwords_dontmatch)
                    inputvalflag = false
                }
                if (inputvalflag) {
                    val userEmail = editEmailsignup.text.toString()
                    val userPassword = editPasswordsignup1.text.toString()

                    lifecycleScope.launch {
                        try {
                            val signUpResult = user.signup(userEmail, userPassword)
                            if (signUpResult) {
                                //onSignUpSuccess()
                                Toast.makeText(
                                    this@LoginView,
                                    getString(R.string.email_signup_success),
                                    Toast.LENGTH_SHORT
                                ).show()
                                Toast.makeText(
                                    this@LoginView,
                                    getString(R.string.success_account_verification),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        } catch (e: FirebaseAuthUserCollisionException) {
                            displayErrorMsg(getString(R.string.error_emailaccountexists) + e.toString())
                        } catch (e: FirebaseAuthWeakPasswordException) {
                            displayErrorMsg(getString(R.string.weakpassword) + e.toString())
                        } catch (e: FirebaseAuthInvalidCredentialsException) {
                            displayErrorMsg(getString(R.string.invalidemailpassword) + e.toString())
                        } catch (e: FirebaseAuthEmailException) {
                            displayErrorMsg(getString(R.string.errorsendingverification) + e.toString())
                        } catch (e: Exception) {
                            displayErrorMsg(getString(R.string.error_emailsignup) + e.toString())
                        }
                    }
                    //user.signup(this, this, userEmail, userPassword)
                    //onBackPressed()
                    logoffapp()
                }
            }

            loginlink.setOnClickListener {
                frame2.visibility = ConstraintLayout.VISIBLE
                frame3.visibility = ConstraintLayout.INVISIBLE
            }
        }

        forgotemaillink.setOnClickListener {
            var inputvalflag = true
            if (editEmaillogin.text.toString().isEmpty()) {
                editEmaillogin.error = getString(R.string.error_valid_emailaccount)
                inputvalflag = false
            }
            if (inputvalflag) {
                val userEmail = editEmaillogin.text.toString()
                val user = User()
                lifecycleScope.launch {
                    try {
                        user.sendPasswordReset(userEmail)
                    } catch (e: FirebaseAuthInvalidUserException) {
                        displayErrorMsg(getString(R.string.resetpwderror_authinvaliduser) + e.toString())
                    } catch (e: FirebaseAuthInvalidCredentialsException) {
                        displayErrorMsg(getString(R.string.resetpwderror_invalidcredentials) + e.toString())
                    } catch (e: FirebaseAuthEmailException) {
                        displayErrorMsg(getString(R.string.resetpwderror_authmailexception) + e.toString())
                    } catch (e: Exception) {
                        displayErrorMsg(getString(R.string.resetpwderror_authmailexception) + e.toString())
                    }
                }
            }
        }

    }

    //override fun onDestroy() {
    //super.onDestroy()
    //scope.cancel()
    //}

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        mCallbackManager.onActivityResult(requestCode, resultCode, data)
        // Pass the activity result back to the Facebook SDK
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                val credential = GoogleAuthProvider.getCredential(account!!.idToken, null)

                lifecycleScope.launchWhenResumed {
                    val authResult =
                        user.login(
                            this@LoginView,
                            "google",
                            null,
                            null,
                            credential
                        )
                    //------------------------------------------------------
                    val firebaseUser = authResult.user
                    //------------------------------------------------------
                    if (firebaseUser != null) {
                        val eventId = getUserSession(this@LoginView, "event_id")
                        if (eventId == "") {
//                            onOnboarding(
//                                firebaseUser.uid,
//                                firebaseUser.email!!,
//                                "google"
//                            )

                            withContext(Dispatchers.Main) {
                                val onboardActivity =
                                    Intent(this@LoginView, OnboardingView::class.java)
                                onboardActivity.putExtra("userid", firebaseUser.uid)
                                onboardActivity.putExtra("email", firebaseUser.email!!)
                                onboardActivity.putExtra("authtype", "google")
                                startActivity(onboardActivity)
                                delay(1000)
//                                finish()
//                                overridePendingTransition(
//                                    android.R.anim.slide_out_right,
//                                    android.R.anim.slide_in_left
//                                )
                            }

                        } else {
                            val dbHelper = DatabaseHelper(this@LoginView)
                            dbHelper.updateLocalDB(firebaseUser.uid)
                        }



                        onLoginSuccess(firebaseUser.email!!)
                    }
                }

//                lifecycleScope.launch {
//                    var authResult =
//                        user.login(this@LoginView, "google", null, null, credential)
//
//                    val firebaseUser = authResult.user
//                    uid = firebaseUser!!.uid
//                    email = firebaseUser.email!!
//                    //------------------------------------------------------
//                    userAccount = UserDBHelper(this@LoginView).getUser(firebaseUser!!.uid)
//                    //------------------------------------------------------
//                    if (userAccount.email == "") {
//                        userAccount = UserModel(firebaseUser!!.uid).getUser()
//                        if (userAccount == null) {
//                            onOnboarding(uid, email, "google")
//                        } else {
//                            ////------------------------- Read remote DB and dump into local
//                            UserDBHelper(this@LoginView).insert(userAccount)
//                            val event = EventModel()
//                            event.getEventdetail(
//                                user.userid!!,
//                                user.eventid,
//                                object : EventModel.FirebaseSuccessListenerEventDetail {
//                                    @RequiresApi(Build.VERSION_CODES.O)
//                                    override fun onEvent(event: Event) {
//                                        EventDBHelper(this@LoginView).insert(event)
//                                    }
//                                }
//                            )
//                            val task = TaskModel()
//                            task.getAllTasksList(
//                                user.userid!!,
//                                user.eventid,
//                                object : TaskModel.FirebaseSuccessTaskList {
//                                    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
//                                    override fun onTaskList(arrayList: ArrayList<Task>) {
//                                        if (arrayList.isNotEmpty()) {
//                                            // This may be heavy, getting all of the tasks from Firebase but storing them into the cache
//                                            for (taskitem in arrayList) {
//                                                TaskDBHelper(this@LoginView).insert(taskitem)
//                                            }
//                                        }
//                                    }
//                                })
//                            val payment = PaymentModel()
//                            payment.getPaymentsList(
//                                user.userid!!,
//                                user.eventid,
//                                object : PaymentModel.FirebaseSuccessPaymentList {
//                                    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
//                                    override fun onPaymentList(arrayList: ArrayList<Payment>) {
//                                        for (paymentitem in arrayList) {
//                                            PaymentDBHelper(this@LoginView).insert(paymentitem)
//                                        }
//                                    }
//                                })
//                            val guest = GuestModel()
//                            guest.getAllGuestList(
//                                user.userid!!,
//                                user.eventid,
//                                object : GuestModel.FirebaseSuccessGuestList {
//                                    @RequiresApi(Build.VERSION_CODES.O)
//                                    override fun onGuestList(list: ArrayList<Guest>) {
//                                        if (list.isNotEmpty()) {
//                                            // This may be heavy, getting all of the tasks from Firebase but storing them into the cache
//                                            for (guestitem in list) {
//                                                GuestDBHelper(this@LoginView).insert(guestitem)
//                                            }
//                                        }
//                                    }
//                                }
//                            )
//                            val vendor = VendorModel()
//                            vendor.getAllVendorList(
//                                user.userid!!,
//                                user.eventid,
//                                object : VendorModel.FirebaseSuccessVendorList {
//                                    @RequiresApi(Build.VERSION_CODES.O)
//                                    override fun onVendorList(list: ArrayList<Vendor>) {
//                                        if (list.isNotEmpty()) {
//                                            // This may be heavy, getting all of the tasks from Firebase but storing them into the cache
//                                            for (vendoritem in list) {
//                                                VendorDBHelper(this@LoginView).insert(vendoritem)
//                                            }
//                                        }
//                                    }
//                                }
//                            )
//                            onLoginSuccess(userAccount.email)
//                        }
//                    } else {
//                        onLoginSuccess(userAccount.email)
//                    }
//
////                    if (uid != "" && email != "") {
////                        if (userAccount.email == "") {
////                            onOnboarding(uid, email, "google")
////                        } else {
////                            //saveUserSession(this@LoginView, email)
////                            UserModel(uid).getUser()
////                            onLoginSuccess()
////                        }
////                    }
//                }
            } catch (e: ApiException) {
                Log.w(TAG, "Google sign in failed", e)
            }
        }
//        else { //Safe to assume that we are talking about Facebook authentication?
//            try {
//                val accessToken = AccessToken.getCurrentAccessToken()
//                val isLoggedIn = accessToken != null && !accessToken.isExpired
//                if (isLoggedIn) {
//                    val credential = FacebookAuthProvider.getCredential(accessToken!!.token)
//                    lifecycleScope.launchWhenResumed {
//                        val authResult =
//                            user.login(
//                                this@LoginView,
//                                "facebook",
//                                null,
//                                null,
//                                credential
//                            )
//                        //------------------------------------------------------
//                        val firebaseUser = authResult.user
//                        //------------------------------------------------------
//                        val eventId = getUserSession(this@LoginView, "event_id")
//                        if (eventId == "") {
//                            withContext(Dispatchers.Main) {
//                                val onboardActivity =
//                                    Intent(this@LoginView, OnboardingView::class.java)
//                                onboardActivity.putExtra("userid", firebaseUser!!.uid)
//                                onboardActivity.putExtra("email", firebaseUser.email!!)
//                                onboardActivity.putExtra("authtype", "facebook")
//                                startActivity(onboardActivity)
//                                delay(1000)
////                                finish()
////                                overridePendingTransition(
////                                    android.R.anim.slide_out_right,
////                                    android.R.anim.slide_in_left
////                                )
//                            }
//                        } else {
//                            val dbHelper = DatabaseHelper(this@LoginView)
//                            dbHelper.updateLocalDB(firebaseUser!!.uid)
//                        }
//                        onLoginSuccess(firebaseUser!!.email!!)
////
////                            }
//                    }
//                }
//            } catch (e: ApiException) {
//                Log.w(TAG, "Google sign in failed", e)
//            }
//        }
    }

    private fun logoffapp() {
        // Dialog
        val builder = android.app.AlertDialog.Builder(this)
        builder.setTitle(getString(R.string.emailverification_title))
        builder.setMessage(getString(R.string.emailverification_message))

        builder.setPositiveButton(
            getString(R.string.accept)
        ) { _, _ ->
            finish()
        }
//        builder.setNegativeButton(
//            "Cancel"
//        ) { p0, _ -> p0!!.dismiss() }
        val dialog = builder.create()
        dialog.show()
    }

    companion object {
        private const val TAG = "GoogleActivity"
        private const val RC_SIGN_IN = 9001
    }

    override fun onLoginSuccess(email: String) {
        Toast.makeText(
            this,
            getString(R.string.welcome_message),
            Toast.LENGTH_SHORT
        ).show()
        saveUserSession(applicationContext, email, null, "email")
        finish()
        val mainactivity =
            Intent(this, ActivityContainer::class.java)
        startActivity(mainactivity)
    }

    override fun onOnboarding(userid: String, email: String, authtype: String) {
        Toast.makeText(
            this,
            getString(R.string.onboarding_message),
            Toast.LENGTH_SHORT
        ).show()

        val onboarding =
            Intent(this@LoginView, OnboardingView::class.java)
        onboarding.putExtra("userid", userid)
        onboarding.putExtra("email", email)
        onboarding.putExtra("authtype", authtype)
        finish()
        startActivity(onboarding)
        overridePendingTransition(android.R.anim.slide_out_right, android.R.anim.slide_in_left)
    }

    override fun onLoginError() {
        Toast.makeText(
            this,
            getString(R.string.login_error_message),
            Toast.LENGTH_SHORT
        ).show()
        frame1.visibility = ConstraintLayout.VISIBLE
        frame2.visibility = ConstraintLayout.INVISIBLE
    }

    private fun isValidPassword(password: String?): Boolean {
        val pattern: Pattern
        val PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{4,}$"
        pattern = Pattern.compile(PASSWORD_PATTERN)
        val matcher: Matcher = pattern.matcher(password)
        return matcher.matches()
    }

    override fun onSignUpSuccess() {
        frame3.visibility = ConstraintLayout.INVISIBLE
        frame2.visibility = ConstraintLayout.VISIBLE
    }

    override fun onSignUpError() {
        frame3.visibility = ConstraintLayout.INVISIBLE
        frame1.visibility = ConstraintLayout.VISIBLE
    }

    override fun onBackPressed() {
        if (back_pressed + TIME_DELAY > System.currentTimeMillis()) {
            super.onBackPressed()
            finish()
        } else {
            Toast.makeText(
                baseContext, getString(R.string.pressexit),
                Toast.LENGTH_SHORT
            ).show()
        }
        back_pressed = System.currentTimeMillis()
    }

    private fun displayErrorMsg(message: String) {
        Toast.makeText(
            this@LoginView,
            message,
            Toast.LENGTH_LONG
        ).show()
    }
}

interface OnBackPressedLoginViewListener
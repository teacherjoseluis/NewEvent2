package com.example.newevent2

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.newevent2.MVP.LoginPresenter
import com.facebook.*
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.android.synthetic.main.login.*


class LoginView() : AppCompatActivity(), LoginPresenter.ViewLoginActivity {

    //private lateinit var mGoogleSignInClient: GoogleSignInClient
    private var mCallbackManager: CallbackManager? = null
    //private var mAuth: FirebaseAuth = FirebaseAuth.getInstance()
    //private var user: UserAccount? = null

    // ----------------------------------------
    // From User Account
    //var UserEmail: String = ""
    //var UserPassword: String = ""
    //var authtype: String = ""
    //private var UserSession = User()
    //private var logoutresult = false
    //-----------------------------------------

    private lateinit var presenter: LoginPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login)

        // Facebook Initializations
        FacebookSdk.sdkInitialize(applicationContext)
        mCallbackManager = CallbackManager.Factory.create()

        // Email Sign In
        signemail.setOnClickListener {
            var inputvalflag = true
            if (mailinputeditlogin.text.toString().isEmpty()) {
                mailinputeditlogin.error = getString(R.string.error_valid_emailaccount)
                inputvalflag = false
            }

            // Call to Login Email Activity
            if (inputvalflag) {
                val loginemail = Intent(this, Login_Email::class.java)
                loginemail.putExtra("email", mailinputeditlogin.text.toString())
                startActivity(loginemail)
            }
        }

        // Email Sign Up
        signemaillink.setOnClickListener {
            val loginemail = Intent(this, Login_Email::class.java)
            loginemail.putExtra("email", "")
            startActivity(loginemail)
        }

        // Google Sign In
        signgoogle.setOnClickListener {
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()

            val mGoogleSignInClient = GoogleSignIn.getClient(this, gso)
            val signInIntent = mGoogleSignInClient.signInIntent
            startActivityForResult(signInIntent, RC_SIGN_IN)
        }

        // Facebook Sign In
        signfacebook.setOnClickListener {
            LoginManager.getInstance().logInWithReadPermissions(this, listOf("email"))
            LoginManager.getInstance()
                .registerCallback(mCallbackManager, object : FacebookCallback<LoginResult> {
                    override fun onSuccess(result: LoginResult?) {
                        val fbtoken = result!!.accessToken
                        val credential = FacebookAuthProvider.getCredential(fbtoken!!.token)
                        presenter = LoginPresenter(
                            this@LoginView,
                            this@LoginView,
                            "facebook",
                            null,
                            null,
                            credential
                        )
                    }

                    override fun onCancel() {
                        Log.d(TAGF, "facebook:onCancel")
                    }

                    override fun onError(error: FacebookException?) {
                        Log.d(TAGF, "facebook:onError", error)
                    }
                })
        }
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        mCallbackManager!!.onActivityResult(requestCode, resultCode, data)
        // Pass the activity result back to the Facebook SDK
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                val credential = GoogleAuthProvider.getCredential(account!!.idToken, null)
                presenter = LoginPresenter(
                    this,
                    this@LoginView,
                    "google",
                    null,
                    null,
                    credential
                )
            } catch (e: ApiException) {
                Log.w(TAG, "Google sign in failed", e)
            }
        }
    }

    companion object {
        private val TAG = "GoogleActivity"
        private val TAGF = "FacebookLogin"
        private val RC_SIGN_IN = 9001
    }

    override fun onSuccess() {
        Toast.makeText(
            this,
            getString(R.string.welcome_message),
            Toast.LENGTH_SHORT
        ).show()
        finish()
    }

    override fun onOnboarding() {
        Toast.makeText(
            this,
            getString(R.string.onboarding_message),
            Toast.LENGTH_SHORT
        ).show()

        //TODO revisar la correcta llamada a la funcionalidad de Onboarding
        val onboardingname =
            Intent(this, Onboarding_Name::class.java)
        startActivity(onboardingname)
    }

    override fun onLoginError() {
        Toast.makeText(
            this,
            getString(R.string.login_error_message),
            Toast.LENGTH_SHORT
        ).show()
    }

    //--------------------------------------------------------------------------------------------------------
}
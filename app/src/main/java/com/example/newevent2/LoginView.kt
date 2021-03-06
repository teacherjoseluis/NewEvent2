package com.example.newevent2

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.newevent2.MVP.LoginEmailPresenter
import com.example.newevent2.MVP.LoginPresenter
import com.example.newevent2.Model.User
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
import kotlinx.android.synthetic.main.login.signfacebook
import kotlinx.android.synthetic.main.login.signgoogle
import kotlinx.android.synthetic.main.login0.*
import kotlinx.android.synthetic.main.onboarding.*
import java.util.regex.Matcher
import java.util.regex.Pattern


class LoginView() : AppCompatActivity(), LoginPresenter.ViewLoginActivity, User.SignUpActivity {

    private lateinit var mCallbackManager: CallbackManager
    private lateinit var presenter: LoginPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login0)

        // Facebook Initializations
        FacebookSdk.sdkInitialize(applicationContext)
        mCallbackManager = CallbackManager.Factory.create()

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
                    presenter =
                        LoginPresenter(this, this, "email", userEmail, userPassword, null)
                }
            }

            signuplink.setOnClickListener {
                frame2.visibility = ConstraintLayout.INVISIBLE
                frame3.visibility = ConstraintLayout.VISIBLE
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
                    user.signup(this, this, userEmail, userPassword)
                }
            }

            loginlink.setOnClickListener {
                frame2.visibility = ConstraintLayout.VISIBLE
                frame3.visibility = ConstraintLayout.INVISIBLE
            }
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

    override fun onLoginSuccess() {
        Toast.makeText(
            this,
            getString(R.string.welcome_message),
            Toast.LENGTH_SHORT
        ).show()
        finish()
    }

    override fun onOnboarding(userid: String, email: String, authtype: String) {
        Toast.makeText(
            this,
            getString(R.string.onboarding_message),
            Toast.LENGTH_SHORT
        ).show()

        val onboarding =
            Intent(this, OnboardingView::class.java)
        //OnboardingView pasando userid como parametro
        onboarding.putExtra("userid", userid)
        onboarding.putExtra("email", email)
        onboarding.putExtra("authtype", authtype)
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
        val matcher: Matcher
        val PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{4,}$"
        pattern = Pattern.compile(PASSWORD_PATTERN)
        matcher = pattern.matcher(password)
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
}
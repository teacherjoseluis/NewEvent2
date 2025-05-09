package com.bridesandgrooms.event

import Application.*
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.lifecycleScope
import com.bridesandgrooms.event.Model.*
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.*
import kotlinx.coroutines.*
import android.net.Uri
import android.view.View
import android.widget.VideoView
import android.media.MediaPlayer.OnCompletionListener
import android.view.animation.AnimationUtils
import androidx.databinding.DataBindingUtil
import com.bridesandgrooms.event.Functions.RemoteConfigSingleton
import com.bridesandgrooms.event.Functions.UserSessionHelper.saveUserSession
import com.bridesandgrooms.event.Functions.getEnglishString
import com.bridesandgrooms.event.UI.Adapters.ContactAdapter
import com.bridesandgrooms.event.UI.Adapters.ContactAdapter.Companion
import com.bridesandgrooms.event.UI.FieldValidators.InputValidator
import com.bridesandgrooms.event.UI.Fragments.GuestCreateEdit.Companion.SCREEN_NAME
import com.bridesandgrooms.event.databinding.LoginVideoBinding
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout


class LoginView : AppCompatActivity(), ViewLoginActivity, User.SignUpActivity {

    private val TIME_DELAY = 2000
    private var back_pressed: Long = 0
    private lateinit var user: User

    private lateinit var binding: LoginVideoBinding
    private val focusChangeListener = View.OnFocusChangeListener { view, hasFocus ->
        if (hasFocus && view is TextInputEditText) {
            val parentLayout = view.parent.parent as? TextInputLayout
            parentLayout?.error = null
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding =
            DataBindingUtil.setContentView(this, R.layout.login_video) as LoginVideoBinding
        val video_login = RemoteConfigSingleton.get_video_login()

        if (video_login) {
            val videoview = findViewById<View>(R.id.videowedding) as VideoView
            videoview.setOnCompletionListener(OnCompletionListener {
                videoview.start() //need to make transition seamless.
            })

            val uri: Uri =
                Uri.parse("android.resource://" + packageName + "/" + R.raw.weddingvideo4)
            videoview.setVideoURI(uri)
            videoview.requestFocus()
            videoview.start()

            // Show Welcome Screen
            showWelcomeScreen()
        }

        // ********************************* SignUp section *********************************************
        binding.signupbuttonstart.setOnClickListener {
            AnalyticsManager.getInstance().trackUserInteraction(SCREEN_NAME, "signupbuttonstart", "click")
            //Maybe this is a good moment to implement an animation for the transition
            //Signup layout becomes visible
            showSignUpScreen()
            binding.editEmailsignup.onFocusChangeListener = focusChangeListener
            binding.editPasswordsignup1.onFocusChangeListener = focusChangeListener
            binding.editPasswordsignup2.onFocusChangeListener = focusChangeListener
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)

            binding.signupbutton.setOnClickListener {
                AnalyticsManager.getInstance().trackUserInteraction(SCREEN_NAME, "signupbutton", "click")
                val isValid = validateAllInputsSignUp()
                if (isValid) {
                    val userEmail = binding.editEmailsignup.text.toString()
                    val userPassword = binding.editPasswordsignup1.text.toString()

                    lifecycleScope.launch {
                        user = User()
                        
                        try {
                            val signUpResult = user.signup(userEmail, userPassword)
                            if (signUpResult) {
                                withContext(Dispatchers.Main) {
                                    showBanner(getString(R.string.email_signup_success), false)
                                }
                            }
                        } catch (e: FirebaseAuthUserCollisionException) {
                            AnalyticsManager.getInstance().trackError(
                                SCREEN_NAME,
                                getEnglishString(this@LoginView, R.string.error_emailaccountexists),
                                "signup",
                                e.stackTraceToString()
                            )
                            displayErrorMsg(getString(R.string.error_emailaccountexists) + e.toString())
                        } catch (e: FirebaseAuthWeakPasswordException) {
                            AnalyticsManager.getInstance().trackError(
                                SCREEN_NAME,
                                getEnglishString(this@LoginView, R.string.weakpassword),
                                "signup",
                                e.stackTraceToString()
                            )
                            displayErrorMsg(getString(R.string.weakpassword) + e.toString())
                        } catch (e: FirebaseAuthInvalidCredentialsException) {
                            AnalyticsManager.getInstance().trackError(
                                SCREEN_NAME,
                                getEnglishString(this@LoginView, R.string.invalidemailpassword),
                                "signup",
                                e.stackTraceToString()
                            )
                            displayErrorMsg(getString(R.string.invalidemailpassword) + e.toString())
                        } catch (e: FirebaseAuthEmailException) {
                            AnalyticsManager.getInstance().trackError(
                                SCREEN_NAME,
                                getEnglishString(this@LoginView, R.string.errorsendingverification),
                                "signup",
                                e.stackTraceToString()
                            )
                            displayErrorMsg(getString(R.string.errorsendingverification) + e.toString())
                        } catch (e: Exception) {
                            AnalyticsManager.getInstance().trackError(
                                SCREEN_NAME,
                                getEnglishString(this@LoginView, R.string.error_emailsignup),
                                "signup",
                                e.stackTraceToString()
                            )
                            displayErrorMsg(getString(R.string.error_emailsignup) + e.toString())
                        }
                    }
                    logoffApp()
                }
            }
            binding.loginlink.setOnClickListener {
                AnalyticsManager.getInstance().trackUserInteraction(SCREEN_NAME, "loginlink", "click")
                showLoginScreen()
            }
        }

        binding.loginbuttonstart.setOnClickListener {
            AnalyticsManager.getInstance().trackUserInteraction(SCREEN_NAME, "loginbuttonstart", "click")
            //Maybe this is a good moment to implement an animation for the transition
            //Login layout becomes visible
            showLoginScreen()
            binding.editEmaillogin.onFocusChangeListener = focusChangeListener
            binding.editPasswordlogin.onFocusChangeListener = focusChangeListener

            // ********************************* Login section *********************************************
            binding.loginbutton.setOnClickListener {
                AnalyticsManager.getInstance().trackUserInteraction(SCREEN_NAME, "loginbutton", "click")
                val isValid = validateAllInputsLogin()
                if (isValid) {
                    val userEmail = binding.editEmaillogin.text.toString()
                    val userPassword = binding.editPasswordlogin.text.toString()
                    lifecycleScope.launch {
                        user = User()

                        try {
                            val authResult =
                                user.login(this@LoginView, "email", userEmail, userPassword, null)

                            val firebaseUser = authResult.user!!
                            user.userid = firebaseUser.uid
                            user.email = firebaseUser.email.toString()
                            AnalyticsManager.getInstance().setUserProperties(userId = firebaseUser.uid, user.role, user.numberguests, user.eventbudget, user.gender, user.agerange)
                            onLoginSuccess(user.email)
                        } catch (e: EmailVerificationException) {
                            AnalyticsManager.getInstance().trackError(
                                SCREEN_NAME,
                                getEnglishString(this@LoginView, R.string.error_emailverification),
                                "login",
                                e.stackTraceToString()
                            )
                            displayErrorMsg(getString(R.string.error_emailverification)/* + e.toString()*/)
                        } catch (e: FirebaseAuthInvalidCredentialsException) {
                            AnalyticsManager.getInstance().trackError(
                                SCREEN_NAME,
                                getEnglishString(this@LoginView, R.string.invalidemailpassword),
                                "login",
                                e.stackTraceToString()
                            )
                            displayErrorMsg(getString(R.string.invalidemailpassword))
                        } catch (e: UserAuthenticationException) {
                            AnalyticsManager.getInstance().trackError(
                                SCREEN_NAME,
                                getEnglishString(this@LoginView, R.string.failed_email_login),
                                "login",
                                e.stackTraceToString()
                            )
                            displayErrorMsg(getString(R.string.failed_email_login)/* + e.toString()*/)
                        } catch (e: SessionAccessException) {
                            AnalyticsManager.getInstance().trackError(
                                SCREEN_NAME,
                                getEnglishString(this@LoginView, R.string.error_usersession),
                                "login",
                                e.stackTraceToString()
                            )
                            displayErrorMsg(getString(R.string.error_usersession)/* + e.toString()*/)
                        } catch (e: ExistingSessionException) {
                            AnalyticsManager.getInstance().trackError(
                                SCREEN_NAME,
                                getEnglishString(this@LoginView, R.string.error_usersession),
                                "login",
                                e.stackTraceToString()
                            )
                            displayErrorMsg(getString(R.string.error_usersession)/* + e.toString()*/)
                        } catch (e: NetworkConnectivityException) {
                            AnalyticsManager.getInstance().trackError(
                                SCREEN_NAME,
                                getEnglishString(this@LoginView, R.string.error_networkconnectivity),
                                "login",
                                e.stackTraceToString()
                            )
                            displayErrorMsg(getString(R.string.error_networkconnectivity)/* + e.toString()*/)
                        } catch (e: FirebaseDataImportException) {
                            AnalyticsManager.getInstance().trackError(
                                SCREEN_NAME,
                                getEnglishString(this@LoginView, R.string.error_firebaseimport),
                                "login",
                                e.stackTraceToString()
                            )
                            displayErrorMsg(getString(R.string.error_firebaseimport)/* + e.toString()*/)
                        } catch (e: EventNotFoundException) {
                            AnalyticsManager.getInstance().trackNavigationEvent(SCREEN_NAME,"Onboarding")
                            onOnboarding(
                                e.firebaseUser.uid,
                                e.firebaseUser.email.toString(),
                                "email"
                            )
                        }
                    }
                }
            }

            binding.forgotemaillink.setOnClickListener {
                AnalyticsManager.getInstance().trackUserInteraction(SCREEN_NAME, "forgotemaillink", "click")
                val isValid = validateAllInputsForgotEmail()
                if (isValid) {
                    val userEmail = binding.editEmaillogin.text.toString()
                    val user = User()
                    lifecycleScope.launch {
                        try {
                            user.sendPasswordReset(userEmail)
                        } catch (e: FirebaseAuthInvalidUserException) {
                            AnalyticsManager.getInstance().trackError(
                                SCREEN_NAME,
                                getEnglishString(this@LoginView, R.string.resetpwderror_authinvaliduser),
                                "forgot password",
                                e.stackTraceToString()
                            )
                            displayErrorMsg(getString(R.string.resetpwderror_authinvaliduser) + e.toString())
                        } catch (e: FirebaseAuthInvalidCredentialsException) {
                            AnalyticsManager.getInstance().trackError(
                                SCREEN_NAME,
                                getEnglishString(this@LoginView, R.string.resetpwderror_invalidcredentials),
                                "forgot password",
                                e.stackTraceToString()
                            )
                            displayErrorMsg(getString(R.string.resetpwderror_invalidcredentials) + e.toString())
                        } catch (e: FirebaseAuthEmailException) {
                            AnalyticsManager.getInstance().trackError(
                                SCREEN_NAME,
                                getEnglishString(this@LoginView, R.string.resetpwderror_authmailexception),
                                "forgot password",
                                e.stackTraceToString()
                            )
                            displayErrorMsg(getString(R.string.resetpwderror_authmailexception) + e.toString())
                        } catch (e: Exception) {
                            AnalyticsManager.getInstance().trackError(
                                SCREEN_NAME,
                                getEnglishString(this@LoginView, R.string.resetpwderror_authmailexception),
                                "forgot password",
                                e.stackTraceToString()
                            )
                            displayErrorMsg(getString(R.string.resetpwderror_authmailexception) + e.toString())
                        }
                    }
                }
            }

            // Google Sign In
            binding.signgoogle.setOnClickListener {
                AnalyticsManager.getInstance().trackUserInteraction(SCREEN_NAME, "signgoogle", "click")
                val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(getString(R.string.default_web_client_id))
                    .requestEmail()
                    .build()

                val mGoogleSignInClient = GoogleSignIn.getClient(this, gso)
                val signInIntent = mGoogleSignInClient.signInIntent
                startActivityForResult(signInIntent, RC_SIGN_IN)
            }

            binding.signuplink.setOnClickListener {
                AnalyticsManager.getInstance().trackUserInteraction(SCREEN_NAME, "signuplink", "click")
                showSignUpScreen()
            }

        }
    }

    private fun validateAllInputsLogin(): Boolean {
        var isValid = true
        val validator = InputValidator(this)

        val emailValidation =
            validator.validate(binding.editEmaillogin)
        if (!emailValidation) {
            binding.editEmaillogin.error = validator.errorCode
            isValid = false
        }

        val passwordValidation =
            validator.validate(binding.editPasswordlogin)
        if (!passwordValidation) {
            binding.editPasswordlogin.error = validator.errorCode
            isValid = false
        }
        return isValid
    }

    private fun validateAllInputsForgotEmail(): Boolean {
        var isValid = true
        val validator = InputValidator(this)

        val emailValidation =
            validator.validate(binding.editEmaillogin)
        if (!emailValidation) {
            binding.editEmaillogin.error = validator.errorCode
            isValid = false
        }
        return isValid
    }

    private fun validateAllInputsSignUp(): Boolean {
        var isValid = true
        val validator = InputValidator(this)

        val emailValidation =
            validator.validate(binding.editEmailsignup)
        if (!emailValidation) {
            binding.editEmailsignup.error = validator.errorCode
            isValid = false
        }

        val passwordValidation1 =
            validator.validate(binding.editPasswordsignup1)
        if (!passwordValidation1) {
            binding.editPasswordsignup1.error = validator.errorCode
            isValid = false
        }

        val passwordValidation2 =
            validator.validate(binding.editPasswordsignup2)
        if (!passwordValidation2) {
            binding.editPasswordsignup2.error = validator.errorCode
            isValid = false
        }
        val password1 = binding.editPasswordsignup1.text?.toString().orEmpty()
        val password2 = binding.editPasswordsignup2.text?.toString().orEmpty()

        if (password1 != password2) {
            binding.editPasswordsignup2.error = getString(R.string.passwords_dontmatch)
            isValid = false
        }
        return isValid
    }

    private fun showWelcomeScreen() {
        binding.welcomeScreen.visibility = ConstraintLayout.VISIBLE
        binding.loginScreen.visibility = ConstraintLayout.INVISIBLE
        binding.signupScreen.visibility = ConstraintLayout.INVISIBLE
    }

    private fun showLoginScreen() {
        binding.welcomeScreen.visibility = ConstraintLayout.INVISIBLE
        binding.loginScreen.visibility = ConstraintLayout.VISIBLE
        binding.signupScreen.visibility = ConstraintLayout.INVISIBLE
    }

    private fun showSignUpScreen() {
        binding.welcomeScreen.visibility = ConstraintLayout.INVISIBLE
        binding.loginScreen.visibility = ConstraintLayout.INVISIBLE
        binding.signupScreen.visibility = ConstraintLayout.VISIBLE
    }

    private fun showBanner(message: String, dismiss: Boolean) {
        val fadeInAnimation = AnimationUtils.loadAnimation(this, R.anim.fade_in)
        binding.bannerCardView.startAnimation(fadeInAnimation)

        binding.bannerCardView.visibility = View.VISIBLE
        binding.bannerText.text = message
        if (dismiss) {
            binding.dismissButton.visibility = View.VISIBLE
            binding.dismissButton.setOnClickListener {
                AnalyticsManager.getInstance().trackUserInteraction(SCREEN_NAME, "dismissButton", "click")
                binding.bannerCardView.visibility = View.INVISIBLE
            }
        }
    }

    private fun displayErrorMsg(message: String) {
        Toast.makeText(
            this@LoginView,
            message,
            Toast.LENGTH_LONG
        ).show()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            lifecycleScope.launch {
                user = User()

                try {
                    val task = GoogleSignIn.getSignedInAccountFromIntent(data)
                    val account = task.getResult(ApiException::class.java)
                    val credential = GoogleAuthProvider.getCredential(account!!.idToken, null)

                    val authResult =
                        user.login(
                            this@LoginView,
                            "google",
                            null,
                            null,
                            credential
                        )

                    val firebaseUser = authResult.user!!
                    user.userid = firebaseUser.uid
                    user.email = firebaseUser.email.toString()
                    AnalyticsManager.getInstance().setUserProperties(userId = firebaseUser.uid, user.role, user.numberguests, user.eventbudget, user.gender, user.agerange)
                    onLoginSuccess(user.email)
                } catch (e: ApiException) {
                    Log.e(TAG, "Google sign in failed", e)
                } catch (e: SessionAccessException) {
                    AnalyticsManager.getInstance().trackError(
                        SCREEN_NAME,
                        getEnglishString(this@LoginView, R.string.error_usersession),
                        "login",
                        e.stackTraceToString()
                    )
                    displayErrorMsg(getString(R.string.error_usersession)/* + e.toString()*/)
                } catch (e: ExistingSessionException) {
                    AnalyticsManager.getInstance().trackError(
                        SCREEN_NAME,
                        getEnglishString(this@LoginView, R.string.error_usersession),
                        "login",
                        e.stackTraceToString()
                    )
                    displayErrorMsg(getString(R.string.error_usersession)/* + e.toString()*/)
                } catch (e: NetworkConnectivityException) {
                    AnalyticsManager.getInstance().trackError(
                        SCREEN_NAME,
                        getEnglishString(this@LoginView, R.string.error_networkconnectivity),
                        "login",
                        e.stackTraceToString()
                    )
                    displayErrorMsg(getString(R.string.error_networkconnectivity)/* + e.toString()*/)
                } catch (e: FirebaseDataImportException) {
                    AnalyticsManager.getInstance().trackError(
                        SCREEN_NAME,
                        getEnglishString(this@LoginView, R.string.error_firebaseimport),
                        "login",
                        e.stackTraceToString()
                    )
                    displayErrorMsg(getString(R.string.error_firebaseimport)/* + e.toString()*/)
                } catch (e: EventNotFoundException) {
                    AnalyticsManager.getInstance().trackNavigationEvent(SCREEN_NAME,"Onboarding")
                    onOnboarding(e.firebaseUser.uid, e.firebaseUser.email.toString(), "google")
                }
            }
        }
    }

    private fun logoffApp() {
        // Dialog
        val builder = android.app.AlertDialog.Builder(this)
        builder.setTitle(getString(R.string.emailverification_title))
        builder.setMessage(getString(R.string.emailverification_message))

        builder.setPositiveButton(
            getString(R.string.accept)
        ) { _, _ ->
            AnalyticsManager.getInstance().trackNavigationEvent(SCREEN_NAME,"Logoff")
            finish()
        }
        val dialog = builder.create()
        dialog.show()
    }

    override fun onLoginSuccess(email: String) {
        showBanner(getString(R.string.welcome_message), false)
        saveUserSession(email, null, "email")
        finish()
        val mainActivity =
            Intent(this, ActivityContainer::class.java)
        startActivity(mainActivity)
    }

    override fun onLoginError() {
        showBanner(getString(R.string.login_error_message), false)
        showWelcomeScreen()
    }

    override fun onOnboarding(userid: String, email: String, authtype: String) {
        showBanner(getString(R.string.onboarding_message), false)

        val onboarding =
            Intent(this@LoginView, OnboardingView::class.java)
        onboarding.putExtra("userid", userid)
        onboarding.putExtra("email", email)
        onboarding.putExtra("authtype", authtype)
        //finish()
        startActivity(onboarding)
        overridePendingTransition(android.R.anim.slide_out_right, android.R.anim.slide_in_left)
    }

    override fun onSignUpSuccess() {
        showLoginScreen()
    }

    override fun onSignUpError() {
        showWelcomeScreen()
    }

    override fun onBackPressed() {
        if (back_pressed + TIME_DELAY > System.currentTimeMillis()) {
            AnalyticsManager.getInstance().trackNavigationEvent(SCREEN_NAME,"ExitApp")
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

    companion object {
        private const val TAG = "LoginView"
        private const val SCREEN_NAME = "login_video.xml"
        private const val RC_SIGN_IN = 9001
    }
}

interface ViewLoginActivity {
    fun onLoginSuccess(email: String)
    fun onOnboarding(userid: String, email: String, authtype: String)
    fun onLoginError()
}


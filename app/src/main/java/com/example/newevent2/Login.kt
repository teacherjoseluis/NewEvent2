package com.example.newevent2

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.facebook.*
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.android.synthetic.main.login.*
import kotlinx.android.synthetic.main.login_email.*


class Login() : AppCompatActivity() {

    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private var mCallbackManager: CallbackManager? = null
    private var mAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private var user: UserAccount? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FacebookSdk.sdkInitialize(applicationContext)
        setContentView(R.layout.login)

        user = UserAccount()

        // Initialize Facebook login button
        mCallbackManager = CallbackManager.Factory.create()

        signemail.setOnClickListener {

            var inputvalflag = true
            if (mailinputeditlogin.text.toString().isEmpty()) {
                mailinputeditlogin.error = "Email is required!"
                inputvalflag = false
            }

            if (inputvalflag) {
                val loginemail = Intent(this, Login_Email::class.java)
                loginemail.putExtra("email", mailinputeditlogin.text.toString())
                startActivity(loginemail)
            }
        }

        signgoogle.setOnClickListener {
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()

            mGoogleSignInClient = GoogleSignIn.getClient(this, gso)

            val signInIntent = mGoogleSignInClient.signInIntent
            startActivityForResult(signInIntent, RC_SIGN_IN)
        }

        signfacebook.setOnClickListener {
            LoginManager.getInstance().logInWithReadPermissions(this, listOf("email"))
            LoginManager.getInstance()
                .registerCallback(mCallbackManager, object : FacebookCallback<LoginResult> {
                    override fun onSuccess(result: LoginResult?) {
                        Log.d(TAGF, "facebook:onSuccess:$result")
                        val token = result!!.accessToken
                        user!!.authtype = "facebook"
                        user!!.login(this@Login, null, token)
                    }

                    override fun onCancel() {
                        Log.d(TAGF, "facebook:onCancel")
                    }

                    override fun onError(error: FacebookException?) {
                        Log.d(TAGF, "facebook:onError", error)
                    }

                })
        }

        signemaillink.setOnClickListener {
            val loginemail = Intent(this, Login_Email::class.java)
            loginemail.putExtra("email", "")
            startActivity(loginemail)
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
                user!!.authtype = "google"
                user!!.login(this, account, null)
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
}
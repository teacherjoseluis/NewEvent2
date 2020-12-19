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


class Login() : AppCompatActivity()
//, View.OnClickListener
{

    //private var mAuth: FirebaseAuth? = null
    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private var mCallbackManager: CallbackManager? = null
    private var mAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private var user: UserAccount? = null

    public override fun onStart() {
        super.onStart()
        // if user logged in, go to sign-in screen
        if (mAuth!!.currentUser != null) {
            startActivity(Intent(this, Welcome::class.java))
            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FacebookSdk.sdkInitialize(applicationContext)
        setContentView(R.layout.login)

        user = UserAccount()

        // Initialize Firebase Auth
        //mAuth = FirebaseAuth.getInstance()

        // Initialize Facebook login button
        mCallbackManager = CallbackManager.Factory.create()

        //signgoogle.setSize(SignInButton.SIZE_WIDE)

        signemail.setOnClickListener {
            startActivity(Intent(this, Login_Email::class.java))
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

                        if (user!!.login("facebook", this@Login, null, token)) {
                            startActivity(Intent(applicationContext, Welcome::class.java))
                        }
//                        val credential = FacebookAuthProvider.getCredential(token.token)
//                        mAuth!!.signInWithCredential(credential)
//                            .addOnCompleteListener {
//                                if (it.isSuccessful) {
//                                    val user = mAuth!!.currentUser
//                                    //Need to implement functionality to save user session. Class
//                                    Log.d(
//                                        TAG,
//                                        "signInWithCredential:success: currentUser: " + user!!.email!!
//                                    )
//                                } else {
//                                    Log.w(TAG, "sigInWithCredential:failure", it.exception)
//                                }
//
//                            }
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

//    override fun onClick(p0: View?) {
//        when (p0!!.id) {
//            signgoogle.id -> signInToGoogle()
//            button.id -> signOut()
//        }
//    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        mCallbackManager!!.onActivityResult(requestCode, resultCode, data)
        // Pass the activity result back to the Facebook SDK
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                if (user!!.login("google", this, account, null)) {
                    startActivity(Intent(this, Welcome::class.java))
                }
                //firebaseAuthWithGoogle(account!!)
            } catch (e: ApiException) {
                Log.w(TAG, "Google sign in failed", e)
            }
        }
    }

//    private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount) {
//        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
//        mAuth!!.signInWithCredential(credential)
//            .addOnCompleteListener(this) { task ->
//                if (task.isSuccessful) {
//                    val user = mAuth!!.currentUser
//                    //Need to implement functionality to save user session. Class
//                    Log.d(TAG, "signInWithCredential:success: currentUser: " + user!!.email!!)
//                } else {
//                    Log.w(TAG, "sigInWithCredential:failure", task.exception)
//                }
//            }
//    }


//    private fun signInToGoogle() {
//        val signInIntent = mGoogleSignInClient.signInIntent
//        startActivityForResult(signInIntent, RC_SIGN_IN)
//    }


//    private fun signOut() {
//        mAuth!!.signOut()
//        mGoogleSignInClient.signOut().addOnCompleteListener(this) {
//            Log.w(TAG, "Signed out of google")
//        }
//    }

    companion object {
        private val TAG = "GoogleActivity"
        private val TAGF = "FacebookLogin"
        private val RC_SIGN_IN = 9001
    }
//
//    public override fun onStart() {
//        super.onStart()
//
//        // if user logged in, go to sign-in screen
//        if (mAuth!!.currentUser != null) {
//            startActivity(Intent(this, Welcome::class.java))
//            finish()
//        }
//    }

}
package com.example.newevent2

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.example.newevent2.MVP.LoginEmailPresenter
import com.example.newevent2.MVP.LoginPresenter
import com.example.newevent2.Model.User
import kotlinx.android.synthetic.main.login_email.*
import java.util.regex.Matcher
import java.util.regex.Pattern

class LoginEmailView : AppCompatActivity(), LoginEmailPresenter.ViewEmailLoginActivity {

    private lateinit var presenter: LoginEmailPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_email)

        val email = intent.getStringExtra("email").toString()
        val user = User()

        if (email != "") {
            //Login
            mailinputedit.setText(email)
            mailinputedit.isEnabled = false
            horizontalline2.isVisible = false
            forgotemaillink.isVisible = false
        } else {
            //SignUp
            passwordinput2.isVisible = false
        }

        button.setOnClickListener {
            var inputvalflag = true
            if (email != "") {
                if (mailinputedit.text.toString().isEmpty()) {
                    mailinputedit.error = getString(R.string.error_valid_emailaccount)
                    inputvalflag = false
                }
//                if (editTextTextPassword.text.toString()
//                        .isEmpty() || editTextTextPassword.text.toString().length < 8 || !isValidPassword(
//                        editTextTextPassword.text.toString()
//                    )
//                ) {
//                    editTextTextPassword.error =
//                        getString(R.string.password_requiredformat)
//                    inputvalflag = false
//                }
//                if (editTextTextPassword2.text.toString()
//                        .isEmpty() && editTextTextPassword2.text != editTextTextPassword.text
//                ) {
//                    editTextTextPassword2.error = getString(R.string.passwords_dontmatch)
//                    inputvalflag = false
//                }
//                if (inputvalflag) {
//                    val userEmail = mailinputedit.text.toString()
//                    val userPassword = editTextTextPassword.text.toString()
//                    //user.signup(this, userEmail, userPassword)
//                }
//            } else {
//                if (editTextTextPassword.text.toString()
//                        .isEmpty() || editTextTextPassword.text.toString().length < 8 || !isValidPassword(
//                        editTextTextPassword.text.toString()
//                    )
//                ) {
//                    editTextTextPassword.error =
//                        getString(R.string.password_requiredformat)
//                    inputvalflag = false
//                }
//                if (inputvalflag) {
//                    val userEmail = mailinputedit.text.toString()
//                    val userPassword = editTextTextPassword.text.toString()
//                    presenter =
//                        LoginEmailPresenter(this, this, userEmail, userPassword)
//                }
            }
        }

        forgotemaillink.setOnClickListener {
            var inputvalflag = true
            if (mailinputedit.text.toString().isEmpty()) {
                mailinputedit.error = getString(R.string.error_valid_emailaccount)
                inputvalflag = false
            }
            if (inputvalflag) {
                val userEmail = mailinputedit.text.toString()
                val user = User()
                user.sendpasswordreset(this, userEmail)
            }
        }
    }

    private fun isValidPassword(password: String?): Boolean {
        val pattern: Pattern
        val matcher: Matcher
        val PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{4,}$"
        pattern = Pattern.compile(PASSWORD_PATTERN)
        matcher = pattern.matcher(password)
        return matcher.matches()
    }

    override fun onLoginEmailSuccess() {
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

        //TODO("Onboarding_Name recibe un objeto User que en este caso ya no estaria mandando
        //        //se me ocurre ne este caso que se maneje toda la funcionalidad de Onboarding solamente en una vista y no en dos
        //        //Se queda pendinete hacer un refactor de esa funcionalidad")

        val onboarding =
            Intent(this, OnboardingView::class.java)
        onboarding.putExtra("userid", userid)
        onboarding.putExtra("email", email)
        onboarding.putExtra("authtype", authtype)
        startActivity(onboarding)
    }

    override fun onLoginEmailError() {
        Toast.makeText(
            this,
            getString(R.string.login_error_message),
            Toast.LENGTH_SHORT
        ).show()
    }
}
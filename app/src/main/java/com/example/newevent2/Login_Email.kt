package com.example.newevent2

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import kotlinx.android.synthetic.main.login_email.*
import java.util.regex.Matcher
import java.util.regex.Pattern

class Login_Email : AppCompatActivity() {

    //private var mAuth: FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_email)

        val email = intent.getStringExtra("email").toString()

        if (email != "") {
            mailinputedit.setText(email)
            mailinputedit.isEnabled = false
            horizontalline2.isVisible = false
            forgotemaillink.isVisible = false
        } else {
            passwordinput2.isVisible = false
            passwordimage2.isVisible = false
        }

        button.setOnClickListener {
            var inputvalflag = true
            if (email != "") {
                if (mailinputedit.text.toString().isEmpty()) {
                    mailinputedit.error = "Email is required!"
                    inputvalflag = false
                }
                if (editTextTextPassword.text.toString()
                        .isEmpty() || editTextTextPassword.text.toString().length < 8 || !isValidPassword(
                        editTextTextPassword.text.toString()
                    )
                ) {
                    editTextTextPassword.error =
                        "A valid password is required! (8 characters, 1 upper case, 1 number and 1 symbol"
                    inputvalflag = false
                }
                if (editTextTextPassword2.text.toString()
                        .isEmpty() && editTextTextPassword2.text != editTextTextPassword.text
                ) {
                    editTextTextPassword2.error = "Password does not match"
                    inputvalflag = false
                }
                if (inputvalflag) {
                    val user = UserAccount()
                    user.UserEmail = mailinputedit.text.toString()
                    user.UserPassword = editTextTextPassword.text.toString()
                    user!!.authtype = "email"
                    user!!.signup(this)
                }
            } else {
                if (editTextTextPassword.text.toString()
                        .isEmpty() || editTextTextPassword.text.toString().length < 8 || !isValidPassword(
                        editTextTextPassword.text.toString()
                    )
                ) {
                    editTextTextPassword.error =
                        "A valid password is required! (8 characters, 1 upper case, 1 number and 1 symbol"
                    inputvalflag = false
                }
                if (inputvalflag) {
                    val user = UserAccount()
                    user.UserEmail = mailinputedit.text.toString()
                    user.UserPassword = editTextTextPassword.text.toString()
                    user!!.authtype = "email"
                    user!!.login(this, null, null)
                }
            }
        }

        forgotemaillink.setOnClickListener {
            var inputvalflag = true
            if (mailinputedit.text.toString().isEmpty()) {
                mailinputedit.error = "Email is required!"
                inputvalflag = false
            }
            if (inputvalflag) {
                val user = UserAccount()
                user.UserEmail = mailinputedit.text.toString()
                user.sendpasswordreset(this)
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

}
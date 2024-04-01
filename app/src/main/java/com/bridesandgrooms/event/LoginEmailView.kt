package com.bridesandgrooms.event

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.bridesandgrooms.event.Model.User
import com.bridesandgrooms.event.databinding.LoginEmailBinding
import com.google.firebase.auth.FirebaseAuthEmailException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
//import kotlinx.android.synthetic.main.login_email.*
import kotlinx.coroutines.launch

class LoginEmailView : AppCompatActivity(), ViewEmailLoginActivity {

    private lateinit var binding: LoginEmailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.login_email)

        val email = intent.getStringExtra("email").toString()
        val user = User()

        if (email != "") {
            //Login
            binding.mailinputedit.setText(email)
            binding.mailinputedit.isEnabled = false
            binding.horizontalline2.isVisible = false
            binding.forgotemaillink.isVisible = false
        } else {
            //SignUp
            binding.passwordinput2.isVisible = false
        }

        binding.button.setOnClickListener {
            var inputvalflag = true
            if (email != "") {
                if (binding.mailinputedit.text.toString().isEmpty()) {
                    binding.mailinputedit.error = getString(R.string.error_valid_emailaccount)
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

        binding.forgotemaillink.setOnClickListener {
            var inputvalflag = true
            if (binding.mailinputedit.text.toString().isEmpty()) {
                binding.mailinputedit.error = getString(R.string.error_valid_emailaccount)
                inputvalflag = false
            }
            if (inputvalflag) {
                val userEmail = binding.mailinputedit.text.toString()
                val user = User()
                lifecycleScope.launch {
                    try {
                        user.sendPasswordReset(userEmail)
                    } catch(e: FirebaseAuthInvalidUserException){
                        displayErrorMsg(getString(R.string.resetpwderror_authinvaliduser) + e.toString())
                    } catch(e: FirebaseAuthInvalidCredentialsException){
                        displayErrorMsg(getString(R.string.resetpwderror_invalidcredentials) + e.toString())
                    } catch (e: FirebaseAuthEmailException){
                        displayErrorMsg(getString(R.string.resetpwderror_authmailexception) + e.toString())
                    } catch (e: Exception){
                        displayErrorMsg(getString(R.string.resetpwderror_authmailexception) + e.toString())
                    }
                }
            }
        }
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

    private fun displayErrorMsg(message: String) {
        Toast.makeText(
            this,
            message,
            Toast.LENGTH_LONG
        ).show()
    }
}

interface ViewEmailLoginActivity {
    fun onLoginEmailSuccess()
    fun onOnboarding(userid: String, email: String, authtype: String)
    fun onLoginEmailError()
}
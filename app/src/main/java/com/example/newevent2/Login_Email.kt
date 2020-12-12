package com.example.newevent2

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.login_email.*
import kotlinx.android.synthetic.main.login_email.button

class Login_Email : AppCompatActivity() {

    private var mAuth: FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_email)

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance()

        button.setOnClickListener {
            var inputvalflag = true
            if (editTextTextEmailAddress.text.toString().isEmpty()) {
                editTextTextEmailAddress.error = "Email is required!"
                inputvalflag = false
            }
            if (editTextTextPassword.text.toString().isEmpty()) {
                editTextTextPassword.error = "Password is required!"
                inputvalflag = false
            }
            if (inputvalflag) {
               mAuth!!.createUserWithEmailAndPassword(editTextTextEmailAddress.text.toString(), editTextTextPassword.text.toString())
                   //Need to implement functionality to save user session. Class
                   .addOnCompleteListener(this) { task ->
                       if(task.isSuccessful) {
                           startActivity(Intent(this, Welcome::class.java))
                           finish()
                       } else {
                           Log.e("MyTag", task.exception.toString())
                       }
                   }
            }
        }
    }
}
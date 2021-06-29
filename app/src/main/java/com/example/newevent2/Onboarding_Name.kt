package com.example.newevent2

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.login.*
import kotlinx.android.synthetic.main.onboarding_name.*

class Onboarding_Name : AppCompatActivity() {

    //private var userSession = User()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.onboarding_name)

        //userSession = intent.getParcelableExtra("usersession")!!
//        useremail = intent.getStringExtra("useremail").toString()
//        userkey = intent.getStringExtra("userkey").toString()

//        buttonname.setOnClickListener {
//            var inputvalflag = true
//            if (nameinputedit.text.toString().isEmpty()) {
//                nameinputedit.error = "Name is required!"
//                inputvalflag = false
//            }
//
//            if (inputvalflag) {
//                userSession!!.shortname = nameinputedit.text.toString()
//
//                val onboardingevent = Intent(this, Onboarding_Event::class.java)
////                onboardingevent.putExtra("username", nameinputedit.text.toString())
////                onboardingevent.putExtra("useremail", useremail)
////                onboardingevent.putExtra("userkey", userkey)
//                onboardingevent.putExtra("usersession", userSession)
//                startActivity(onboardingevent)
//            }
//        }
    }
}
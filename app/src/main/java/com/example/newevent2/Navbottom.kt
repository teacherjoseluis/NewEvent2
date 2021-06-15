package com.example.newevent2

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.FirebaseApp
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.navbottom.*

class Navbottom : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.navbottom)

        imageButton3.setOnClickListener {
            val contacts = Intent(this, MyContacts::class.java)
            startActivity(contacts)
        }
    }
}
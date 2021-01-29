package com.example.newevent2

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity
import com.example.newevent2.ui.dialog.DatePickerFragment
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.event_edit.*
import kotlinx.android.synthetic.main.event_edit.button
import kotlinx.android.synthetic.main.event_edit.etPlannedDate
import kotlinx.android.synthetic.main.event_edit.etPlannedTime
import kotlinx.android.synthetic.main.event_edit.etabout
import kotlinx.android.synthetic.main.event_edit.etlocation
import kotlinx.android.synthetic.main.event_edit.etname
import TimePickerFragment
import android.app.Activity
import android.content.Context
import android.util.Log
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.sql.Time
import java.text.SimpleDateFormat
import java.util.*

class Onboarding_Event : AppCompatActivity() {

    private val autocompletePlaceCode = 1

    private var event_placeid: String? = null
    private var event_latitude = 0.0
    private var event_longitude = 0.0
    private var event_address: String? = null

    private var eventkey = ""
    private var username = ""
    private var useremail = ""
    private var userkey = ""

    private var userSession = User()

    private val database = FirebaseDatabase.getInstance()
    private val myRef = database.reference
    //private val postRef = myRef.child("User").child("Event")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.onboarding_event)

        val intent = intent
//        username = intent.getStringExtra("username").toString()
//        useremail = intent.getStringExtra("useremail").toString()
//        userkey = intent.getStringExtra("userkey").toString()
        userSession = intent.getParcelableExtra("usersession")!!

        etname.setOnClickListener {
            etname.error = null
        }

        etPlannedDate.setOnClickListener {
            etPlannedDate.error = null
            showDatePickerDialog()
        }

        etPlannedTime.setOnClickListener {
            etPlannedTime.error = null
            showTimePickerDialog()
        }

        etlocation.setOnClickListener {
            val locationmap = Intent(this, MapsActivity::class.java)
            startActivityForResult(locationmap, autocompletePlaceCode)
        }

        button.setOnClickListener {
            var inputvalflag = true
            if (etname.text.toString().isEmpty()) {
                etname.error = "Event name is required!"
                inputvalflag = false
            }
            if (etPlannedDate.text.toString().isEmpty()) {
                etPlannedDate.error = "Event date is required!"
                inputvalflag = false
            }
            if (etPlannedTime.text.toString().isEmpty()) {
                etPlannedTime.error = "Event time is required!"
                inputvalflag = false
            }
            if (etlocation.text.toString().isEmpty()) {
                etlocation.error = "Event location is required!"
                inputvalflag = false
            }
            if (inputvalflag) {
                saveUserEvent()
                this.onBackPressed()
            }
        }
    }

    private fun saveUserEvent() {
        var postRef = myRef.child("User").child(userSession!!.key)

        //---------------------------------------
        // Getting the time and date to record in the recently created task
        val timestamp = Time(System.currentTimeMillis())
        val taskdatetime = Date(timestamp.time)
        val sdf = SimpleDateFormat("MM/dd/yyyy h:mm:ss a")

        userSession!!.createdatetime = sdf.format(taskdatetime)
        userSession!!.country = this.resources.configuration.locale.country
        userSession!!.language = this.resources.configuration.locale.language
        userSession!!.status = "A"
        //---------------------------------------

        val user = hashMapOf(
            "shortname" to userSession!!.shortname,
            "email" to userSession!!.email,
            "country" to userSession!!.country,
            "language" to userSession!!.language,
            "createdatetime" to userSession!!.createdatetime,
            "status" to userSession!!.status,
            "hasevent" to "Y"
        )

        postRef.setValue(user as Map<String, Any>)
            .addOnFailureListener {
            }
            .addOnSuccessListener {
                postRef = myRef.child("User").child(userSession!!.key).child("Event").push()
                val events = hashMapOf(
                    "name" to etname.text.toString(),
                    "date" to etPlannedDate.text.toString(),
                    "time" to etPlannedTime.text.toString(),
                    "location" to etlocation.text.toString(),
                    "placeid" to event_placeid.toString(),
                    "latitude" to event_latitude,
                    "longitude" to event_longitude,
                    "address" to event_address.toString(),
                    "about" to "",
                    "imageurl" to ""
                )

                postRef.setValue(events as Map<String, Any>)
                    .addOnFailureListener {
                        return@addOnFailureListener
                    }
                    .addOnSuccessListener {
                        //Save the eventkey in the user session
                        val eventkey = postRef.key.toString()
                        var userlocalsession =
                            this.getSharedPreferences("USER_SESSION", Context.MODE_PRIVATE)
                        try {
                            val sessionEditor = userlocalsession!!.edit()
                            sessionEditor.putString("UID", userSession!!.key) // UID from Firebase
                            sessionEditor.putString("Email", userSession!!.email)
                            sessionEditor.putString("Autentication", userSession!!.authtype)
                            sessionEditor.putString("Eventid", eventkey)
                            sessionEditor.apply()
                            saveLog(this, "INSERT", "event", eventkey, etname.text.toString())
                        } catch (e: Exception) {
                            Log.e("Save Session Exception", e.toString())
                        }
                    }
            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == autocompletePlaceCode) {
            val placenameString = data?.getStringExtra("place_name")
            event_placeid = data!!.getStringExtra("place_id").toString()
            event_latitude = data.getDoubleExtra("place_latitude", 0.0)
            event_longitude = data.getDoubleExtra("place_longitude", 0.0)
            event_address = data.getStringExtra("place_address").toString()
            etlocation.setText(placenameString)
        } else {
            //Toast.makeText(this, "Error in autocomplete location", Toast.LENGTH_SHORT).show()
        }
    }


    private fun showDatePickerDialog() {
        val newFragment =
            DatePickerFragment.newInstance(DatePickerDialog.OnDateSetListener { _, year, month, day ->
                // +1 because January is zero
                val selectedDate = day.toString() + "/" + (month + 1) + "/" + year
                etPlannedDate.setText(selectedDate)
            })

        newFragment.show(supportFragmentManager, "datePicker")
    }

    private fun showTimePickerDialog() {
        val newFragment = TimePickerFragment()
        newFragment.show(supportFragmentManager, "Time Picker")

    }
}
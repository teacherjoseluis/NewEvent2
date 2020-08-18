package com.example.newevent2

import android.Manifest
import android.R.attr
import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.newevent2.ui.dialog.DatePickerFragment
import com.example.newevent2.ui.dialog.TimePickerFragment
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.FirebaseApp
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import kotlinx.android.synthetic.main.activity_main.*
import java.io.ByteArrayOutputStream


class Event {
    var event_name: String? = null
    var event_date: String? = null
    var event_time: String? = null
    //var event_location_id: String? = null
    var event_location_name: String? = null
    //var event_about: String? = null

    constructor(
        event_name: String?,
        event_date: String?
    ) {
        // ...
    }

    constructor(
        event_name: String?,
        event_date: String?,
        event_time: String?,
        //event_location_id: String?,
        event_location_name: String?
        //event_about: String?
    ) {
        // ...
    }
}

class MainActivity : AppCompatActivity() {

    private val autocomplete_place_code = 1
    private val TAG = "KotlinActivity"
    lateinit var storage: FirebaseStorage

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(applicationContext)
        storage=Firebase.storage
        setContentView(R.layout.activity_main)

        etPlannedDate.setOnClickListener{
            showDatePickerDialog()
        }

        etPlannedTime.setOnClickListener{
            showTimePickerDialog()
        }

        etlocation.setOnClickListener{
            val locationmap = Intent(this, MapsActivity::class.java)
            startActivityForResult(locationmap,autocomplete_place_code)
        }

        floatingActionButton.setOnClickListener{
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) ==
                    PackageManager.PERMISSION_DENIED){
                    //permission denied
                    val permissions = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
                    //show popup to request runtime permission
                    requestPermissions(permissions, PERMISSION_CODE)
                }
                else{
                    //permission already granted
                    showImagePickerDialog()
                }
            }
            else{
                //system OS is < Marshmallow
                showImagePickerDialog()
            }
        }

        button.setOnClickListener {
            // [START write_message]
            // Write a message to the database

            val database = FirebaseDatabase.getInstance()

            //val myRef = database.getReference("message")


            //myRef.setValue("Hello, World!")

            val myRef = database.getReference("User/Event").push()
            val eventsref =myRef.child("events")

            val event_name= etname.text.toString()
            val event_date= etPlannedDate.text.toString()
            val event_time= etPlannedTime.text.toString()
            val event_location= etlocation.text.toString()
            val event_about = etabout.text.toString()

/*
            val map: HashMap<String, String> = hashMapOf(
                "message1" to message1,
                "message2" to message2
            )
*/
            val events= hashMapOf(
            "name" to event_name,
            "date" to event_date,
            "time" to event_time,
            "location" to event_location,
            "about" to event_about
            )

            //events["name"] = Event (event_name,event_date,event_time,event_location)

            myRef.setValue(events as Map<String, Any>)
                .addOnFailureListener {
                    Snackbar .make(findViewById(android.R.id.content), "Error while saving the Event", Snackbar.LENGTH_LONG).show()
                }
                .addOnSuccessListener {
                    Snackbar .make(findViewById(android.R.id.content), "Event Saved Successfully", Snackbar.LENGTH_LONG).show()
                }
        }
    }

    private fun showDatePickerDialog() {
        val newFragment = DatePickerFragment.newInstance(DatePickerDialog.OnDateSetListener { _, year, month, day ->
            // +1 because January is zero
            val selectedDate = day.toString() + " / " + (month + 1) + " / " + year
            etPlannedDate.setText(selectedDate)
        })

        newFragment.show(supportFragmentManager, "datePicker")
    }

    private fun showTimePickerDialog() {
        val newFragment= TimePickerFragment.newInstance(TimePickerDialog.OnTimeSetListener { _, hour, minute ->
            val timeValue = String.format("%02d",hour) + ":" + String.format("%02d",minute)
            etPlannedTime.setText(timeValue)

        })

        newFragment.show(supportFragmentManager, "timePicker")
    }


    private fun showImagePickerDialog() {
        //Intent to pick image
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, IMAGE_PICK_CODE)
        }

    companion object {
        //image pick code
        private val IMAGE_PICK_CODE = 1000

        //Permission code
        private val PERMISSION_CODE = 1001
    }

    //handle requested permission result
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when(requestCode){
            PERMISSION_CODE -> {
                if (grantResults[0] ==
                    PackageManager.PERMISSION_GRANTED){
                    //permission from popup granted
                    showImagePickerDialog()
                }
                else{
                    //permission from popup denied
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == autocomplete_place_code) {
            if(resultCode == Activity.RESULT_OK) {
                val placeidString = data?.getStringExtra("place_id")
                val placenameString = data?.getStringExtra("place_name")
                etlocation.setText(placenameString)
            }
        }
        if (resultCode == Activity.RESULT_OK && requestCode == IMAGE_PICK_CODE){
            imageView.setImageURI(data?.data)

            var uri= data?.data!!

            val storageRef = storage.reference
            val mountainsRef = storageRef.child("images/${uri.lastPathSegment}")
            // Get the data from an ImageView as bytes

            var uploadTask = mountainsRef.putFile(uri)
            uploadTask.addOnFailureListener {
                Snackbar .make(findViewById(android.R.id.content), "Error while uploading the Image", Snackbar.LENGTH_LONG).show()
            }.addOnSuccessListener {
                Snackbar .make(findViewById(android.R.id.content), "Image uploaded successfully", Snackbar.LENGTH_LONG).show()
            }

        }

    }
}


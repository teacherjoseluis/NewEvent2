package com.example.newevent2

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.newevent2.ui.dialog.DatePickerFragment
import TimePickerFragment
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.FirebaseApp
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import com.theartofdev.edmodo.cropper.CropImage
import kotlinx.android.synthetic.main.event_edit.*
import android.Manifest
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.eventdetail_event.view.*
import kotlinx.android.synthetic.main.task_editdetail.*

class Event_EditDetail : AppCompatActivity() {

    private val autocomplete_place_code = 1
    private lateinit var uri: Uri
    var placeid = ""
    var latitude = 0.0
    var longitude = 0.0
    var address = ""

    lateinit var storage: FirebaseStorage

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(applicationContext)
        setContentView(R.layout.event_edit)

        val intent = intent
        val eventkey = intent.getStringExtra("eventkey").toString()
        val eventname = intent.getStringExtra("name").toString()
        val eventdate = intent.getStringExtra("date").toString()
        val eventtime = intent.getStringExtra("time").toString()
        val eventabout = intent.getStringExtra("about").toString()
        val eventlocation = intent.getStringExtra("location").toString()
        placeid = intent.getStringExtra("location").toString()
        latitude = intent.getDoubleExtra("latitude", 0.0)
        longitude = intent.getDoubleExtra("longitude", 0.0)
        address = intent.getStringExtra("address").toString()
        uri = Uri.parse(intent.getStringExtra("imageurl").toString())

        val eventimageurl = intent.getStringExtra("imageurl").toString()

        storage = FirebaseStorage.getInstance()
        val storageRef =
            storage.getReferenceFromUrl("gs://brides-n-grooms.appspot.com/images/${eventkey}/${eventimageurl}")

        Glide.with(this)
            .load(storageRef)
            .centerCrop()
            .into(imageViewedit)

        etname.setText(eventname)
        etPlannedDate.setText(eventdate)
        etPlannedTime.setText(eventtime)
        etabout.setText(eventabout)
        etlocation.setText(eventlocation)

        etPlannedDate.setOnClickListener {
            showDatePickerDialog()
        }

        etPlannedTime.setOnClickListener {
            showTimePickerDialog()
        }

        etlocation.setOnClickListener {
            val locationmap = Intent(this, MapsActivity::class.java)
            startActivityForResult(locationmap, autocomplete_place_code)
        }

        floatingActionButton.setOnClickListener {
            showImagePickerDialog()
        }

        button.setOnClickListener {
            saveEvent(eventkey)
        }
    }

    private fun showDatePickerDialog() {
        val newFragment =
            DatePickerFragment.newInstance(DatePickerDialog.OnDateSetListener { _, year, month, day ->
                // +1 because January is zero
                val selectedDate = day.toString() + " / " + (month + 1) + " / " + year
                etPlannedDate.setText(selectedDate)
            })

        newFragment.show(supportFragmentManager, "datePicker")
    }

    private fun showTimePickerDialog() {
        val newFragment = TimePickerFragment()
        newFragment.show(supportFragmentManager, "Time Picker")

    }

    private fun showImagePickerDialog() {
        //Intent to pick image
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"

        //Request permissions
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) ==
                PackageManager.PERMISSION_DENIED
            ) {
                //permission denied
                val permissions = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
                //show popup to request runtime permission
                requestPermissions(permissions, PERMISSION_CODE)
            } else {
                //permission already granted
                startActivityForResult(intent, IMAGE_PICK_CODE)
            }
        } else {
            //system OS is < Marshmallow
            startActivityForResult(intent, IMAGE_PICK_CODE)
        }
    }

    companion object {
        //image pick code
        private val IMAGE_PICK_CODE = 1000

        //Permission code
        private val PERMISSION_CODE = 1001
    }

    //handle requested permission result
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            PERMISSION_CODE -> {
                if (grantResults[0] ==
                    PackageManager.PERMISSION_GRANTED
                ) {
                    //permission from popup granted
                    showImagePickerDialog()
                } else {
                    //permission from popup denied
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun saveEvent(eventkey: String) {
        val database = FirebaseDatabase.getInstance()
        val myRef = database.reference
        val postRef = myRef.child("User").child("Event").child(eventkey)

        postRef.child("name").setValue(etname.text.toString())
        postRef.child("date").setValue(etPlannedDate.text.toString())
        postRef.child("time").setValue(etPlannedTime.text.toString())
        postRef.child("about").setValue(etabout.text.toString())
        postRef.child("location").setValue(etlocation.text.toString())
        postRef.child("latitude").setValue(latitude)
        postRef.child("longitude").setValue(longitude)
        postRef.child("address").setValue(address)
        //postRef.child("imageurl").setValue(uri.lastPathSegment)

//        val events = hashMapOf(
//            "name" to etname.text.toString(),
//            "date" to etPlannedDate.text.toString(),
//            "time" to etPlannedTime.text.toString(),
//            "location" to etlocation.text.toString(),
//            "placeid" to placeid,
//            "latitude" to latitude,
//            "longitude" to longitude,
//            "address" to address,
//            "about" to etabout.text.toString(),
//            "imageurl" to uri.lastPathSegment
//        )
//
//        myRef.setValue(events as Map<String, Any>)
//            .addOnFailureListener {
//                Snackbar.make(
//                    findViewById(android.R.id.content),
//                    "Error while saving the Event",
//                    Snackbar.LENGTH_LONG
//                ).show()
//            }
//            .addOnSuccessListener {
//                Snackbar.make(
//                    findViewById(android.R.id.content),
//                    "Event Saved Successfully",
//                    Snackbar.LENGTH_LONG
//                ).show()
//            }

        if (intent.getStringExtra("imageurl").toString() != uri.lastPathSegment) {
            postRef.child("imageurl").setValue(uri.lastPathSegment)

            //key = myRef.key.toString()
            storage = Firebase.storage
            val storageRef = storage.reference
            val mountainsRef = storageRef.child("images/$eventkey/${uri.lastPathSegment}")

            val uploadTask = mountainsRef.putFile(uri)
            //val imageurl = mountainsRef.toString()

            uploadTask.addOnFailureListener {
                Snackbar.make(
                    findViewById(android.R.id.content),
                    "Error while uploading the Image",
                    Snackbar.LENGTH_LONG
                ).show()
            }.addOnSuccessListener {
                Snackbar.make(
                    findViewById(android.R.id.content),
                    "Image uploaded successfully",
                    Snackbar.LENGTH_LONG
                ).show()
                //finish()
            }
        }

        val myevents = Intent(this, MyEvents::class.java)
        finish()
        startActivity(myevents)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == autocomplete_place_code) {
            val placenameString = data?.getStringExtra("place_name")
            placeid = data?.getStringExtra("place_id").toString()
            latitude = data!!.getDoubleExtra("place_latitude", 0.0)
            longitude = data!!.getDoubleExtra("place_longitude", 0.0)
            address = data?.getStringExtra("place_address").toString()
            etlocation.setText(placenameString)
        } else {
            Toast.makeText(this, "Error in autocomplete location", Toast.LENGTH_SHORT).show()
        }

        if (resultCode == Activity.RESULT_OK && requestCode == IMAGE_PICK_CODE) {
            uri = data?.data!!
            CropImage.activity(uri)
                .setMinCropResultSize(80, 80)
                .setMaxCropResultSize(800, 800)
                .start(this)
        }

        if (resultCode == Activity.RESULT_OK && requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result = CropImage.getActivityResult(data)
            if (resultCode == Activity.RESULT_OK) {
                uri = result.uri

                Glide.with(this)
                    .load(uri.toString())
                    .centerCrop()
                    .into(imageViewedit)

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                val error = result.error
            }
        }
    }
}



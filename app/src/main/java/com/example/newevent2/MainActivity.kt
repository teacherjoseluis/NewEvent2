package com.example.newevent2

import TimePickerFragment
import android.Manifest
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
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.FirebaseApp
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import com.theartofdev.edmodo.cropper.CropImage
import kotlinx.android.synthetic.main.activity_main.*
import java.lang.Exception

class MainActivity() : AppCompatActivity() {

    private val autocomplete_place_code = 1
    private lateinit var uri: Uri
    private lateinit var placeid: String
    var latitude = 0.0
    var longitude = 0.0
    private lateinit var address: String

    lateinit var storage: FirebaseStorage

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(applicationContext)
        setContentView(R.layout.activity_main)

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
            saveEvent()
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
        /* val newFragment= TimePickerFragment.newInstance(TimePickerDialog.OnTimeSetListener { _, hour, minute ->
            val timeValue = String.format("%02d",hour) + ":" + String.format("%02d",minute)
            etPlannedTime.setText(timeValue)
        })

        newFragment.show(supportFragmentManager, "timePicker") */

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

    private fun saveEvent() {
        val database = FirebaseDatabase.getInstance()
        val myRef = database.getReference("User/Event").push()
        var key = ""

        val events = hashMapOf(
            "name" to etname.text.toString(),
            "date" to etPlannedDate.text.toString(),
            "time" to etPlannedTime.text.toString(),
            "location" to etlocation.text.toString(),
            "placeid" to placeid,
            "latitude" to latitude,
            "longitude" to longitude,
            "address" to address,
            "about" to etabout.text.toString(),
            "imageurl" to uri.lastPathSegment
            //"images/${uri.lastPathSegment}"
        )

        myRef.setValue(events as Map<String, Any>)
            .addOnFailureListener {
                Snackbar.make(
                    findViewById(android.R.id.content),
                    "Error while saving the Event",
                    Snackbar.LENGTH_LONG
                ).show()
            }
            .addOnSuccessListener {
                Snackbar.make(
                    findViewById(android.R.id.content),
                    "Event Saved Successfully",
                    Snackbar.LENGTH_LONG
                ).show()
            }

        key = myRef.key.toString()
        storage = Firebase.storage
        val storageRef = storage.reference
        val mountainsRef = storageRef.child("images/$key/${uri.lastPathSegment}")

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

        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == autocomplete_place_code) {
            val placenameString = data?.getStringExtra("place_name")
            placeid = data?.getStringExtra("place_id").toString()
            latitude= data!!.getDoubleExtra("place_latitude",0.0)
            longitude= data!!.getDoubleExtra("place_longitude", 0.0)
            address=data?.getStringExtra("place_address").toString()
            etlocation.setText(placenameString)
        } else {
            Toast.makeText(this, "Error in autocomplete location", Toast.LENGTH_SHORT).show()
        }

        if (resultCode == Activity.RESULT_OK && requestCode == IMAGE_PICK_CODE) {
            uri = data?.data!!
            CropImage.activity(uri)
                .setMinCropResultSize(80,80)
                .setMaxCropResultSize(800,800)
                .start(this)
            //imageView.setImageURI(data?.data)
/*
            storage=Firebase.storage
            val storageRef = storage.reference
            val mountainsRef = storageRef.child("images/${uri.lastPathSegment}")


            val uploadTask = mountainsRef.putFile(uri)
            uploadTask.addOnFailureListener {
                Snackbar .make(findViewById(android.R.id.content), "Error while uploading the Image", Snackbar.LENGTH_LONG).show()
            }.addOnSuccessListener {
                Snackbar .make(findViewById(android.R.id.content), "Image uploaded successfully", Snackbar.LENGTH_LONG).show()
            }
*/
        }

        if (resultCode == Activity.RESULT_OK && requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result = CropImage.getActivityResult(data)
            if (resultCode == Activity.RESULT_OK) {
                uri = result.uri
                imageView.setImageURI(uri)
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                val error = result.error
            }
        }

    }
}


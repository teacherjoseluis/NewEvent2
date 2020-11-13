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
import android.content.ContentResolver
import android.widget.ImageView
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.event_edit.button
import kotlinx.android.synthetic.main.event_edit.etPlannedDate
import kotlinx.android.synthetic.main.event_edit.etPlannedTime
import kotlinx.android.synthetic.main.event_edit.etabout
import kotlinx.android.synthetic.main.event_edit.etlocation
import kotlinx.android.synthetic.main.event_edit.etname
import kotlinx.android.synthetic.main.eventdetail_event.view.*
import kotlinx.android.synthetic.main.task_editdetail.*

class Event_EditDetail : AppCompatActivity() {

    private val autocomplete_place_code = 1
    private lateinit var uri: Uri
    var eventkey = ""
    var event_imageurl = ""
    var event_placeid = ""
    var event_latitude = 0.0
    var event_longitude = 0.0
    var event_address = ""

    lateinit var storage: FirebaseStorage

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        FirebaseApp.initializeApp(applicationContext)
        setContentView(R.layout.activity_main)
        val imageView = findViewById<ImageView>(R.id.imageView)


        val intent = intent
        eventkey = intent.getStringExtra("eventkey").toString()
        event_imageurl = intent.getStringExtra("imageurl").toString()
        val eventname = intent.getStringExtra("name").toString()
        val eventdate = intent.getStringExtra("date").toString()
        val eventtime = intent.getStringExtra("time").toString()
        val eventabout = intent.getStringExtra("about").toString()
        val eventlocation = intent.getStringExtra("location").toString()
        event_placeid = intent.getStringExtra("location").toString()
        event_latitude = intent.getDoubleExtra("latitude", 0.0)
        event_longitude = intent.getDoubleExtra("longitude", 0.0)
        event_address = intent.getStringExtra("address").toString()
        uri = Uri.parse(intent.getStringExtra("imageurl").toString())


        var storageRef: Any
        if (event_imageurl != "null") {
            storage = FirebaseStorage.getInstance()
            storageRef =
                storage.getReferenceFromUrl("gs://brides-n-grooms.appspot.com/images/${eventkey}/${event_imageurl}")
        } else {
            storageRef =
                Uri.parse(
                    ContentResolver.SCHEME_ANDROID_RESOURCE +
                            "://" + resources.getResourcePackageName(R.drawable.frame_206)
                            + '/' + resources.getResourceTypeName(R.drawable.frame_206) + '/' + resources.getResourceEntryName(
                        R.drawable.frame_206
                    )
                ).toString()
        }
        Glide.with(this)
            .load(storageRef)
            .centerCrop()
            .into(imageView)

        etname.setText(eventname)
        etPlannedDate.setText(eventdate)
        etPlannedTime.setText(eventtime)
        etabout.setText(eventabout)
        etlocation.setText(eventlocation)

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

        etabout.setOnClickListener {
            etabout.error = null
        }

        etlocation.setOnClickListener {
            val locationmap = Intent(this, MapsActivity::class.java)
            startActivityForResult(locationmap, autocomplete_place_code)
        }

        saveImageActionButton.setOnClickListener {
            showImagePickerDialog()
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
            if (etabout.text.toString().isEmpty()) {
                etabout.error = "Enter description about Event!"
                inputvalflag = false
            }
            if (inputvalflag) {
                saveEvent()
            }
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
        val evententity = EventEntity().apply {
            key = eventkey
            name = etname.text.toString()
            date = etPlannedDate.text.toString()
            time = etPlannedTime.text.toString()
            location = etlocation.text.toString()
            placeid = event_placeid
            latitude = event_latitude
            longitude = event_longitude
            address = event_address.toString()
            about = etabout.text.toString()
            imageurl = event_imageurl
        }
        evententity.editEvent(uri)
        //onBackPressed()

        val myevents = Intent(this, MyEvents::class.java)
        finish()
        startActivity(myevents)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == autocomplete_place_code) {
            val placenameString = data?.getStringExtra("place_name")
            event_placeid = data?.getStringExtra("place_id").toString()
            event_latitude = data!!.getDoubleExtra("place_latitude", 0.0)
            event_longitude = data!!.getDoubleExtra("place_longitude", 0.0)
            event_address = data?.getStringExtra("place_address").toString()
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
                    .into(imageView)

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                val error = result.error
            }
        }
    }
}



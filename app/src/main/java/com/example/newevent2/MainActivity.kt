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
import com.theartofdev.edmodo.cropper.CropImage
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.button

class MainActivity() : AppCompatActivity() {

    private val autocompletePlaceCode = 1

    private var event_placeid: String? = null
    private var event_latitude = 0.0
    private var event_longitude = 0.0
    private var event_address: String? = null
    private var uri: Uri? = null
    //private lateinit var storage: FirebaseStorage

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //FirebaseApp.initializeApp(applicationContext)
        setContentView(R.layout.activity_main)

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
            etlocation.error = null
            val locationmap = Intent(this, MapsActivity::class.java)
            startActivityForResult(locationmap, autocompletePlaceCode)
        }

        etabout.setOnClickListener {
            etabout.error = null
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
        internal val PERMISSION_CODE = 1001
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
            name = etname.text.toString()
            date = etPlannedDate.text.toString()
            time = etPlannedTime.text.toString()
            location = etlocation.text.toString()
            placeid = event_placeid.toString()
            latitude = event_latitude
            longitude = event_longitude
            address = event_address.toString()
            about = etabout.text.toString()
            imageurl = uri?.lastPathSegment.toString()
        }
        evententity.addEvent(uri)
        onBackPressed()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == autocompletePlaceCode) {
            val placenameString = data?.getStringExtra("place_name")
            event_placeid = data!!.getStringExtra("place_id").toString()
            event_latitude = data!!.getDoubleExtra("place_latitude", 0.0)
            event_longitude = data!!.getDoubleExtra("place_longitude", 0.0)
            event_address = data!!.getStringExtra("place_address").toString()
            etlocation.setText(placenameString)
        } else {
            //Toast.makeText(this, "Error in autocomplete location", Toast.LENGTH_SHORT).show()
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
                imageView.setImageURI(uri)
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                val error = result.error
            }
        }
    }
}


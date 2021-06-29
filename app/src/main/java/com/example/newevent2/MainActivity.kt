package com.example.newevent2

import TimePickerFragment
import android.Manifest
import android.app.Activity
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.DatePicker
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.example.newevent2.Functions.*
import com.example.newevent2.Functions.validateOldDate
import com.example.newevent2.MVP.EventPresenter
import com.example.newevent2.MVP.EventSummaryPresenter
import com.example.newevent2.MVP.ImagePresenter
import com.example.newevent2.Model.Event
import com.example.newevent2.Model.EventDBHelper
import com.example.newevent2.Model.EventModel
import com.example.newevent2.ui.TextValidate
import com.example.newevent2.ui.dialog.DatePickerFragment
import com.theartofdev.edmodo.cropper.CropImage
import kotlinx.android.synthetic.main.eventform_layout.*
import kotlinx.android.synthetic.main.task_editdetail.*
import java.util.*

class MainActivity() : AppCompatActivity(), ImagePresenter.EventImage, EventPresenter.EventItem {

    private val autocompletePlaceCode = 1

    private var event_key = ""
    private var event_placeid = ""
    private var event_latitude = 0.0
    private var event_longitude = 0.0
    private var event_address = ""
    private var uri: Uri? = null

    private lateinit var presenterevent: EventPresenter
    private lateinit var imagePresenter: ImagePresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.eventform_layout)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.setHomeAsUpIndicator(R.drawable.icons8_left_24)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        presenterevent = EventPresenter(applicationContext, this)
        presenterevent.getEventDetail()

        eventname.setOnClickListener {
            eventname.error = null
        }

        eventname.onFocusChangeListener = View.OnFocusChangeListener { _, p1 ->
            if (!p1) {
                val validationmessage = TextValidate(taskname).namefieldValidate()
                if (validationmessage != "") {
                    eventname.error = "Error in Event name: $validationmessage"
                }
            }
        }

        eventdate.setOnClickListener {
            eventdate.error = null
            showDatePickerDialog()
        }

        eventtime.setOnClickListener {
            eventtime.error = null
            showTimePickerDialog()
        }

        eventlocation.setOnClickListener {
            eventlocation.error = null
            val locationmap = Intent(this, MapsActivity::class.java)
            startActivityForResult(locationmap, autocompletePlaceCode)
        }

        editImageActionButton.setOnClickListener {
            showImagePickerDialog()
        }

        savebutton.setOnClickListener {
            var inputvalflag = true
            if (eventname.text.toString().isEmpty()) {
                eventname.error = "Event name is required!"
                inputvalflag = false
            } else {
                val validationmessage = TextValidate(eventname).namefieldValidate()
                if (validationmessage != "") {
                    eventname.error = "Error in Event name: $validationmessage"
                    inputvalflag = false
                }
            }
            if (eventdate.text.toString().isEmpty()) {
                eventdate.error = "Event date is required!"
                inputvalflag = false
            }
            if (eventtime.text.toString().isEmpty()) {
                eventtime.error = "Event time is required!"
                inputvalflag = false
            }
            if (eventlocation.text.toString().isEmpty()) {
                eventlocation.error = "Event location is required!"
                inputvalflag = false
            }
            if (inputvalflag) {
                saveEvent()
            }
        }
    }

    private fun showDatePickerDialog() {
        val newFragment =
            DatePickerFragment.newInstance((object : DatePickerDialog.OnDateSetListener {
                @RequiresApi(Build.VERSION_CODES.O)
                override fun onDateSet(p0: DatePicker?, p1: Int, p2: Int, p3: Int) {
                    if (validateOldDate(p1, p2 + 1, p3)) {
                        val selectedDate = p3.toString() + "/" + (p2 + 1) + "/" + p1
                        eventdate.setText(selectedDate)
                    } else {
                        eventdate.error = "Event date is invalid!"
                    }
                }
            }))
        newFragment.show(supportFragmentManager, "datePicker")
    }

    private fun showTimePickerDialog() {
        val newFragment = TimePickerFragment(eventtime)
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
        val user = com.example.newevent2.Functions.getUserSession(applicationContext)
        val event = Event().apply {
            key = event_key
            placeid = event_placeid
            latitude = event_latitude
            longitude = event_longitude
            address = event_address
            name = eventname.text.toString()
            date = eventdate.text.toString()
            time = eventtime.text.toString()
            location = eventlocation.text.toString()
        }
        val eventmodel = EventModel()
        eventmodel.editEvent(user.key, event, uri, object : EventModel.FirebaseSaveSuccess {
            override fun onSaveSuccess(eventid: String) {
                //Updating local storage
                val eventdb = EventDBHelper(applicationContext)
                eventdb.update(event)

                if(uri != null) {
                    //There was a change in the event image
                    replaceImage(applicationContext, "eventimage", user.key, user.eventid, uri!!)
                }
            }
        })
        val resultIntent = Intent()
        setResult(Activity.RESULT_OK, resultIntent)
        finish()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == autocompletePlaceCode) {
            val placenameString = data?.getStringExtra("place_name")
            event_placeid = data!!.getStringExtra("place_id").toString()
            event_latitude = data!!.getDoubleExtra("place_latitude", 0.0)
            event_longitude = data!!.getDoubleExtra("place_longitude", 0.0)
            event_address = data!!.getStringExtra("place_address").toString()
            eventlocation.setText(placenameString)
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
                //delImgfromSD("eventimage", this@MainActivity)
                //eventimage.setImageURI(uri)
                // event_imageurl = uri.lastPathSegment.toString()
                Glide.with(this@MainActivity)
                    .load(uri)
                    .apply(RequestOptions.circleCropTransform())
                    .into(eventimage)

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                val error = result.error
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    override fun onEventImage(mContext: Context, inflatedView: View?, packet: Any) {
        Glide.with(mContext)
            .load(packet)
            .apply(RequestOptions.circleCropTransform())
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>?,
                    isFirstResource: Boolean
                ): Boolean {
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    return false
                }
            }).placeholder(R.drawable.avatar2)
            .into(eventimage)
    }

    override fun onEvent(event: Event) {
        eventname.setText(event.name)
        eventdate.setText(event.date)
        eventtime.setText(event.time)
        eventlocation.setText(event.location)
        event_placeid = event.placeid
        event_latitude = event.latitude
        event_longitude = event.longitude
        event_address = event.address
        event_key = event.key

        imagePresenter = ImagePresenter(applicationContext, this@MainActivity)
        imagePresenter.getEventImage()
    }

    override fun onEventError(errcode: String) {
        //This should not be reached as there will always be an Event to be edited
    }
}


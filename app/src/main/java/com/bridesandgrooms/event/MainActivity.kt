package com.bridesandgrooms.event

import TimePickerFragment
import android.Manifest
import android.app.Activity
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.bumptech.glide.Glide
import com.yalantis.ucrop.UCrop
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.bridesandgrooms.event.Functions.*
import com.bridesandgrooms.event.Functions.validateOldDate
import com.bridesandgrooms.event.MVP.EventPresenter
import com.bridesandgrooms.event.MVP.ImagePresenter
import com.bridesandgrooms.event.Model.Event
import com.bridesandgrooms.event.Model.EventDBHelper
import com.bridesandgrooms.event.Model.EventModel
import com.bridesandgrooms.event.Model.MyFirebaseApp
import com.bridesandgrooms.event.ui.TextValidate
import com.bridesandgrooms.event.ui.dialog.DatePickerFragment
import com.google.firebase.analytics.FirebaseAnalytics
import kotlinx.android.synthetic.main.eventform_layout.*
import kotlinx.android.synthetic.main.task_editdetail.*
import java.io.File
import java.util.*

class MainActivity : AppCompatActivity(), ImagePresenter.EventImage, EventPresenter.EventItem {

    private val autocompletePlaceCode = 1

    private var eventkey = ""
    private var eventplaceid = ""
    private var eventlatitude = 0.0
    private var eventlongitude = 0.0
    private var eventaddress = ""
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

        try {
            presenterevent = EventPresenter(applicationContext, this)
        } catch (e: Exception) {
            println(e.message)
        }
        presenterevent.getEventDetail()

        eventname.setOnClickListener {
            eventname.error = null
        }

        eventname.onFocusChangeListener = View.OnFocusChangeListener { _, p1 ->
            if (!p1) {
                val validationmessage = TextValidate(taskname).namefieldValidate()
                if (validationmessage != "") {
                    val errormsg = getString(R.string.error_eventname)
                    errormsg.plus(validationmessage)
                    eventname.error = errormsg
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
                eventname.error = getString(R.string.error_tasknameinput)
                inputvalflag = false
            } else {
                val validationmessage = TextValidate(eventname).namefieldValidate()
                if (validationmessage != "") {
                    val errormsg = getString(R.string.error_eventname)
                    errormsg.plus(validationmessage)
                    eventname.error = errormsg
                    inputvalflag = false
                }
            }
            if (eventdate.text.toString().isEmpty()) {
                eventdate.error = getString(R.string.error_taskdateinput)
                inputvalflag = false
            }
            if (eventtime.text.toString().isEmpty()) {
                eventtime.error = getString(R.string.error_timeinput)
                inputvalflag = false
            }
            if (eventlocation.text.toString().isEmpty()) {
                eventlocation.error = getString(R.string.error_locationinput)
                inputvalflag = false
            }
            if (inputvalflag) {
                saveEvent()
            }
        }
    }

    private fun showDatePickerDialog() {
        val newFragment =
            DatePickerFragment.newInstance((DatePickerDialog.OnDateSetListener { _, p1, p2, p3 ->
                if (validateOldDate(p1, p2 + 1, p3)) {
                    val selectedDate = p3.toString() + "/" + (p2 + 1) + "/" + p1
                    eventdate.setText(selectedDate)
                } else {
                    eventdate.error = getString(R.string.error_invaliddate)
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
    }

    companion object {
        //image pick code
        private const val IMAGE_PICK_CODE = 1000

        //Permission code
        internal const val PERMISSION_CODE = 1001
    }

    //handle requested permission result
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PERMISSION_CODE -> {
                if (grantResults[0] ==
                    PackageManager.PERMISSION_GRANTED
                ) {
                    //permission from popup granted
                    showImagePickerDialog()
                } else {
                    //permission from popup denied
                    Toast.makeText(this, getString(R.string.permissiondenied), Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }

    private fun saveEvent() {
        val user = userdbhelper.getUser(userdbhelper.getUserKey())
        val event = Event().apply {
            key = eventkey
            placeid = eventplaceid
            latitude = eventlatitude
            longitude = eventlongitude
            address = eventaddress
            name = eventname.text.toString()
            date = eventdate.text.toString()
            time = eventtime.text.toString()
            location = eventlocation.text.toString()
        }
        val eventmodel = EventModel()
        eventmodel.editEvent(user.userid!!, event, object : EventModel.FirebaseSaveSuccess {
            override fun onSaveSuccess(eventid: String) {
                //Updating local storage
                val eventdb = EventDBHelper(applicationContext)
                eventdb.update(event)

                if (uri != null) {
                    //There was a change in the event image
                    replaceImage(
                        applicationContext,
                        "eventimage",
                        user.userid!!,
                        user.eventid,
                        uri!!
                    )
                }

                if (eventplaceid != "") {
                    //There was a change in the event location
                    delImgfromSD(ImagePresenter.PLACEIMAGE, this@MainActivity)
                }
            }
        })
        // ------- Analytics call ----------------
        val bundle = Bundle()
        bundle.putString("LOCATION", event.location)
        bundle.putString("DATE", event.date)
        bundle.putString("TIME", event.date)
        bundle.putDouble("LATITUDE", event.latitude)
        bundle.putDouble("LONGITUDE", event.longitude)
        bundle.putString(FirebaseAnalytics.Param.SCREEN_NAME, javaClass.simpleName)
        MyFirebaseApp.mFirebaseAnalytics.logEvent("EDITEVENT", bundle)
        //----------------------------------------

        val resultIntent = Intent()
        setResult(Activity.RESULT_OK, resultIntent)
        finish()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == autocompletePlaceCode) {
            val placenameString = data?.getStringExtra("place_name")
            eventplaceid = data!!.getStringExtra("place_id").toString()
            eventlatitude = data.getDoubleExtra("place_latitude", 0.0)
            eventlongitude = data.getDoubleExtra("place_longitude", 0.0)
            eventaddress = data.getStringExtra("place_address").toString()
            eventlocation.setText(placenameString)
        }

//        if (resultCode == Activity.RESULT_OK && requestCode == IMAGE_PICK_CODE) {
//            uri = data?.data!!
//            CropImage.activity(uri)
//                .setMinCropResultSize(80, 80)
//                .setMaxCropResultSize(800, 800)
//                .start(this)
//        }
//
//        if (resultCode == Activity.RESULT_OK && requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
//            val result = CropImage.getActivityResult(data)
//            if (resultCode == Activity.RESULT_OK) {
//                uri = result.uri
//                //delImgfromSD("eventimage", this@MainActivity)
//                //eventimage.setImageURI(uri)
//                // event_imageurl = uri.lastPathSegment.toString()
//                Glide.with(this@MainActivity)
//                    .load(uri)
//                    .apply(RequestOptions.circleCropTransform())
//                    .into(eventimage)
//
//            }
//        }

        if (resultCode == Activity.RESULT_OK && requestCode == IMAGE_PICK_CODE) {
            uri = data?.data!!
            val destinationUri = Uri.fromFile(File(cacheDir, "cropped_image.jpg")) // Specify the destination file URI for the cropped image
            UCrop.of(uri!!, destinationUri)
                .withAspectRatio(1f, 1f) // Set the desired aspect ratio (change as needed)
                .withMaxResultSize(800, 800) // Set the maximum result size for the cropped image
                .start(this)
        }

        if (resultCode == Activity.RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
            val resultUri = UCrop.getOutput(data!!)
            if (resultUri != null) {
                uri = resultUri
                //delImgfromSD("eventimage", this@MainActivity)
                //eventimage.setImageURI(uri)
                // event_imageurl = uri.lastPathSegment.toString()
                Glide.with(this@MainActivity)
                    .load(uri)
                    .apply(RequestOptions.circleCropTransform())
                    .into(eventimage)
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    override fun onEventImage(context: Context, inflatedView: View?, packet: Any) {
        Glide.with(context)
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
        eventplaceid = event.placeid
        eventlatitude = event.latitude
        eventlongitude = event.longitude
        eventaddress = event.address
        eventkey = event.key

        try {
            imagePresenter = ImagePresenter(applicationContext, this@MainActivity)
        } catch (e: Exception) {
            println(e.message)
        }
        imagePresenter.getEventImage()
    }

    override fun onEventError(errcode: String) {
        //This should not be reached as there will always be an Event to be edited
    }
}

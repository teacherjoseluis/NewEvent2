package com.bridesandgrooms.event

import Application.AnalyticsManager
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
import androidx.databinding.DataBindingUtil
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
import Application.MyFirebaseApp
import android.util.Log
import androidx.core.content.ContextCompat
import com.baoyachi.stepview.HorizontalStepView
import com.bridesandgrooms.event.Model.User
import com.bridesandgrooms.event.Model.UserDBHelper
import com.bridesandgrooms.event.databinding.EventformLayoutBinding
import com.bridesandgrooms.event.UI.TextValidate
import com.bridesandgrooms.event.UI.dialog.DatePickerFragment
import com.google.firebase.analytics.FirebaseAnalytics
//import kotlinx.android.synthetic.main.eventform_layout.*
//import kotlinx.android.synthetic.main.task_editdetail.*
import java.io.File

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
    private lateinit var binding: EventformLayoutBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val user = User().getUser(this)

        binding = DataBindingUtil.setContentView(this, R.layout.eventform_layout)
        binding.eventname.setOnClickListener {
            binding.eventname.error = null
        }

        binding.eventname.onFocusChangeListener = View.OnFocusChangeListener { _, p1 ->
            if (!p1) {
                val validationmessage = TextValidate(binding.eventname).namefieldValidate()
                if (validationmessage != "") {
                    val errormsg = binding.eventname.toString()
                    errormsg.plus(validationmessage)
                    binding.eventname.error = errormsg
                }
            }
        }

        binding.eventdate.setOnClickListener {
            AnalyticsManager.getInstance().trackUserInteraction(SCREEN_NAME, "Pick_Date")
            binding.eventdate.error = null
            showDatePickerDialog()
        }

        binding.eventtime.setOnClickListener {
            AnalyticsManager.getInstance().trackUserInteraction(SCREEN_NAME, "Pick_Time")
            binding.eventtime.error = null
            showTimePickerDialog()
        }

        binding.eventlocation.setOnClickListener {
            binding.eventlocation.error = null
            AnalyticsManager.getInstance().trackNavigationEvent(SCREEN_NAME,"GooglePlaces")
            val locationmap = Intent(this, MapsActivity::class.java)
            startActivityForResult(locationmap, autocompletePlaceCode)
        }

        binding.editImageActionButton.setOnClickListener {
            AnalyticsManager.getInstance().trackUserInteraction(SCREEN_NAME, "Event_Image")
            showImagePickerDialog()
        }

        binding.savebutton.setOnClickListener {
            AnalyticsManager.getInstance().trackUserInteraction(SCREEN_NAME,"Edit_Event")
            var inputvalflag = true
            if (binding.eventname.text.toString().isEmpty()) {
                binding.eventname.error = getString(R.string.error_tasknameinput)
                inputvalflag = false
            } else {
                val validationmessage = TextValidate(binding.eventname).namefieldValidate()
                if (validationmessage != "") {
                    val errormsg = binding.eventname.toString()
                    errormsg.plus(validationmessage)
                    binding.eventname.error = errormsg
                    inputvalflag = false
                }
            }
            if (binding.eventdate.text.toString().isEmpty()) {
                binding.eventdate.error = getString(R.string.error_taskdateinput)
                inputvalflag = false
            }
            if (binding.eventtime.text.toString().isEmpty()) {
                binding.eventtime.error = getString(R.string.error_timeinput)
                inputvalflag = false
            }
            if (binding.eventlocation.text.toString().isEmpty()) {
                binding.eventlocation.error = getString(R.string.error_locationinput)
                inputvalflag = false
            }
            if (inputvalflag) {
                saveEvent()
            }
        }

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.setHomeAsUpIndicator(R.drawable.icons8_left_24)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        try {
            presenterevent = EventPresenter(applicationContext, this)
            presenterevent.getEventDetail()
        } catch (e: Exception) {
            AnalyticsManager.getInstance().trackError(
                SCREEN_NAME,
                e.message.toString(),
                "EventPresenter",
                e.stackTraceToString()
            )
            Log.e(TAG, e.message.toString())
        }

        //Load with the achievements obtained by the user -------------------------------------------
        val stepsBeanList = user.onboardingprogress(this)
        val stepview = binding.root.findViewById<HorizontalStepView>(R.id.step_view)
        stepview
            .setStepViewTexts(stepsBeanList)
            .setTextSize(12)
            .setStepsViewIndicatorCompletedLineColor(
                ContextCompat.getColor(
                    this,
                    R.color.azulmasClaro
                )
            )
            .setStepsViewIndicatorUnCompletedLineColor(
                ContextCompat.getColor(
                    this,
                    R.color.rosaChillon
                )
            )
            .setStepViewComplectedTextColor(
                ContextCompat.getColor(
                    this,
                    R.color.azulmasClaro
                )
            )
            .setStepViewUnComplectedTextColor(
                ContextCompat.getColor(
                    this,
                    R.color.rosaChillon
                )
            )
            .setStepsViewIndicatorCompleteIcon(
                ContextCompat.getDrawable(
                    this,
                    R.drawable.icons8_checked_rosachillon
                )
            )
            .setStepsViewIndicatorDefaultIcon(
                ContextCompat.getDrawable(
                    this,
                    R.drawable.circle_rosachillon
                )
            )
            .setStepsViewIndicatorAttentionIcon(
                ContextCompat.getDrawable(
                    this,
                    R.drawable.alert_icon_rosachillon
                )
            )

    }

    private fun showDatePickerDialog() {
        val newFragment =
            DatePickerFragment.newInstance((DatePickerDialog.OnDateSetListener { _, p1, p2, p3 ->
                if (validateOldDate(p1, p2 + 1, p3)) {
                    val selectedDate = p3.toString() + "/" + (p2 + 1) + "/" + p1
                    binding.eventdate.setText(selectedDate)
                } else {
                    binding.eventdate.error = getString(R.string.error_invaliddate)
                }
            }))
        newFragment.show(supportFragmentManager, "datePicker")
    }

    private fun showTimePickerDialog() {
        val newFragment = TimePickerFragment(binding.eventtime)
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
        private const val IMAGE_PICK_CODE = 1000
        internal const val PERMISSION_CODE = 1001
        const val SCREEN_NAME = "Main_Activity"
        const val TAG = "MainActivity"
    }

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
        val user = User().getUser(this)
        val event = Event().apply {
            key = eventkey
            placeid = eventplaceid
            latitude = eventlatitude
            longitude = eventlongitude
            address = eventaddress
            name = binding.eventname.text.toString()
            date = binding.eventdate.text.toString()
            time = binding.eventtime.text.toString()
            location = binding.eventlocation.text.toString()
        }
        val eventmodel = EventModel()
        eventmodel.editEvent(user.userid!!, event, object : EventModel.FirebaseSaveSuccess {
            override fun onSaveSuccess(eventid: String) {
                val eventdb = EventDBHelper(this@MainActivity)
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
            binding.eventlocation.setText(placenameString)
        }

        if (resultCode == Activity.RESULT_OK && requestCode == IMAGE_PICK_CODE) {
            uri = data?.data!!
            val destinationUri = Uri.fromFile(
                File(
                    cacheDir,
                    "cropped_image.jpg"
                )
            ) // Specify the destination file URI for the cropped image
            UCrop.of(uri!!, destinationUri)
                .withAspectRatio(1f, 1f) // Set the desired aspect ratio (change as needed)
                .withMaxResultSize(800, 800) // Set the maximum result size for the cropped image
                .start(this)
        }

        if (resultCode == Activity.RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
            val resultUri = UCrop.getOutput(data!!)
            if (resultUri != null) {
                uri = resultUri
                Glide.with(this@MainActivity)
                    .load(uri)
                    .into(binding.eventimage)
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
            .into(binding.eventimage)
    }

    override fun onEvent(event: Event) {
        binding.eventname.setText(event.name)
        binding.eventdate.setText(event.date)
        binding.eventtime.setText(event.time)
        binding.eventlocation.setText(event.location)
        eventplaceid = event.placeid
        eventlatitude = event.latitude
        eventlongitude = event.longitude
        eventaddress = event.address
        eventkey = event.key

        try {
            imagePresenter = ImagePresenter(applicationContext, this@MainActivity)
            imagePresenter.getEventImage()
        } catch (e: Exception) {
            AnalyticsManager.getInstance().trackError(
                SCREEN_NAME,
                e.message.toString(),
                "ImagePresenter",
                e.stackTraceToString()
            )
            Log.e(TAG, e.message.toString())
        }
    }

    override fun onEventError(errcode: String) {
        //This should not be reached as there will always be an Event to be edited
    }
}


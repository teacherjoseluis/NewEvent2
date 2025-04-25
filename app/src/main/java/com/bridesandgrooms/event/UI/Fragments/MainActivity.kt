package com.bridesandgrooms.event.UI.Fragments

import Application.AnalyticsManager
import Application.UserRetrievalException
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
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.yalantis.ucrop.UCrop
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.bridesandgrooms.event.Functions.*
import com.bridesandgrooms.event.Functions.validateOldDate
import com.bridesandgrooms.event.MVP.EventPresenter
import com.bridesandgrooms.event.MVP.ImagePresenter
import com.bridesandgrooms.event.Model.Event
import com.bridesandgrooms.event.Model.EventDBHelper
import com.bridesandgrooms.event.Model.EventModel
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.baoyachi.stepview.HorizontalStepView
import com.bridesandgrooms.event.MapsActivity
import com.bridesandgrooms.event.Model.User
import com.bridesandgrooms.event.R
import com.bridesandgrooms.event.databinding.EventformLayoutBinding
import com.bridesandgrooms.event.UI.Dialogs.DatePickerFragment
import com.bridesandgrooms.event.UI.FieldValidators.InputValidator
import com.bridesandgrooms.event.UI.Fragments.GuestsAll.Companion
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.launch
import java.io.File

class MainActivity : Fragment(), ImagePresenter.EventImage, EventPresenter.EventItem {

    private var eventkey = ""
    private var eventplaceid = ""
    private var eventlatitude = 0.0
    private var eventlongitude = 0.0
    private var eventaddress = ""
    private var uri: Uri? = null

    private lateinit var context: Context
    private lateinit var userSession: User

    private lateinit var presenterevent: EventPresenter
    private lateinit var imagePresenter: ImagePresenter
    private lateinit var binding: EventformLayoutBinding

    private lateinit var imagePickerLauncher: ActivityResultLauncher<Intent>
    private lateinit var cropImageLauncher: ActivityResultLauncher<Intent>
    private lateinit var locationResultLauncher: ActivityResultLauncher<Intent>

    private val focusChangeListener = View.OnFocusChangeListener { view, hasFocus ->
        if (hasFocus && view is TextInputEditText) {
            val parentLayout = view.parent.parent as? TextInputLayout
            parentLayout?.error = null
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        context = requireContext()

        // Launcher for picking an image
        imagePickerLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    result.data?.data?.let { uri ->
                        startImageCrop(uri)
                    }
                }
            }

        // Launcher for UCrop activity
        cropImageLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val resultUri = UCrop.getOutput(result.data!!)
                    resultUri?.let {
                        displayCroppedImage(it)
                    }
                }
            }

        locationResultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    // Handle the result returned from MapsActivity
                    // You may need to use result.data to get the data returned, if any
                    handleLocationResult(result.data)
                }
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        lifecycleScope.launch {
            try {
                userSession = User.getUserAsync()
            } catch (e: UserRetrievalException) {
                displayErrorMsg(getString(R.string.errorretrieveuser))
            } catch (e: Exception) {
                displayErrorMsg(getString(R.string.error_unknown) + " - " + e.toString())
            }
        }

        binding = DataBindingUtil.inflate(inflater, R.layout.eventform_layout, container, false)

        val toolbar = requireActivity().findViewById<MaterialToolbar>(R.id.toolbar)
        toolbar.findViewById<TextView>(R.id.appbartitle)?.text = getString(R.string.event)

        try {
            presenterevent = EventPresenter(context, this)
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
        val colorPrimary = R.color.Primary_cream
        val colorSecondary = R.color.Secondary_cream
        val stepsBeanList = userSession.onboardingprogress(context)
        val stepview = binding.root.findViewById<HorizontalStepView>(R.id.step_view)
        stepview
            .setStepViewTexts(stepsBeanList)
            .setTextSize(12)
            .setStepsViewIndicatorCompletedLineColor(
                ContextCompat.getColor(
                    context,
                    colorSecondary
                )
            )
            .setStepsViewIndicatorUnCompletedLineColor(
                ContextCompat.getColor(
                    context,
                    colorPrimary
                )
            )
            .setStepViewComplectedTextColor(
                ContextCompat.getColor(
                    context,
                    colorSecondary
                )
            )
            .setStepViewUnComplectedTextColor(
                ContextCompat.getColor(
                    context,
                    colorPrimary
                )
            )
            .setStepsViewIndicatorCompleteIcon(
                ContextCompat.getDrawable(
                    context,
                    R.drawable.icons8_checked_primarycolor
                )
            )
            .setStepsViewIndicatorDefaultIcon(
                ContextCompat.getDrawable(
                    context,
                    R.drawable.circle_primarycolor
                )
            )
            .setStepsViewIndicatorAttentionIcon(
                ContextCompat.getDrawable(
                    context,
                    R.drawable.alert_icon_primarycolor
                )
            )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.eventname.onFocusChangeListener = focusChangeListener
        binding.eventdate.onFocusChangeListener = focusChangeListener
        binding.eventdate.setOnClickListener {
            AnalyticsManager.getInstance().trackUserInteraction(SCREEN_NAME, "eventdate", "click")
            showDatePickerDialog()
        }

        binding.eventtime.setOnClickListener {
            AnalyticsManager.getInstance().trackUserInteraction(SCREEN_NAME, "eventtime", "click")
            showTimePickerDialog()
        }

        binding.eventlocation.setOnClickListener {
            AnalyticsManager.getInstance().trackUserInteraction(SCREEN_NAME, "eventlocation", "click")
            val locationmap = Intent(context, MapsActivity::class.java)
            locationResultLauncher.launch(locationmap)
        }

        binding.editImageActionButton.setOnClickListener {
            AnalyticsManager.getInstance().trackUserInteraction(SCREEN_NAME, "editImageActionButton", "click")
            showImagePickerDialog()
        }

        binding.savebutton.setOnClickListener {
            AnalyticsManager.getInstance().trackUserInteraction(SCREEN_NAME, "Edit_Event", null)
            val isValid = validateAllInputs()
            if (isValid) {
                saveEvent()
            }
        }
    }

    private fun validateAllInputs(): Boolean {
        var isValid = true
        val validator = InputValidator(context)

        val nameValidation =
            validator.validate(binding.eventname)
        if (!nameValidation) {
            binding.eventname.error = validator.errorCode
            isValid = false
        }

        val dateValidation =
            validator.validate(binding.eventdate)
        if (!dateValidation) {
            binding.eventdate.error = validator.errorCode
            isValid = false
        }
        return isValid
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
        newFragment.show(requireActivity().supportFragmentManager, "datePicker")
    }

    private fun showTimePickerDialog() {
        val newFragment = TimePickerFragment(binding.eventtime)
        newFragment.show(requireActivity().supportFragmentManager, "Time Picker")
    }

    private fun showImagePickerDialog() {
        //Intent to pick image
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"

        //Request permissions
        if (checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) ==
            PackageManager.PERMISSION_DENIED
        ) {
            //permission denied
            val permissions = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
            //show popup to request runtime permission
            requestPermissions(permissions, PERMISSION_CODE)
        } else {
            //permission already granted
            openImagePicker()
        }
    }

    private fun openImagePicker() {
        val intent = Intent(Intent.ACTION_PICK).apply {
            type = "image/*"
        }
        imagePickerLauncher.launch(intent)
    }

    private fun startImageCrop(sourceUri: Uri) {
        val destinationUri = Uri.fromFile(File(requireContext().cacheDir, "cropped_image.jpg"))
        val options = UCrop.Options().apply {
            withAspectRatio(1f, 1f)
            withMaxResultSize(800, 800)
        }
        UCrop.of(sourceUri, destinationUri)
            .withOptions(options)
            .start(requireContext(), this)
    }

    // Handle the result in the deprecated onActivityResult method
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK && requestCode == UCrop.REQUEST_CROP) {

            val resultUri = UCrop.getOutput(data!!)
            uri = resultUri

            binding.eventimage.setImageURI(uri)
            saveEvent()
        }
    }


    private fun displayCroppedImage(uri: Uri) {
        Glide.with(this)
            .load(uri)
            .into(binding.eventimage)

        // Check if the user has triggered the save operation
        saveEvent()
    }

    private fun handleLocationResult(data: Intent?) {
        val placenameString = data?.getStringExtra("place_name")
        eventplaceid = data!!.getStringExtra("place_id").toString()
        eventlatitude = data.getDoubleExtra("place_latitude", 0.0)
        eventlongitude = data.getDoubleExtra("place_longitude", 0.0)
        eventaddress = data.getStringExtra("place_address").toString()
        binding.eventlocation.setText(placenameString)
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
                    Toast.makeText(
                        context,
                        getString(R.string.permissiondenied),
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }
            }
        }
    }

    private fun saveEvent() {
        //val user = User().getUser()
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
        eventmodel.editEvent(event, object : EventModel.FirebaseSaveSuccess {
            override fun onSaveSuccess(eventid: String) {
                val eventdb = EventDBHelper()
                eventdb.update(event)

                if (uri != null) {
                    //There was a change in the event image
                    replaceImage(
                        context,
                        "eventimage",
                        uri!!
                    )
                }

                if (eventplaceid != "") {
                    //There was a change in the event location
                    delImgfromSD(ImagePresenter.PLACEIMAGE, context)
                }
            }
        })
        finish()
    }
//
//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//
//        super.onActivityResult(requestCode, resultCode, data)
//        if (resultCode == Activity.RESULT_OK && requestCode == autocompletePlaceCode) {
//            val placenameString = data?.getStringExtra("place_name")
//            eventplaceid = data!!.getStringExtra("place_id").toString()
//            eventlatitude = data.getDoubleExtra("place_latitude", 0.0)
//            eventlongitude = data.getDoubleExtra("place_longitude", 0.0)
//            eventaddress = data.getStringExtra("place_address").toString()
//            binding.eventlocation.setText(placenameString)
//        }
//
//        if (resultCode == Activity.RESULT_OK && requestCode == IMAGE_PICK_CODE) {
//            uri = data?.data!!
//            val destinationUri = Uri.fromFile(
//                File(
//                    cacheDir,
//                    "cropped_image.jpg"
//                )
//            ) // Specify the destination file URI for the cropped image
//            UCrop.of(uri!!, destinationUri)
//                .withAspectRatio(1f, 1f) // Set the desired aspect ratio (change as needed)
//                .withMaxResultSize(800, 800) // Set the maximum result size for the cropped image
//                .start(requireActivity())
//        }
//
//        if (resultCode == Activity.RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
//            val resultUri = UCrop.getOutput(data!!)
//            if (resultUri != null) {
//                uri = resultUri
//                Glide.with(this@MainActivity)
//                    .load(uri)
//                    .into(binding.eventimage)
//            }
//        }
//    }

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
            imagePresenter = ImagePresenter(context, this@MainActivity)
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

    fun finish() {
        val fragment = DashboardEvent()
        Handler(Looper.getMainLooper()).postDelayed({
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left)
                .commit()
        }, 500)
    }

    private fun displayErrorMsg(message: String) {
        Toast.makeText(
            requireContext(),
            message,
            Toast.LENGTH_LONG
        ).show()
    }
}


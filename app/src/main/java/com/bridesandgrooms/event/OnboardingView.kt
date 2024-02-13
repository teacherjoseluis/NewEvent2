package com.bridesandgrooms.event

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bridesandgrooms.event.UI.dialog.DatePickerFragment
import TimePickerFragment
import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.widget.Button
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.lifecycleScope
import com.bridesandgrooms.event.Functions.onBoarding
import com.bridesandgrooms.event.Model.Event
import com.bridesandgrooms.event.Model.User
//import kotlinx.android.synthetic.main.onboarding_name.*
import kotlinx.coroutines.*
import android.content.Context
import android.telephony.TelephonyManager
import android.view.View

import android.view.View.OnFocusChangeListener
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.bridesandgrooms.event.Functions.addPayment
import com.bridesandgrooms.event.Functions.addTask
import com.bridesandgrooms.event.Model.Payment
import com.bridesandgrooms.event.Model.Task
import com.bridesandgrooms.event.Functions.RemoteConfigSingleton.getautocreateTaskPayment
import com.bridesandgrooms.event.databinding.OnboardingNameBinding
import com.google.android.gms.common.api.Status
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.TypeFilter
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import java.util.*


class OnboardingView : AppCompatActivity() {

    private var eventlocationname: String? = null
    private var eventplaceid: String? = null
    private var eventlatitude = 0.0
    private var eventlongitude = 0.0
    private var eventaddress: String? = null

    private val TIME_DELAY = 2000
    private var back_pressed: Long = 0

    private lateinit var userSession: User
    private lateinit var taskitem: com.bridesandgrooms.event.Model.Task
    private lateinit var binding: OnboardingNameBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userSession = User()
        userSession.userid = intent.getStringExtra("userid").toString()
        userSession.email = intent.getStringExtra("email").toString()
        userSession.authtype = intent.getStringExtra("authtype").toString()
        userSession.language = this.resources.configuration.locales.get(0).language
        userSession.country = this.resources.configuration.locales.get(0).country

        binding = DataBindingUtil.setContentView(this, R.layout.onboarding_name)

        val position = when (userSession.country) {
            "MX" -> 0
            "CL" -> 1
            "PE" -> 2
            else -> 0
        }
        binding.spinner2.setSelection(position)

        userSession.country = when (binding.spinner2.selectedItemPosition) {
            0 -> "MX"
            1 -> "CL"
            2 -> "PE"
            else -> "MX"
        }

        //---------- Places loading -------------
        if (!Places.isInitialized()) {
            Places.initialize(applicationContext, getString(R.string.google_maps_key), Locale.US)
        }

        // Initialize the AutocompleteSupportFragment.
        val autocompleteFragment =
            supportFragmentManager.findFragmentById(R.id.etlocation)
                    as AutocompleteSupportFragment

        //val tm = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        //autocompleteFragment.setCountry(tm.simCountryIso)
        autocompleteFragment.setCountry(userSession.country)
        autocompleteFragment.setTypeFilter(TypeFilter.ESTABLISHMENT)
        autocompleteFragment.setPlaceFields(
            listOf(
                Place.Field.ID,
                Place.Field.NAME,
                Place.Field.LAT_LNG,
                Place.Field.ADDRESS,
                Place.Field.PHONE_NUMBER,
                Place.Field.RATING,
                Place.Field.USER_RATINGS_TOTAL
            )
        )
        //---------------------------------------

        // Hide Layout for Onboarding Event
        binding.eventonboaarding.visibility = ConstraintLayout.INVISIBLE
        binding.submitevent.visibility = Button.INVISIBLE

        binding.nameinputedit.setOnClickListener {
            binding.nameinputedit.error = null
        }

        binding.submituser.setOnClickListener {
            var inputvalflag = true
            if (binding.nameinputedit.text.toString().isEmpty()) {
                binding.nameinputedit.error = getString(R.string.error_shortnameinput)
                inputvalflag = false
            }

            if (inputvalflag) {
                userSession.shortname = binding.nameinputedit.text.toString()

                // Hide Layout for Onboarding Name and show Onboarding Event
                binding.nameonboaarding.visibility = ConstraintLayout.INVISIBLE
                binding.submituser.visibility = Button.INVISIBLE

                binding.eventonboaarding.visibility = ConstraintLayout.VISIBLE
                binding.submitevent.visibility = Button.VISIBLE

                binding.etname.setOnClickListener {
                    binding.etname.error = null
                }

                binding.etname.onFocusChangeListener = object : OnFocusChangeListener {
                    override fun onFocusChange(v: View?, hasFocus: Boolean) {
                        hideSoftKeyboard()
                    }
                }

                binding.etPlannedDate.setOnClickListener {
                    binding.etPlannedDate.error = null
                    showDatePickerDialog()
                }

                binding.etPlannedDate.onFocusChangeListener = object : OnFocusChangeListener {
                    override fun onFocusChange(v: View?, hasFocus: Boolean) {
                        hideSoftKeyboard()
                    }
                }

                binding.etPlannedTime.setOnClickListener {
                    binding.etPlannedTime.error = null
                    showTimePickerDialog()
                }

                binding.etPlannedTime.onFocusChangeListener = object : OnFocusChangeListener {
                    override fun onFocusChange(v: View?, hasFocus: Boolean) {
                        hideSoftKeyboard()
                    }
                }

//                etlocation.setOnClickListener {
//                    val locationmap = Intent(this, MapsActivity::class.java)
//                    startActivityForResult(locationmap, autocompletePlaceCode)


                autocompleteFragment.setOnPlaceSelectedListener(object :
                    PlaceSelectionListener {
                    override fun onPlaceSelected(p0: Place) {
                        eventplaceid = p0.id
                        eventlatitude = p0.latLng!!.latitude
                        eventlongitude = p0.latLng!!.longitude
                        eventaddress = p0.address
                        eventlocationname = p0.name
                        //etlocation.setText(eventlocationname)

//                            val resultIntent = Intent()
                        //resultIntent.putExtra("eventid", eventkey)
                        //resultIntent.putExtra("place_name", p0.name)
                        //resultIntent.putExtra("place_id", p0.id)
                        //resultIntent.putExtra("place_latitude", p0.latLng!!.latitude)
                        //resultIntent.putExtra("place_longitude", p0.latLng!!.longitude)
                        //resultIntent.putExtra("place_address", p0.address)
//                            resultIntent.putExtra("place_phone", p0.phoneNumber)
//                            resultIntent.putExtra("place_rating", p0.rating)
//                            resultIntent.putExtra("place_userrating", p0.userRatingsTotal)
//                            setResult(Activity.RESULT_OK, resultIntent)
//                            finish()
                    }

                    override fun onError(p0: Status) {
                        // Here it's where I Should be implementing the case when the provider has not been found
                        //Toast.makeText(applicationContext, "" + p0.toString(), Toast.LENGTH_LONG).show();
                    }
                })
//                }

//                etlocation.setOnFocusChangeListener(object : View.OnFocusChangeListener {
//                    override fun onFocusChange(v: View?, hasFocus: Boolean) {
//                        hideSoftKeyboard()
//                    }
//                })

                binding.submitevent.setOnClickListener {
                    var inputvalflag: Boolean
                    inputvalflag = true
                    if (binding.etname.text.toString().isEmpty()) {
                        binding.etname.error = getString(R.string.error_eventnameinput)
                        inputvalflag = false
                    }
                    if (binding.etPlannedDate.text.toString().isEmpty()) {
                        binding.etPlannedDate.error = getString(R.string.error_eventdateinput)
                        inputvalflag = false
                    }
                    if (binding.etPlannedTime.text.toString().isEmpty()) {
                        binding.etPlannedTime.error = getString(R.string.error_eventtimeinput)
                        inputvalflag = false
                    }
//                    if (etlocation.text.toString().isEmpty()) {
//                        etlocation.error = getString(R.string.error_eventlocationinput)
//                        inputvalflag = false
//                    }
                    if (inputvalflag) {
                        val event = Event().apply {
                            placeid = eventplaceid.toString()
                            latitude = eventlatitude
                            longitude = eventlongitude
                            address = eventaddress.toString()
                            name = binding.etname.text.toString()
                            date = binding.etPlannedDate.text.toString()
                            time = binding.etPlannedTime.text.toString()
                            location = eventlocationname.toString()
                        }

                        userSession.role = when (binding.spinner.selectedItemPosition) {
                            0 -> "Bride"
                            1 -> "Groom"
                            else -> "Bride"
                        }

//                        userSession.country = when (binding.spinner2.selectedItemPosition) {
//                            0 -> "MX"
//                            1 -> "CL"
//                            2 -> "PE"
//                            else -> "MX"
//                        }

                        if (!checkPermissions()) {
                            alertBox()
                        } else {
                            lifecycleScope.launch {
                                //addUser - Remote & Local

                                //2/26/2022 -- We need to merge the functionality to create the event within add user
                                //as the whole flow for creating the user works but I cannot say the same for the event
                                //this will make a pretty long chain of responsibility but otherwise this won't be working
                                //addUser(this@OnboardingView, userSession)
                                //addEvent - Remote & Local
                                //addEvent(this@OnboardingView, event)
                                if (!getautocreateTaskPayment()) {
                                    onBoarding(this@OnboardingView, userSession, event)
                                } else {
                                    val taskitem = Task(dummy = true)
                                    val paymentitem = Payment(dummy = true)
                                    onBoarding(this@OnboardingView, userSession, event)
                                    addTask(applicationContext, taskitem)
                                    addPayment(applicationContext, paymentitem)
                                }
                                delay(1000)
                                finish()
                            }
                        }
                        val resultIntent = Intent()
                        setResult(Activity.RESULT_OK, resultIntent)
                    }
                }
            }
        }
    }

//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//
//        super.onActivityResult(requestCode, resultCode, data)
//        if (resultCode == Activity.RESULT_OK && requestCode == autocompletePlaceCode) {
//            val placenameString = data?.getStringExtra("place_name")
//            eventplaceid = data!!.getStringExtra("place_id").toString()
//            eventlatitude = data.getDoubleExtra("place_latitude", 0.0)
//            eventlongitude = data.getDoubleExtra("place_longitude", 0.0)
//            eventaddress = data.getStringExtra("place_address").toString()
//            etlocation.setText(placenameString)
//        }
//    }

    private fun alertBox() {
        val builder = android.app.AlertDialog.Builder(this)
        builder.setTitle(getString(R.string.lackpermissions_message))
        builder.setMessage(getString(R.string.lackpermissions_message))

        builder.setPositiveButton(
            getString(R.string.accept)
        ) { _, _ ->
            requestPermissions()
        }
        builder.setNegativeButton(
            "Cancel"
        ) { p0, _ -> p0!!.dismiss() }

        val dialog = builder.create()
        dialog.show()
    }

    private fun checkPermissions(): Boolean {
        return !((checkSelfPermission(Manifest.permission.READ_CALENDAR) ==
                PackageManager.PERMISSION_DENIED
                ) && (checkSelfPermission(Manifest.permission.WRITE_CALENDAR) ==
                PackageManager.PERMISSION_DENIED
                ) && (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) ==
                PackageManager.PERMISSION_DENIED
                ) && (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                PackageManager.PERMISSION_DENIED
                ))
    }

    private fun requestPermissions() {
        val permissions =
            arrayOf(
                Manifest.permission.READ_CALENDAR,
                Manifest.permission.WRITE_CALENDAR,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        //show popup to request runtime permission
        requestPermissions(permissions, TaskCreateEdit.PERMISSION_CODE)
    }

    private fun showDatePickerDialog() {
        val newFragment =
            DatePickerFragment.newInstance(DatePickerDialog.OnDateSetListener { _, year, month, day ->
                // +1 because January is zero
                val selectedDate = day.toString() + "/" + (month + 1) + "/" + year
                binding.etPlannedDate.setText(selectedDate)
            })
        newFragment.show(supportFragmentManager, "datePicker")
    }

    private fun showTimePickerDialog() {
        val newFragment = TimePickerFragment(binding.etPlannedTime)
        newFragment.show(supportFragmentManager, "Time Picker")

    }

    fun hideSoftKeyboard() {
        if (currentFocus != null) {
            val inputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
        }
    }

    override fun onBackPressed() {
        if (back_pressed + TIME_DELAY > System.currentTimeMillis()) {
            userSession.logout(this)
            super.onBackPressed()
            finish()
        } else {
            Toast.makeText(
                baseContext, getString(R.string.pressexit),
                Toast.LENGTH_SHORT
            ).show()
        }
        back_pressed = System.currentTimeMillis()
    }
}
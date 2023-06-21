package com.example.newevent2

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.newevent2.ui.dialog.DatePickerFragment
import TimePickerFragment
import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.widget.Button
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.lifecycleScope
import com.example.newevent2.Functions.onBoarding
import com.example.newevent2.Model.Event
import com.example.newevent2.Model.User
import kotlinx.android.synthetic.main.onboarding_name.*
import kotlinx.coroutines.*
import android.content.Context
import android.telephony.TelephonyManager
import android.view.View

import android.view.View.OnFocusChangeListener
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.content.ContextCompat.getSystemService
import com.google.android.gms.common.api.Status
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.TypeFilter
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import com.google.android.material.textfield.TextInputEditText
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userSession = User()
        userSession.userid = intent.getStringExtra("userid").toString()
        userSession.email = intent.getStringExtra("email").toString()
        userSession.authtype = intent.getStringExtra("authtype").toString()
        userSession.language = this.resources.configuration.locales.get(0).language
        userSession.language = this.resources.configuration.locales.get(0).country

        setContentView(R.layout.onboarding_name)

        //---------- Places loading -------------
        if (!Places.isInitialized()) {
            Places.initialize(applicationContext, getString(R.string.google_maps_key), Locale.US)
        }

        // Initialize the AutocompleteSupportFragment.
        val autocompleteFragment =
            supportFragmentManager.findFragmentById(R.id.etlocation)
                    as AutocompleteSupportFragment

        val tm = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        autocompleteFragment.setCountry(tm.simCountryIso)
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
        eventonboaarding.visibility = ConstraintLayout.INVISIBLE
        submitevent.visibility = Button.INVISIBLE

        nameinputedit.setOnClickListener {
            nameinputedit.error = null
        }

        submituser.setOnClickListener {
            var inputvalflag = true
            if (nameinputedit.text.toString().isEmpty()) {
                nameinputedit.error = getString(R.string.error_shortnameinput)
                inputvalflag = false
            }

            if (inputvalflag) {
                userSession.shortname = nameinputedit.text.toString()

                // Hide Layout for Onboarding Name and show Onboarding Event
                nameonboaarding.visibility = ConstraintLayout.INVISIBLE
                submituser.visibility = Button.INVISIBLE

                eventonboaarding.visibility = ConstraintLayout.VISIBLE
                submitevent.visibility = Button.VISIBLE

                etname.setOnClickListener {
                    etname.error = null
                }

                etname.onFocusChangeListener = object : OnFocusChangeListener {
                    override fun onFocusChange(v: View?, hasFocus: Boolean) {
                        hideSoftKeyboard()
                    }
                }

                etPlannedDate.setOnClickListener {
                    etPlannedDate.error = null
                    showDatePickerDialog()
                }

                etPlannedDate.onFocusChangeListener = object : OnFocusChangeListener {
                    override fun onFocusChange(v: View?, hasFocus: Boolean) {
                        hideSoftKeyboard()
                    }
                }

                etPlannedTime.setOnClickListener {
                    etPlannedTime.error = null
                    showTimePickerDialog()
                }

                etPlannedTime.onFocusChangeListener = object : OnFocusChangeListener {
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

                submitevent.setOnClickListener {
                    var inputvalflag: Boolean
                    inputvalflag = true
                    if (etname.text.toString().isEmpty()) {
                        etname.error = getString(R.string.error_eventnameinput)
                        inputvalflag = false
                    }
                    if (etPlannedDate.text.toString().isEmpty()) {
                        etPlannedDate.error = getString(R.string.error_eventdateinput)
                        inputvalflag = false
                    }
                    if (etPlannedTime.text.toString().isEmpty()) {
                        etPlannedTime.error = getString(R.string.error_eventtimeinput)
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
                            name = etname.text.toString()
                            date = etPlannedDate.text.toString()
                            time = etPlannedTime.text.toString()
                            location = eventlocationname.toString()
                        }

                        userSession.role = when (spinner.selectedItemPosition) {
                            0 -> "Bride"
                            1 -> "Groom"
                            else -> "Bride"
                        }

                        if ((checkSelfPermission(Manifest.permission.READ_CALENDAR) ==
                                    PackageManager.PERMISSION_DENIED
                                    ) && (checkSelfPermission(Manifest.permission.WRITE_CALENDAR) ==
                                    PackageManager.PERMISSION_DENIED
                                    ) && (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) ==
                                    PackageManager.PERMISSION_DENIED
                                    ) && (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                                    PackageManager.PERMISSION_DENIED
                                    ) && (checkSelfPermission(Manifest.permission.MANAGE_EXTERNAL_STORAGE) ==
                                    PackageManager.PERMISSION_DENIED
                                    )
                        ) {
                            //permission denied
                            val permissions =
                                arrayOf(
                                    Manifest.permission.READ_CALENDAR,
                                    Manifest.permission.WRITE_CALENDAR,
                                    Manifest.permission.READ_EXTERNAL_STORAGE,
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                    Manifest.permission.MANAGE_EXTERNAL_STORAGE
                                )
                            //show popup to request runtime permission
                            requestPermissions(permissions, TaskCreateEdit.PERMISSION_CODE)
                        } else {
                            lifecycleScope.launch {
                                //addUser - Remote & Local

                                //2/26/2022 -- We need to merge the functionality to create the event within add user
                                //as the whole flow for creating the user works but I cannot say the same for the event
                                //this will make a pretty long chain of responsibility but otherwise this won't be working
                                //addUser(this@OnboardingView, userSession)
                                //addEvent - Remote & Local
                                //addEvent(this@OnboardingView, event)

                                onBoarding(this@OnboardingView, userSession, event)
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
        val newFragment = TimePickerFragment(etPlannedTime)
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
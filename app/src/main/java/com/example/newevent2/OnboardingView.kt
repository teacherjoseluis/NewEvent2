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
import com.example.newevent2.Functions.addEvent
import com.example.newevent2.Functions.addUser
import com.example.newevent2.Model.Event
import com.example.newevent2.Model.User
import kotlinx.android.synthetic.main.onboarding_name.*
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class OnboardingView() : AppCompatActivity() {

    private val autocompletePlaceCode = 1

    private var eventplaceid: String? = null
    private var eventlatitude = 0.0
    private var eventlongitude = 0.0
    private var eventaddress: String? = null

    private lateinit var userSession: User
    private val scope = CoroutineScope(Job() + Dispatchers.Main)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userSession = User()
        userSession.key = intent.getStringExtra("userid").toString()
        userSession.email = intent.getStringExtra("email").toString()
        userSession.authtype = intent.getStringExtra("authtype").toString()
        userSession.language = this.resources.configuration.locales.get(0).language
        userSession.language = this.resources.configuration.locales.get(0).country

        setContentView(R.layout.onboarding_name)

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

                etPlannedDate.setOnClickListener {
                    etPlannedDate.error = null
                    showDatePickerDialog()
                }

                etPlannedTime.setOnClickListener {
                    etPlannedTime.error = null
                    showTimePickerDialog()
                }

                etlocation.setOnClickListener {
                    val locationmap = Intent(this, MapsActivity::class.java)
                    startActivityForResult(locationmap, autocompletePlaceCode)
                }

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
                    if (etlocation.text.toString().isEmpty()) {
                        etlocation.error = getString(R.string.error_eventlocationinput)
                        inputvalflag = false
                    }
                    if (inputvalflag) {
                        val event = Event().apply {
                            placeid = eventplaceid.toString()
                            latitude = eventlatitude
                            longitude = eventlongitude
                            address = eventaddress.toString()
                            name = etname.text.toString()
                            date = etPlannedDate.text.toString()
                            time = etPlannedTime.text.toString()
                            location = etlocation.text.toString()
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
                                    )
                        ) {
                            //permission denied
                            val permissions =
                                arrayOf(
                                    Manifest.permission.READ_CALENDAR,
                                    Manifest.permission.WRITE_CALENDAR
                                )
                            //show popup to request runtime permission
                            requestPermissions(permissions, TaskCreateEdit.PERMISSION_CODE)
                        } else {
                            scope.launch {
                                //addUser - Remote & Local
                                addUser(this@OnboardingView, userSession)
                                //addEvent - Remote & Local
                                addEvent(this@OnboardingView, event)
                            }
                        }
                        val resultIntent = Intent()
                        setResult(Activity.RESULT_OK, resultIntent)
                    }
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == autocompletePlaceCode) {
            val placenameString = data?.getStringExtra("place_name")
            eventplaceid = data!!.getStringExtra("place_id").toString()
            eventlatitude = data.getDoubleExtra("place_latitude", 0.0)
            eventlongitude = data.getDoubleExtra("place_longitude", 0.0)
            eventaddress = data.getStringExtra("place_address").toString()
            etlocation.setText(placenameString)
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
        val newFragment = TimePickerFragment(etPlannedTime)
        newFragment.show(supportFragmentManager, "Time Picker")

    }
}
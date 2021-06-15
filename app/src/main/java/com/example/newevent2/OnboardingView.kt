package com.example.newevent2

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity
import com.example.newevent2.ui.dialog.DatePickerFragment
import kotlinx.android.synthetic.main.event_edit.*
import kotlinx.android.synthetic.main.event_edit.button
import kotlinx.android.synthetic.main.event_edit.etPlannedDate
import kotlinx.android.synthetic.main.event_edit.etPlannedTime
import kotlinx.android.synthetic.main.event_edit.etabout
import kotlinx.android.synthetic.main.event_edit.etlocation
import kotlinx.android.synthetic.main.event_edit.etname
import TimePickerFragment
import android.app.Activity
import android.content.Context
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.newevent2.MVP.OnboardingPresenter
import com.example.newevent2.Model.Event
import com.example.newevent2.Model.User
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.android.synthetic.main.onboarding.*
import kotlinx.android.synthetic.main.onboarding.buttonname
import kotlinx.android.synthetic.main.onboarding.nameinputedit
import kotlinx.android.synthetic.main.onboarding_name.*
import kotlinx.android.synthetic.main.welcome.*
import java.sql.Time
import java.text.SimpleDateFormat
import java.util.*

class OnboardingView() : AppCompatActivity(),
    OnboardingPresenter.ViewOnboardingActivity {

    private val autocompletePlaceCode = 1

    private var event_placeid: String? = null
    private var event_latitude = 0.0
    private var event_longitude = 0.0
    private var event_address: String? = null

    private lateinit var presenter: OnboardingPresenter
    private lateinit var userSession: User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userSession = User()
        userSession.key = intent.getStringExtra("userid").toString()
        userSession.email = intent.getStringExtra("email").toString()
        userSession.authtype = intent.getStringExtra("authtype").toString()

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
                    var inputvalflag = true
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
                            placeid = event_placeid.toString()
                            latitude = event_latitude
                            longitude = event_longitude
                            address = event_address.toString()
                            name = etname.text.toString()
                            date = etPlannedDate.text.toString()
                            time = etPlannedTime.text.toString()
                            location = etlocation.text.toString()
                        }
                        presenter = OnboardingPresenter(this, this, userSession, event)
                    }
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == autocompletePlaceCode) {
            val placenameString = data?.getStringExtra("place_name")
            event_placeid = data!!.getStringExtra("place_id").toString()
            event_latitude = data.getDoubleExtra("place_latitude", 0.0)
            event_longitude = data.getDoubleExtra("place_longitude", 0.0)
            event_address = data.getStringExtra("place_address").toString()
            etlocation.setText(placenameString)
        } else {
            //Toast.makeText(this, "Error in autocomplete location", Toast.LENGTH_SHORT).show()
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

    override fun onOnboardingSuccess() {
        Toast.makeText(
            this,
            getString(R.string.sucess_onboardingwelcome),
            Toast.LENGTH_SHORT
        ).show()
        finish()
    }

    override fun onOnboardingError(errorcode: String) {
        when(errorcode) {
            "USERERROR" -> Toast.makeText(
                this,
                getString(R.string.error_onboardingcreateaccount),
                Toast.LENGTH_SHORT
            ).show()
            "EVENTERROR" -> Toast.makeText(
                this,
                getString(R.string.error_onboardingcreateevent),
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}
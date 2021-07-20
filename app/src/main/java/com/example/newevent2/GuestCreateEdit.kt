package com.example.newevent2

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.telephony.PhoneNumberUtils
import android.telephony.TelephonyManager
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.newevent2.Functions.addGuest
import com.example.newevent2.Functions.addTask
import com.example.newevent2.Functions.editGuest
import com.example.newevent2.Functions.editTask
import com.example.newevent2.Model.Guest
import com.example.newevent2.Model.GuestModel
import com.example.newevent2.ui.TextValidate
import com.google.android.material.chip.Chip
import com.google.android.material.textfield.TextInputEditText
import com.theartofdev.edmodo.cropper.CropImage
import kotlinx.android.synthetic.main.new_guest.*
import kotlinx.android.synthetic.main.new_guest.button

class GuestCreateEdit : AppCompatActivity(), CoRAddEditGuest {

    private var uri: Uri? = null
    private var chiptextvalue: String? = null
    private var categoryrsvp: String = ""
    private var categorycompanions: String = ""

    private lateinit var guestitem: Guest
    private lateinit var loadingview: View
    private lateinit var withdataview: View
    lateinit var mContext: Context

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.new_guest)

        // Toolbar
        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar!!.setHomeAsUpIndicator(R.drawable.icons8_left_24)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        val apptitle = findViewById<TextView>(R.id.appbartitle)
        apptitle.text = "New Guest"

        loadingview = findViewById(R.id.loadingscreen)
        withdataview = findViewById(R.id.withdata)


        val extras = intent.extras
        guestitem = if (extras!!.containsKey("guest")) {
            intent.getParcelableExtra("guest")!!
        } else {
            Guest()
        }

        val mPhoneNumber = findViewById<TextInputEditText>(R.id.phoneinputedit)
        val tm = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager

        val guestid = guestitem.key

        if (guestid != "") {
            val guestmodel = GuestModel()
            val user = com.example.newevent2.Functions.getUserSession(applicationContext!!)
            guestmodel.getGuestdetail(
                user.key,
                user.eventid,
                guestid,
                object : GuestModel.FirebaseSuccessGuest {
                    override fun onGuest(guest: Guest) {
                        nameinputedit.setText(guest.name)
                        phoneinputedit.setText(guest.phone)
                        tableinputedit.setText(guest.table)

                        guestitem.key = guest.key
                        guestitem.name = guest.name
                        guestitem.phone = guest.phone
                        guestitem.email = guest.email
                        guestitem.rsvp = guest.rsvp
                        guestitem.companion = guest.companion
                        guestitem.table = guest.table
                    }
                })
        }

        nameinputedit.onFocusChangeListener = View.OnFocusChangeListener { _, p1 ->
            if (!p1) {
                val validationmessage = TextValidate(nameinputedit).namefieldValidate()
                if (validationmessage != "") {
                    nameinputedit.error = "Error in Task name: $validationmessage"
                }
            }
        }

        nameinputedit.setOnClickListener {
            nameinputedit.error = null
        }

        phoneinputedit.setOnClickListener {
            phoneinputedit.error = null
        }

        mPhoneNumber.setOnFocusChangeListener { p0, p1 ->
            PhoneNumberUtils.formatNumber(
                mPhoneNumber.text.toString(),
                tm.simCountryIso
            )
        }

        tableinputedit.setOnClickListener {
            tableinputedit.error = null
        }

        button.setOnClickListener {
            var inputvalflag = true
            if (nameinputedit.text.toString().isEmpty()) {
                nameinputedit.error = "Guest name is required!"
                inputvalflag = false
            }
            if (phoneinputedit.text.toString().isEmpty()) {
                phoneinputedit.error = "Guest phone is required!"
                inputvalflag = false
            }
            if (rsvpgroup.checkedChipId == -1) {
                Toast.makeText(this, "RSVP is required!", Toast.LENGTH_SHORT).show()
                inputvalflag = false
            }
            if (companionsgroup.checkedChipId == -1) {
                Toast.makeText(this, "Companion info is required!", Toast.LENGTH_SHORT).show()
                inputvalflag = false
            }
            if (tableinputedit.text.toString().isEmpty()) {
                Toast.makeText(this, "Table info is required!", Toast.LENGTH_SHORT).show()
                inputvalflag = false
            }
            if (inputvalflag) {
                saveguest()
            }
        }
    }

    private fun saveguest() {
        loadingview.visibility = ConstraintLayout.VISIBLE
        withdataview.visibility = ConstraintLayout.GONE

        var id = rsvpgroup.checkedChipId
        var chipselected = rsvpgroup.findViewById<Chip>(id)
        chiptextvalue = chipselected.text.toString()
        categoryrsvp = when (chiptextvalue) {
            "Yes" -> "y"
            "No" -> "n"
            "Pending" -> "pending"
            else -> "pending"
        }

        id = companionsgroup.checkedChipId
        chipselected = companionsgroup.findViewById<Chip>(id)
        chiptextvalue = chipselected.text.toString()
        categorycompanions = when (chiptextvalue) {
            "Adult" -> "adult"
            "Child" -> "child"
            "Baby" -> "baby"
            "None" -> "none"
            else -> "none"
        }

        guestitem.rsvp = categoryrsvp
        guestitem.companion = categorycompanions
        guestitem.table = tableinputedit.text.toString()
        guestitem.name = nameinputedit.text.toString()
        guestitem.phone = phoneinputedit.text.toString()
        guestitem.email = mailinputedit.text.toString()

        if (guestitem.key == "") {
            addGuest(this, guestitem, CALLER)
        } else if (guestitem.key != "") {
            editGuest(this, guestitem)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    companion object {
        //Permission code
        internal val PERMISSION_CODE = 1001
        const val CALLER = "guest"
    }

    override fun onAddEditGuest(guest: Guest) {
        (mContext as GuestCreateEdit).loadingview.visibility = ConstraintLayout.GONE
        (mContext as GuestCreateEdit).withdataview.visibility = ConstraintLayout.VISIBLE
        GuestsAll.guestcreated_flag = 1
    }
}


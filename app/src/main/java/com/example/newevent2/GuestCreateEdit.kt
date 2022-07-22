package com.example.newevent2

import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.telephony.PhoneNumberUtils
import android.telephony.TelephonyManager
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.lifecycleScope
import com.example.newevent2.Functions.addGuest
import com.example.newevent2.Functions.deleteGuest
import com.example.newevent2.Functions.editGuest
import com.example.newevent2.Model.Guest
import com.example.newevent2.ui.TextValidate
import com.google.android.material.chip.Chip
import com.google.android.material.textfield.TextInputEditText
import kotlinx.android.synthetic.main.new_guest.*
import kotlinx.coroutines.launch


class GuestCreateEdit : AppCompatActivity(), CoRAddEditGuest {

    private var chiptextvalue: String? = null
    private var categoryrsvp: String = ""
    private var categorycompanions: String = ""

    private lateinit var guestitem: Guest
    private lateinit var loadingview: View
    private lateinit var withdataview: View
    lateinit var mContext: Context
    private lateinit var activitymenu: Menu

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.new_guest)

        // Toolbar
        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar!!.setHomeAsUpIndicator(R.drawable.icons8_left_24)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        val apptitle = findViewById<TextView>(R.id.appbartitle)
        apptitle.text = getString(R.string.new_guest)

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

//        if (guestitem.key != "") {
//            val guestmodel = GuestModel()
//            val user = com.example.newevent2.Functions.getUserSession(applicationContext!!)
//            guestmodel.getGuestdetail(
//                user.key,
//                user.eventid,
//                guestid,
//                object : GuestModel.FirebaseSuccessGuest {
//                    override fun onGuest(guest: Guest) {
//                        nameinputedit.setText(guest.name)
//                        phoneinputedit.setText(guest.phone)
//                        tableinputedit.setText(guest.table)
//
//                        guestitem.key = guest.key
//                        guestitem.name = guest.name
//                        guestitem.phone = guest.phone
//                        guestitem.email = guest.email
//                        guestitem.rsvp = guest.rsvp
//                        guestitem.companion = guest.companion
//                        guestitem.table = guest.table
//                    }
//                })
//        }

        if (guestitem.key != "") {
            nameinputedit.setText(guestitem.name)
            phoneinputedit.setText(guestitem.phone)
            tableinputedit.setText(guestitem.table)
        }

        nameinputedit.onFocusChangeListener = View.OnFocusChangeListener { _, p1 ->
            if (!p1) {
                val validationmessage = TextValidate(nameinputedit).namefieldValidate()
                if (validationmessage != "") {
                    val errormsg = getString(R.string.error_guestname)
                    errormsg.plus(validationmessage)
                    nameinputedit.error = errormsg
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

        phoneimage.setOnClickListener {
            if (!phoneinputedit.text.isNullOrBlank()) {
                val intent = Intent(
                    Intent.ACTION_DIAL,
                    Uri.fromParts("tel", phoneinputedit.text.toString(), null)
                )
                startActivity(intent)
            }
        }

        mailimage.setOnClickListener {
            if (!mailinputedit.text.isNullOrBlank()) {
                try {
                    val intent = Intent(Intent.ACTION_SENDTO)
                    intent.data = Uri.parse("mailto:") // only email apps should handle this
                    intent.putExtra(Intent.EXTRA_EMAIL, mailinputedit.text.toString())
                    intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.genericmessage))
                    startActivity(intent)
                } catch (ex: ActivityNotFoundException) {
                    Toast.makeText(
                        this,
                        getString(R.string.error_noemailclient),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

        tableinputedit.setOnClickListener {
            tableinputedit.error = null
        }

        button.setOnClickListener {
            var inputvalflag = true
            if (nameinputedit.text.toString().isEmpty()) {
                nameinputedit.error = getString(R.string.error_tasknameinput)
                inputvalflag = false
            }
            if (phoneinputedit.text.toString().isEmpty()) {
                phoneinputedit.error = getString(R.string.error_vendorphoneinput)
                inputvalflag = false
            }
            if (rsvpgroup.checkedChipId == -1) {
                Toast.makeText(this, getString(R.string.error_rsvpinput), Toast.LENGTH_SHORT).show()
                inputvalflag = false
            }
            if (companionsgroup.checkedChipId == -1) {
                Toast.makeText(this, getString(R.string.error_companioninput), Toast.LENGTH_SHORT).show()
                inputvalflag = false
            }
            if (tableinputedit.text.toString().isEmpty()) {
                Toast.makeText(this, getString(R.string.error_tableinput), Toast.LENGTH_SHORT).show()
                inputvalflag = false
            }
            if (inputvalflag) {
                lifecycleScope.launch {
                    saveguest()
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        if (guestitem.key != "") {
            menuInflater.inflate(R.menu.guests_menu2, menu)
            activitymenu = menu
            val guestmenu = activitymenu.findItem(R.id.remove_guest)
            guestmenu.isEnabled = true
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.remove_guest -> {
                AlertDialog.Builder(this)
                    .setTitle(getString(R.string.delete_message))
                    .setMessage(getString(R.string.delete_entry)) // Specifying a listener allows you to take an action before dismissing the dialog.
                    // The dialog is automatically dismissed when a dialog button is clicked.
                    .setPositiveButton(
                        android.R.string.yes
                    ) { dialog, which ->
                        deleteGuest(this, guestitem)
                        //finish()
                    } // A null listener allows the button to dismiss the dialog and take no further action.
                    .setNegativeButton(android.R.string.no, null)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show()
                super.onOptionsItemSelected(item);
            }
            else -> {
                super.onOptionsItemSelected(item);
            }
        }
    }

    private suspend fun saveguest() {
        loadingview.visibility = ConstraintLayout.VISIBLE
        withdataview.visibility = ConstraintLayout.GONE

        var id = rsvpgroup.checkedChipId
        var chipselected = rsvpgroup.findViewById<Chip>(id)
        chiptextvalue = chipselected.text.toString()
        categoryrsvp = when (chiptextvalue) {
            getString(R.string.yes) -> "y"
            getString(R.string.no) -> "n"
            getString(R.string.pending) -> "pending"
            else -> "pending"
        }

        id = companionsgroup.checkedChipId
        chipselected = companionsgroup.findViewById(id)
        chiptextvalue = chipselected.text.toString()
        categorycompanions = when (chiptextvalue) {
            getString(R.string.adult) -> "adult"
            getString(R.string.child) -> "child"
            getString(R.string.baby) -> "baby"
            getString(R.string.none) -> "none"
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

    override fun finish() {
        val returnintent = Intent()
        setResult(RESULT_OK, returnintent)
        super.finish()
    }

    companion object {
        //Permission code
        internal const val PERMISSION_CODE = 1001
        const val CALLER = "guest"
    }

    override suspend fun onAddEditGuest(guest: Guest) {
        (mContext as GuestCreateEdit).loadingview.visibility = ConstraintLayout.GONE
        (mContext as GuestCreateEdit).withdataview.visibility = ConstraintLayout.VISIBLE
        GuestsAll.guestcreated_flag = 1
    }
}


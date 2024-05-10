package com.bridesandgrooms.event

import Application.AnalyticsManager
import Application.GuestCreationException
import Application.GuestDeletionException
import Application.TaskCreationException
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.telephony.PhoneNumberFormattingTextWatcher
import android.telephony.PhoneNumberUtils
import android.telephony.TelephonyManager
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.bridesandgrooms.event.Functions.addGuest
import com.bridesandgrooms.event.Functions.deleteGuest
import com.bridesandgrooms.event.Functions.editGuest
import com.bridesandgrooms.event.Model.Guest
import com.bridesandgrooms.event.Model.User
import com.bridesandgrooms.event.databinding.NewGuestBinding
import com.bridesandgrooms.event.UI.FieldValidators.TextValidate
import com.google.android.material.chip.Chip
import com.google.android.material.textfield.TextInputEditText
//import kotlinx.android.synthetic.main.new_guest.*
//import kotlinx.android.synthetic.main.task_editdetail.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class GuestCreateEdit : AppCompatActivity() {

    private var chiptextvalue: String? = null
    private var categoryrsvp: String = ""
    private var categorycompanions: String = ""
    private var categoryguests: String = ""

    private lateinit var guestItem: Guest
    private lateinit var activitymenu: Menu
    private lateinit var binding: NewGuestBinding

    private lateinit var userSession: User

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.new_guest)
        userSession = User().getUser(this)
        //loadingview = findViewById(R.id.loadingscreen)
        //withdataview = findViewById(R.id.withdata)

        val mPhoneNumber = findViewById<TextInputEditText>(R.id.phoneinputedit)
        val tm = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        val apptitle = findViewById<TextView>(R.id.appbartitle)
        val guestMaxNumber = userSession.numberguests
        val extras = intent.extras

        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar!!.setHomeAsUpIndicator(R.drawable.icons8_left_24)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        if (extras!!.containsKey("guest")) {
            apptitle.text = getString(R.string.edit_guest)
            guestItem = intent.getParcelableExtra("guest")!!
        } else {
            apptitle.text = getString(R.string.new_guest)
            guestItem = Guest()
        }

        if (guestItem.name.isNotEmpty()) {
            binding.nameinputedit.setText(guestItem.name)
            binding.phoneinputedit.setText(guestItem.phone)
            //tableinputedit.setText(guestitem.table)
            when (guestItem.companion) {
                "adult" -> binding.companionsgroup.check(binding.chipadult.id)
                "child" -> binding.companionsgroup.check(binding.chipchild.id)
                "baby" -> binding.companionsgroup.check(binding.chipbaby.id)
                else -> binding.companionsgroup.check(binding.chipnone.id)
            }
            when (guestItem.table) {
                "family" -> binding.guestgroup.check(binding.chipfamily.id)
                "extendedfamily" -> binding.guestgroup.check(binding.chipextended.id)
                "familyfriends" -> binding.guestgroup.check(binding.chipfamfriends.id)
                "oldfriends" -> binding.guestgroup.check(binding.chipoldfriends.id)
                "coworkers" -> binding.guestgroup.check(binding.coworkers.id)
                "others" -> binding.guestgroup.check(binding.others.id)
                else -> binding.guestgroup.check(binding.others.id)
            }
            when (guestItem.rsvp) {
                "y" -> binding.rsvpgroup.check(binding.chip1.id)
                "n" -> binding.rsvpgroup.check(binding.chip2.id)
                "pending" -> binding.rsvpgroup.check(binding.chip3.id)
                else -> binding.rsvpgroup.check(binding.chip1.id)
            }
        }

//        binding.nameinputedit.onFocusChangeListener = View.OnFocusChangeListener { _, p1 ->
//            if (!p1) {
//                val validationmessage = TextValidate(binding.nameinputedit).nameFieldValidate()
//                if (validationmessage != "") {
//                    val errormsg = getString(R.string.error_guestname)
//                    errormsg.plus(validationmessage)
//                    binding.nameinputedit.error = errormsg
//                }
//            }
//        }

        binding.nameinputedit.setOnClickListener {
            binding.nameinputedit.error = null
        }

        binding.phoneinputedit.setOnClickListener {
            binding.phoneinputedit.error = null
        }

        mPhoneNumber.addTextChangedListener(PhoneNumberFormattingTextWatcher())
        mPhoneNumber.setOnFocusChangeListener { p0, p1 ->
            PhoneNumberUtils.formatNumber(
                mPhoneNumber.text.toString(),
                tm.simCountryIso
            )
        }

        binding.phoneimage.setOnClickListener {
            AnalyticsManager.getInstance().trackUserInteraction(SCREEN_NAME, "Phone_Call")
            if (!binding.phoneinputedit.text.isNullOrBlank()) {
                val intent = Intent(
                    Intent.ACTION_DIAL,
                    Uri.fromParts("tel", binding.phoneinputedit.text.toString(), null)
                )
                startActivity(intent)
            }
        }

        binding.mailimage.setOnClickListener {
            AnalyticsManager.getInstance().trackUserInteraction(SCREEN_NAME, "Send_Mail")
            if (!binding.mailinputedit.text.isNullOrBlank()) {
                try {
                    val intent = Intent(Intent.ACTION_SENDTO)
                    intent.data = Uri.parse("mailto:") // only email apps should handle this
                    intent.putExtra(Intent.EXTRA_EMAIL, binding.mailinputedit.text.toString())
                    intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.genericmessage))
                    startActivity(intent)
                } catch (e: ActivityNotFoundException) {
                    displayToastMsg(getString(R.string.error_noemailclient) + e.toString())
                    AnalyticsManager.getInstance().trackError(
                        SCREEN_NAME,
                        e.message.toString(),
                        "Send Mail",
                        e.stackTraceToString()
                    )
                    Log.e(TAG, e.message.toString())
                }
            }
        }

        binding.button.setOnClickListener {
            AnalyticsManager.getInstance().trackUserInteraction(SCREEN_NAME, "Save_Guest")
            var inputvalflag = true
            if (binding.nameinputedit.text.toString().isEmpty()) {
                binding.nameinputedit.error = getString(R.string.error_tasknameinput)
                inputvalflag = false
            }
            if (binding.phoneinputedit.text.toString().isEmpty()) {
                binding.phoneinputedit.error = getString(R.string.error_vendorphoneinput)
                inputvalflag = false
            }
            if (binding.rsvpgroup.checkedChipId == -1) {
                Toast.makeText(this, getString(R.string.error_rsvpinput), Toast.LENGTH_SHORT).show()
                inputvalflag = false
            }
            if (binding.companionsgroup.checkedChipId == -1) {
                Toast.makeText(this, getString(R.string.error_companioninput), Toast.LENGTH_SHORT)
                    .show()
                inputvalflag = false
            }
            if (binding.guestgroup.checkedChipId == -1) {
                Toast.makeText(
                    this,
                    getString(R.string.guest_category_is_required), Toast.LENGTH_SHORT
                )
                    .show()
                inputvalflag = false
            }

            if (inputvalflag) {
                val guestEvent = Guest()
                val guestCount = guestEvent.getGuestCount(this)!!
                val newGuestBalance = guestMaxNumber - (guestCount + 1)
                if (newGuestBalance > 0) {
                    showBanner(getString(R.string.banner_withinguestcount), false)
                    saveguest()
                } else {
                    showBanner(getString(R.string.banner_outguestcount), true)
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        if (guestItem.key.isNotEmpty()) {
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
                    .setMessage(getString(R.string.delete_entry))
                    .setPositiveButton(
                        android.R.string.yes
                    ) { dialog, which ->
                        AnalyticsManager.getInstance()
                            .trackUserInteraction(SCREEN_NAME, "Delete_Guest")
                        try {
                            deleteGuest(this@GuestCreateEdit, userSession, guestItem)
                            disableControls()
                        } catch (e: GuestDeletionException) {
                            displayToastMsg(getString(R.string.errorGuestDeletion) + e.toString())
                            AnalyticsManager.getInstance().trackError(
                                SCREEN_NAME,
                                e.message.toString(),
                                "deleteGuest()",
                                e.stackTraceToString()
                            )
                            Log.e(TAG, e.message.toString())
                        }
                        finish()

                    } // A null listener allows the button to dismiss the dialog and take no further action.
                    .setNegativeButton(android.R.string.no, null)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show()
                true
            }

            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }

    private fun disableControls() {
        binding.nameinputedit.isEnabled = false
        binding.phoneinputedit.isEnabled = false
        binding.mailinputedit.isEnabled = false
        binding.rsvpgroup.isEnabled = false
        binding.companionsgroup.isEnabled = false
        binding.button.isEnabled = false

        setResult(Activity.RESULT_OK, Intent())
        //delay(2000) // Replace Thread.sleep with delay from coroutines
        finish()
    }

    @SuppressLint("SuspiciousIndentation")
    private fun saveguest() {
        binding.nameinputedit.isEnabled = false
        binding.phoneinputedit.isEnabled = false
        binding.mailinputedit.isEnabled = false
        binding.rsvpgroup.isEnabled = false
        binding.companionsgroup.isEnabled = false
        binding.button.isEnabled = false

        var id = binding.rsvpgroup.checkedChipId
        var chipselected = binding.rsvpgroup.findViewById<Chip>(id)
        chiptextvalue = chipselected.text.toString()
        categoryrsvp = when (chiptextvalue) {
            getString(R.string.yes) -> "y"
            getString(R.string.no) -> "n"
            getString(R.string.pending) -> "pending"
            else -> "pending"
        }

        id = binding.companionsgroup.checkedChipId
        chipselected = binding.companionsgroup.findViewById(id)
        chiptextvalue = chipselected.text.toString()
        categorycompanions = when (chiptextvalue) {
            getString(R.string.adult) -> "adult"
            getString(R.string.child) -> "child"
            getString(R.string.baby) -> "baby"
            getString(R.string.none) -> "none"
            else -> "none"
        }

        id = binding.guestgroup.checkedChipId
        chipselected = binding.guestgroup.findViewById(id)
        chiptextvalue = chipselected.text.toString()
        categoryguests = when (chiptextvalue) {
            getString(R.string.family) -> "family"
            getString(R.string.extendedfamily) -> "extendedfamily"
            getString(R.string.familyfriends) -> "familyfriends"
            getString(R.string.oldfriends) -> "oldfriends"
            getString(R.string.coworkers) -> "coworkers"
            getString(R.string.others) -> "others"
            else -> "others"
        }

        if (categoryrsvp == "n") {
            categoryguests = "notattending"
        }

        guestItem.rsvp = categoryrsvp
        guestItem.companion = categorycompanions
        guestItem.table = categoryguests
        guestItem.name = binding.nameinputedit.text.toString()
        guestItem.phone = binding.phoneinputedit.text.toString()
        guestItem.email = binding.mailinputedit.text.toString()

        if (guestItem.key.isEmpty()) {
            lifecycleScope.launch {
                try {
                    addGuest(applicationContext, userSession, guestItem)
                } catch (e: GuestCreationException) {
                    AnalyticsManager.getInstance().trackError(
                        SCREEN_NAME,
                        e.message.toString(),
                        "addGuest()",
                        e.stackTraceToString()
                    )
                    withContext(Dispatchers.Main) {
                        displayToastMsg(getString(R.string.errorGuestCreation) + e.toString())
                    }
                    Log.e(TaskCreateEdit.TAG, e.message.toString())
                }
            }

        } else {
            //lifecycleScope.launch {
            try {
                editGuest(applicationContext, userSession, guestItem)
            } catch (e: TaskCreationException) {
                AnalyticsManager.getInstance().trackError(
                    SCREEN_NAME,
                    e.message.toString(),
                    "editGuest()",
                    e.stackTraceToString()
                )
                //withContext(Dispatchers.Main) {
                displayToastMsg(getString(R.string.errorTaskCreation) + e.toString())
                //}
                Log.e(TaskCreateEdit.TAG, e.message.toString())
            }
        }
        finish()
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    private fun showBanner(message: String, dismiss: Boolean) {
        //binding.taskname.visibility = View.INVISIBLE
        val fadeInAnimation = AnimationUtils.loadAnimation(applicationContext, R.anim.fade_in)
        binding.bannerCardView.startAnimation(fadeInAnimation)

        binding.bannerCardView.visibility = View.VISIBLE
        binding.bannerText.text = message
        //getString(R.string.number_guests)
        if (dismiss) {
            binding.dismissButton.visibility = View.VISIBLE
            binding.dismissButton.setOnClickListener {
                binding.bannerCardView.visibility = View.INVISIBLE
            }
        }
    }

    private fun displayToastMsg(message: String) {
        Toast.makeText(
            this@GuestCreateEdit,
            message,
            Toast.LENGTH_LONG
        ).show()
    }

    override fun finish() {
        val returnintent = Intent()
        setResult(RESULT_OK, returnintent)
        super.finish()
    }

    companion object {
        //Permission code
        //internal const val PERMISSION_CODE = 1001
        const val SCREEN_NAME = "Guest_CreateEdit"
        const val TAG = "GuestCreateEdit"
        const val CALLER = "guest"
    }

//    override fun onAddEditGuest(guest: Guest) {
//        this.loadingview = findViewById(R.id.loadingscreen)
//        this.withdataview = findViewById(R.id.withdata)
//
//        (mContext as GuestCreateEdit).loadingview.visibility = ConstraintLayout.GONE
//        (mContext as GuestCreateEdit).withdataview.visibility = ConstraintLayout.VISIBLE
//        GuestsAll.guestcreated_flag = 1
//    }
}


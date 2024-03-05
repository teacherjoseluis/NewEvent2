package com.bridesandgrooms.event

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
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.bridesandgrooms.event.Functions.CoRAddEditGuest
import com.bridesandgrooms.event.Functions.PermissionUtils
import com.bridesandgrooms.event.Functions.addGuest
import com.bridesandgrooms.event.Functions.deleteGuest
import com.bridesandgrooms.event.Functions.editGuest
import com.bridesandgrooms.event.Functions.userdbhelper
import com.bridesandgrooms.event.Model.Guest
import com.bridesandgrooms.event.Model.User
import com.bridesandgrooms.event.Model.UserDBHelper
import com.bridesandgrooms.event.databinding.NewGuestBinding
import com.bridesandgrooms.event.UI.TextValidate
import com.google.android.material.chip.Chip
import com.google.android.material.textfield.TextInputEditText
//import kotlinx.android.synthetic.main.new_guest.*
//import kotlinx.android.synthetic.main.task_editdetail.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class GuestCreateEdit : AppCompatActivity(), CoRAddEditGuest {

    private var chiptextvalue: String? = null
    private var categoryrsvp: String = ""
    private var categorycompanions: String = ""
    private var categoryguests: String = ""

    private lateinit var guestitem: Guest
    private lateinit var loadingview: View
    private lateinit var withdataview: View
    lateinit var mContext: Context
    private lateinit var activitymenu: Menu
    private lateinit var binding: NewGuestBinding

    private lateinit var usersession: User

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userdbhelper = UserDBHelper(this)
        usersession = userdbhelper.getUser(userdbhelper.getUserKey())!!
        val guestMaxNumber = usersession.numberguests

        binding = DataBindingUtil.setContentView(this, R.layout.new_guest)

        // Toolbar
        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar!!.setHomeAsUpIndicator(R.drawable.icons8_left_24)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        val extras = intent.extras
        val apptitle = findViewById<TextView>(R.id.appbartitle)

        if (extras!!.containsKey("guest")) {
            apptitle.text = getString(R.string.edit_guest)
        } else {
            apptitle.text = getString(R.string.new_guest)
        }

        loadingview = findViewById(R.id.loadingscreen)
        withdataview = findViewById(R.id.withdata)

        guestitem = if (extras!!.containsKey("guest")) {
            intent.getParcelableExtra("guest")!!
        } else {
            Guest()
        }

        val mPhoneNumber = findViewById<TextInputEditText>(R.id.phoneinputedit)
        mPhoneNumber.addTextChangedListener(PhoneNumberFormattingTextWatcher())
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
//                        binding.nameinputedit.setText(guest.name)
//                        binding.phoneinputedit.setText(guest.phone)
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

        if (guestitem.name != "") {
            binding.nameinputedit.setText(guestitem.name)
            binding.phoneinputedit.setText(guestitem.phone)
            //tableinputedit.setText(guestitem.table)
            when (guestitem.companion) {
                "adult" -> binding.companionsgroup.check(binding.chipadult.id)
                "child" -> binding.companionsgroup.check(binding.chipchild.id)
                "baby" -> binding.companionsgroup.check(binding.chipbaby.id)
                else -> binding.companionsgroup.check(binding.chipnone.id)
            }
            when (guestitem.table) {
                "family" -> binding.guestgroup.check(binding.chipfamily.id)
                "extendedfamily" -> binding.guestgroup.check(binding.chipextended.id)
                "familyfriends" -> binding.guestgroup.check(binding.chipfamfriends.id)
                "oldfriends" -> binding.guestgroup.check(binding.chipoldfriends.id)
                "coworkers" -> binding.guestgroup.check(binding.coworkers.id)
                "others" -> binding.guestgroup.check(binding.others.id)
                else -> binding.guestgroup.check(binding.others.id)
            }
            when (guestitem.rsvp) {
                "y" -> binding.rsvpgroup.check(binding.chip1.id)
                "n" -> binding.rsvpgroup.check(binding.chip2.id)
                "pending" -> binding.rsvpgroup.check(binding.chip3.id)
                else -> binding.rsvpgroup.check(binding.chip1.id)
            }
        }

        binding.nameinputedit.onFocusChangeListener = View.OnFocusChangeListener { _, p1 ->
            if (!p1) {
                val validationmessage = TextValidate(binding.nameinputedit).namefieldValidate()
                if (validationmessage != "") {
                    val errormsg = getString(R.string.error_guestname)
                    errormsg.plus(validationmessage)
                    binding.nameinputedit.error = errormsg
                }
            }
        }

        binding.nameinputedit.setOnClickListener {
            binding.nameinputedit.error = null
        }

        binding.phoneinputedit.setOnClickListener {
            binding.phoneinputedit.error = null
        }

        mPhoneNumber.setOnFocusChangeListener { p0, p1 ->
            PhoneNumberUtils.formatNumber(
                mPhoneNumber.text.toString(),
                tm.simCountryIso
            )
        }

        binding.phoneimage.setOnClickListener {
            if (!binding.phoneinputedit.text.isNullOrBlank()) {
                val intent = Intent(
                    Intent.ACTION_DIAL,
                    Uri.fromParts("tel", binding.phoneinputedit.text.toString(), null)
                )
                startActivity(intent)
            }
        }

        binding.mailimage.setOnClickListener {
            if (!binding.mailinputedit.text.isNullOrBlank()) {
                try {
                    val intent = Intent(Intent.ACTION_SENDTO)
                    intent.data = Uri.parse("mailto:") // only email apps should handle this
                    intent.putExtra(Intent.EXTRA_EMAIL, binding.mailinputedit.text.toString())
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

//        tableinputedit.setOnClickListener {
//            tableinputedit.error = null
//        }

//        mtaggroup.setOnTagClickListener { TagGroup.OnTagClickListener {
//            Toast.makeText(applicationContext, it.toString(), Toast.LENGTH_LONG).show()
//        }
//        }

        binding.button.setOnClickListener {
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
                Toast.makeText(this, "Guest category is required", Toast.LENGTH_SHORT)
                    .show()
                inputvalflag = false
            }
//            if (tableinputedit.text.toString().isEmpty()) {
//                Toast.makeText(this, getString(R.string.error_tableinput), Toast.LENGTH_SHORT)
//                    .show()
//                inputvalflag = false
//            }
            if (inputvalflag) {
                val guestEvent = Guest()
                val guestCount = guestEvent.getGuestCount(applicationContext)!!
                val newGuestBalance = guestMaxNumber - (guestCount + 1)
                if (newGuestBalance > 0) {
                    showBanner(getString(R.string.banner_withinguestcount), false)
                    lifecycleScope.launch {
                        saveguest()
                    }
                } else {
                    showBanner(getString(R.string.banner_outguestcount), true)
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
//                        if (!PermissionUtils.checkPermissions(applicationContext)) {
//                            PermissionUtils.alertBox(this)
//                        } else {
                            lifecycleScope.launch {
                                deleteGuest(this@GuestCreateEdit, guestitem)
                                disableControls()
                                withContext(Dispatchers.IO) {
                                    Thread.sleep(1500)
                                }
                                finish()
                            }
                            //finish()
                       //}
                    } // A null listener allows the button to dismiss the dialog and take no further action.
                    .setNegativeButton(android.R.string.no, null)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show()
                //Disable all controls in the view
//                binding.nameinputedit.isEnabled = false
//                binding.phoneinputedit.isEnabled = false
//                binding.mailinputedit.isEnabled = false
//                binding.rsvpgroup.isEnabled = false
//                binding.companionsgroup.isEnabled = false
//                binding.button.isEnabled = false
//                super.onOptionsItemSelected(item)
                true

            }

            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }

    private suspend fun disableControls() {
        binding.nameinputedit.isEnabled = false
        binding.phoneinputedit.isEnabled = false
        binding.mailinputedit.isEnabled = false
        binding.rsvpgroup.isEnabled = false
        binding.companionsgroup.isEnabled = false
        binding.button.isEnabled = false

        setResult(Activity.RESULT_OK, Intent())
        delay(2000) // Replace Thread.sleep with delay from coroutines
        finish()
    }

    @SuppressLint("SuspiciousIndentation")
    private suspend fun saveguest() {
        //loadingview.visibility = ConstraintLayout.VISIBLE
        //withdataview.visibility = ConstraintLayout.GONE

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

        guestitem.rsvp = categoryrsvp
        guestitem.companion = categorycompanions
        guestitem.table = categoryguests
        guestitem.name = binding.nameinputedit.text.toString()
        guestitem.phone = binding.phoneinputedit.text.toString()
        guestitem.email = binding.mailinputedit.text.toString()

//        if (!PermissionUtils.checkPermissions(applicationContext)) {
//            PermissionUtils.alertBox(this)
//        } else {
            if (guestitem.key == "") {
                addGuest(this, guestitem, CALLER)
            } else if (guestitem.key != "") {
                editGuest(this, guestitem)
            }
            //withContext(Dispatchers.IO) {
            Thread.sleep(2000)
            //}
            finish()
        //}
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
            binding.dismissButton.setOnClickListener { binding.bannerCardView.visibility = View.INVISIBLE }
        }
    }

    override fun finish() {
        val returnintent = Intent()
        setResult(RESULT_OK, returnintent)
        super.finish()
    }

    companion object {
        //Permission code
        //internal const val PERMISSION_CODE = 1001
        const val CALLER = "guest"
    }

    override fun onAddEditGuest(guest: Guest) {
        this.loadingview = findViewById(R.id.loadingscreen)
        this.withdataview = findViewById(R.id.withdata)

        (mContext as GuestCreateEdit).loadingview.visibility = ConstraintLayout.GONE
        (mContext as GuestCreateEdit).withdataview.visibility = ConstraintLayout.VISIBLE
        GuestsAll.guestcreated_flag = 1
    }
}


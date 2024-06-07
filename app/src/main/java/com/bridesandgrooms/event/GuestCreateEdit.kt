package com.bridesandgrooms.event

import Application.AnalyticsManager
import Application.GuestCreationException
import Application.GuestDeletionException
import Application.TaskCreationException
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.telephony.PhoneNumberFormattingTextWatcher
import android.telephony.PhoneNumberUtils
import android.telephony.TelephonyManager
import android.text.Editable
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.TextView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.bridesandgrooms.event.Functions.addGuest
import com.bridesandgrooms.event.Functions.deleteGuest
import com.bridesandgrooms.event.Functions.editGuest
import com.bridesandgrooms.event.Model.Guest
import com.bridesandgrooms.event.Model.User
import com.bridesandgrooms.event.UI.FieldValidators.InputValidator
import com.bridesandgrooms.event.databinding.NewGuestBinding
import com.bridesandgrooms.event.UI.Fragments.VendorCreateEdit
import com.bridesandgrooms.event.UI.Fragments.VendorsAll
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.chip.Chip
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
//import kotlinx.android.synthetic.main.new_guest.*
//import kotlinx.android.synthetic.main.task_editdetail.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class GuestCreateEdit : Fragment() {

//    private var chiptextvalue: String? = null
//    private var categoryrsvp: String = ""
//    private var categorycompanions: String = ""
//    private var categoryguests: String = ""

    private lateinit var guestItem: Guest

    //private lateinit var activitymenu: Menu
    private lateinit var binding: NewGuestBinding

    private lateinit var user: User
    private lateinit var context: Context

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
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        user = User().getUser(context)
        binding = DataBindingUtil.inflate(inflater, R.layout.new_guest, container, false)

        val toolbar = requireActivity().findViewById<MaterialToolbar>(R.id.toolbar)
        toolbar.findViewById<TextView>(R.id.appbartitle)?.text = getString(R.string.new_guest)

        guestItem = arguments?.getParcelable("guest") ?: Guest()

        if (guestItem.key.isNotEmpty()) {
            toolbar.findViewById<TextView>(R.id.appbartitle)?.text = getString(R.string.edit_guest)
            binding.nameinputedit.setText(guestItem.name)
            binding.phoneinputedit.setText(guestItem.phone)
            binding.mailinputedit.setText(guestItem.email)

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
        binding.nameinputedit.onFocusChangeListener = focusChangeListener
        binding.phoneinputedit.onFocusChangeListener = focusChangeListener
        binding.mailinputedit.onFocusChangeListener = focusChangeListener

        val tm = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        binding.phoneinputedit.apply {
            addTextChangedListener(PhoneNumberFormattingTextWatcher())

            setOnFocusChangeListener { view, hasFocus ->
                if (hasFocus) {
                    val formattedNumber = PhoneNumberUtils.formatNumber(
                        text.toString(),
                        tm.simCountryIso
                    )
                    // Check if formattedNumber is null
                    if (formattedNumber != null) {
                        text = Editable.Factory.getInstance().newEditable(formattedNumber)
                    }

                    // Clear the error from the parent TextInputLayout
                    val parentLayout = view.parent.parent as? TextInputLayout
                    parentLayout?.error = null
                }
            }
        }

        binding.button.setOnClickListener {
            AnalyticsManager.getInstance().trackUserInteraction(SCREEN_NAME, "Save_Guest")
            val isValid = validateAllInputs()
            if (isValid) {
                saveguest()
            }
        }
        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        if (guestItem.key.isNotEmpty()) {
            inflater.inflate(R.menu.guests_menu2, menu)
            menu.findItem(R.id.remove_guest).isEnabled = true
            if (!binding.phoneinputedit.text.isNullOrBlank()) {
                menu.findItem(R.id.call_vendor).isEnabled = true
            }
            if (!binding.mailinputedit.text.isNullOrBlank()) {
                menu.findItem(R.id.email_vendor).isEnabled = true
            }
        }
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.remove_guest -> {
                AnalyticsManager.getInstance().trackUserInteraction(SCREEN_NAME, "Remove_Guest")
                AlertDialog.Builder(context)
                    .setTitle(getString(R.string.delete_message))
                    .setMessage(getString(R.string.delete_entry))
                    .setPositiveButton(
                        android.R.string.yes
                    ) { dialog, which ->
                        AnalyticsManager.getInstance()
                            .trackUserInteraction(SCREEN_NAME, "Delete_Guest")
                        try {
                            deleteGuest(context, user, guestItem)
                            finish()
                        } catch (e: GuestDeletionException) {
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

            R.id.call_vendor -> {
                AnalyticsManager.getInstance()
                    .trackUserInteraction(VendorCreateEdit.SCREEN_NAME, "Call_Vendor")
                val intent = Intent(
                    Intent.ACTION_DIAL,
                    Uri.fromParts("tel", binding.phoneinputedit.text.toString(), null)
                )
                startActivity(intent)
                true
            }

            R.id.email_vendor -> {
                AnalyticsManager.getInstance()
                    .trackUserInteraction(VendorCreateEdit.SCREEN_NAME, "Email_Vendor")
                try {
                    val intent = Intent(Intent.ACTION_SENDTO)
                    intent.data = Uri.parse("mailto:") // only email apps should handle this
                    intent.putExtra(Intent.EXTRA_EMAIL, binding.mailinputedit.text.toString())
                    intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.requestinfo))
                    startActivity(intent)
                } catch (ex: ActivityNotFoundException) {
                    Toast.makeText(
                        context,
                        getString(R.string.error_noemailclient),
                        Toast.LENGTH_SHORT
                    ).show()
                }
                true
            }

            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }

    private fun validateAllInputs(): Boolean {
        var isValid = true
        val validator = InputValidator(context)

        val nameValidation =
            validator.validate(binding.nameinputedit)
        if (!nameValidation) {
            binding.nameinput.error = validator.errorCode
            isValid = false
        }

        val phoneValidation =
            validator.validate(binding.phoneinputedit)
        if (!phoneValidation) {
            binding.phoneinput.error = validator.errorCode
            isValid = false
        }

        val mailValidation =
            validator.validate(binding.mailinputedit)
        if (!mailValidation) {
            binding.mailinput.error = validator.errorCode
            isValid = false
        }

        val guestEvent = Guest()
        val guestCount = guestEvent.getGuestCount(context)!!
        val newGuestBalance = user.numberguests - (guestCount + 1)
        if (newGuestBalance > 0) {
            showBanner(getString(R.string.banner_withinguestcount), false)
        } else {
            showBanner(getString(R.string.banner_outguestcount), true)
            isValid = false
        }
        return isValid
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
        var chiptextValue = chipselected.text.toString()
        val categoryRSVP = when (chiptextValue) {
            getString(R.string.yes) -> "y"
            getString(R.string.no) -> "n"
            getString(R.string.pending) -> "pending"
            else -> "pending"
        }

        id = binding.companionsgroup.checkedChipId
        chipselected = binding.companionsgroup.findViewById(id)
        chiptextValue = chipselected.text.toString()
        val categoryCompanions = when (chiptextValue) {
            getString(R.string.adult) -> "adult"
            getString(R.string.child) -> "child"
            getString(R.string.baby) -> "baby"
            getString(R.string.none) -> "none"
            else -> "none"
        }

        id = binding.guestgroup.checkedChipId
        chipselected = binding.guestgroup.findViewById(id)
        chiptextValue = chipselected.text.toString()
        var categoryGuests = when (chiptextValue) {
            getString(R.string.family) -> "family"
            getString(R.string.extendedfamily) -> "extendedfamily"
            getString(R.string.familyfriends) -> "familyfriends"
            getString(R.string.oldfriends) -> "oldfriends"
            getString(R.string.coworkers) -> "coworkers"
            getString(R.string.others) -> "others"
            else -> "others"
        }

        if (categoryRSVP == "n") {
            categoryGuests = "notattending"
        }

        guestItem.apply {
            rsvp = categoryRSVP
            companion = categoryCompanions
            table = categoryGuests
            name = binding.nameinputedit.text.toString()
            phone = binding.phoneinputedit.text.toString()
            email = binding.mailinputedit.text.toString()
        }

        if (guestItem.key.isEmpty()) {
                try {
                    addGuest(context, user, guestItem)
                } catch (e: GuestCreationException) {
                    AnalyticsManager.getInstance().trackError(
                        SCREEN_NAME,
                        e.message.toString(),
                        "addGuest()",
                        e.stackTraceToString()
                    )
                    Log.e(TAG, e.message.toString())
                }
        } else {
            try {
                editGuest(context, user, guestItem)
            } catch (e: GuestCreationException) {
                AnalyticsManager.getInstance().trackError(
                    SCREEN_NAME,
                    e.message.toString(),
                    "editGuest()",
                    e.stackTraceToString()
                )
                Log.e(TaskCreateEdit.TAG, e.message.toString())
            }
        }
        finish()
    }

    private fun showBanner(message: String, dismiss: Boolean) {
        //binding.taskname.visibility = View.INVISIBLE
        val fadeInAnimation = AnimationUtils.loadAnimation(context, R.anim.fade_in)
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

    fun finish() {
        val fragment = GuestsAll()
        Handler(Looper.getMainLooper()).postDelayed({
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit()
        }, 500)
    }

    companion object {
        const val SCREEN_NAME = "Guest_CreateEdit"
        const val TAG = "GuestCreateEdit"
        const val CALLER = "guest"
    }
}


package com.bridesandgrooms.event.UI.Fragments

import Application.AnalyticsManager
import Application.GuestCreationException
import Application.GuestDeletionException
import Application.UserRetrievalException
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
import com.bridesandgrooms.event.R
import com.bridesandgrooms.event.UI.FieldValidators.InputValidator
import com.bridesandgrooms.event.UI.Fragments.GuestsAll.Companion
import com.bridesandgrooms.event.databinding.NewGuestBinding
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.chip.Chip
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.functions.FirebaseFunctionsException
import kotlinx.coroutines.launch

//import kotlinx.android.synthetic.main.new_guest.*
//import kotlinx.android.synthetic.main.task_editdetail.*


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

    val companionCounts = mutableMapOf("Adult" to 0, "Child" to 0, "Baby" to 0)

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

        lifecycleScope.launch {
            try {
                user = User.getUserAsync()
            } catch (e: UserRetrievalException) {
                displayErrorMsg(getString(R.string.errorretrieveuser))
            } catch (e: Exception) {
                displayErrorMsg(getString(R.string.error_unknown) + " - " + e.toString())
            }
        }
        binding = DataBindingUtil.inflate(inflater, R.layout.new_guest, container, false)

        val toolbar = requireActivity().findViewById<MaterialToolbar>(R.id.toolbar)
        toolbar.findViewById<TextView>(R.id.appbartitle)?.text = getString(R.string.new_guest)

        guestItem = arguments?.getParcelable("guest") ?: Guest()

        if (guestItem.key.isNotEmpty()) {
            toolbar.findViewById<TextView>(R.id.appbartitle)?.text = getString(R.string.edit_guest)
            binding.nameinputedit.setText(guestItem.name)
            binding.phoneinputedit.setText(guestItem.phone)
            binding.mailinputedit.setText(guestItem.email)

//            when (guestItem.companion) {
//                "adult" -> binding.companionsgroup.check(binding.chipadult.id)
//                "child" -> binding.companionsgroup.check(binding.chipchild.id)
//                "baby" -> binding.companionsgroup.check(binding.chipbaby.id)
//                else -> binding.companionsgroup.check(binding.chipnone.id)
//            }
            decodeCompanionString(guestItem.companion)

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
            AnalyticsManager.getInstance().trackUserInteraction(SCREEN_NAME, "Save_Guest", null)
            val isValid = validateAllInputs()
            if (isValid) {
                saveguest()
            }
        }
        setupCounters()
        return binding.root
    }

    private fun setupCounters() {
        val counters = listOf(
            Triple(binding.buttonMinusAdult, binding.textCountAdult, binding.buttonPlusAdult),
            Triple(binding.buttonMinusChild, binding.textCountChild, binding.buttonPlusChild),
            Triple(binding.buttonMinusBaby, binding.textCountBaby, binding.buttonPlusBaby)
        )

        counters.forEachIndexed { index, (minusButton, textView, plusButton) ->
            val category = when (index) {
                0 -> "Adult"
                1 -> "Child"
                2 -> "Baby"
                else -> ""
            }

            fun updateText() {
                textView.text = companionCounts[category].toString()
            }

            minusButton.setOnClickListener {
                if (companionCounts[category]!! > 0) {
                    companionCounts[category] = companionCounts[category]!! - 1
                    updateText()
                }
            }

            plusButton.setOnClickListener {
                if (companionCounts[category]!! < 5) {
                    companionCounts[category] = companionCounts[category]!! + 1
                    updateText()
                }
            }

            updateText() // Initialize the text
        }
    }

    fun getCompanionString(): String {
        val companions = mutableListOf<String>()

        if (companionCounts["Adult"]!! > 0) companions.add("${companionCounts["Adult"]} Adult(s)")
        if (companionCounts["Child"]!! > 0) companions.add("${companionCounts["Child"]} Child(ren)")
        if (companionCounts["Baby"]!! > 0) companions.add("${companionCounts["Baby"]} Baby(ies)")

        return if (companions.isEmpty()) "None" else companions.joinToString(", ")
    }

    fun decodeCompanionString(companionString: String) {
        // Reset all values to 0 before updating
        companionCounts["Adult"] = 0
        companionCounts["Child"] = 0
        companionCounts["Baby"] = 0

        if (companionString == "None") {
            updateCounterUI()
            return
        }

        // Split the string by ", " to get each category
        val regex = """(\d+) (Adult|Child|Baby)""".toRegex()

        regex.findAll(companionString).forEach { match ->
            val count = match.groupValues[1].toInt()
            val category = match.groupValues[2]

            companionCounts[category] = count
        }

        updateCounterUI() // Apply changes to the UI
    }

    private fun updateCounterUI() {
        binding.textCountAdult.text = companionCounts["Adult"].toString()
        binding.textCountChild.text = companionCounts["Child"].toString()
        binding.textCountBaby.text = companionCounts["Baby"].toString()
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        if (guestItem.key.isNotEmpty()) {
            inflater.inflate(R.menu.guests_menu2, menu)
            menu.findItem(R.id.remove_guest).isEnabled = true
            if (!binding.phoneinputedit.text.isNullOrBlank()) {
                menu.findItem(R.id.call_guest).isEnabled = true
            }
            if (!binding.mailinputedit.text.isNullOrBlank()) {
                menu.findItem(R.id.email_guest).isEnabled = true
            }
        }
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.remove_guest -> {
                AnalyticsManager.getInstance().trackUserInteraction(SCREEN_NAME, "Remove_Guest", null)
                AlertDialog.Builder(context)
                    .setTitle(getString(R.string.delete_message))
                    .setMessage(getString(R.string.delete_entry))
                    .setPositiveButton(
                        android.R.string.yes
                    ) { dialog, which ->
                        AnalyticsManager.getInstance()
                            .trackUserInteraction(SCREEN_NAME, "Delete_Guest", null)
                        try {
                            deleteGuest(guestItem.key)
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

            R.id.call_guest -> {
                AnalyticsManager.getInstance()
                    .trackUserInteraction(SCREEN_NAME, "Call_Vendor", null)
                val intent = Intent(
                    Intent.ACTION_DIAL,
                    Uri.fromParts("tel", binding.phoneinputedit.text.toString(), null)
                )
                startActivity(intent)
                true
            }

            R.id.email_guest -> {
                AnalyticsManager.getInstance()
                    .trackUserInteraction(SCREEN_NAME, "Email_Vendor", null)
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
        val guestCount = guestEvent.getGuestCount()!!
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
        //binding.companionsgroup.isEnabled = false
        binding.button.isEnabled = false

        var id = binding.rsvpgroup.checkedChipId
        var chipselected = binding.rsvpgroup.findViewById<Chip>(id)

        val categoryRSVP = chipselected?.tag?.toString() ?: run {
            // Handle fallback here
            //displayToastMsg(getString(R.string.select_rsvp_error))
            return
        }

//        id = binding.companionsgroup.checkedChipId
//        chipselected = binding.companionsgroup.findViewById(id)
//        chiptextValue = chipselected.text.toString()
//        val categoryCompanions = when (chiptextValue) {
//            getString(R.string.adult) -> "adult"
//            getString(R.string.child) -> "child"
//            getString(R.string.baby) -> "baby"
//            getString(R.string.none) -> "none"
//            else -> "none"
//        }

        id = binding.guestgroup.checkedChipId
        chipselected = binding.guestgroup.findViewById(id)
        val chiptextValue = chipselected.text.toString()
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
            companion = getCompanionString()
            table = categoryGuests
            name = binding.nameinputedit.text.toString()
            phone = binding.phoneinputedit.text.toString()
            email = binding.mailinputedit.text.toString()
        }

        if (guestItem.key.isEmpty()) {
            try {
                addGuest(guestItem)
                // ✅ Pass `finish()` as a callback to execute only after user action
                if (guestItem.key.isNotEmpty()) {
                    showSendInvitationDialog(
                        userId = user.userid!!,
                        eventId = user.eventid,
                        guestId = guestItem.key,
                        language = user.language
                    ) {
                        finish()
                    }
                }
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
                editGuest(guestItem)
                finish()
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
    }

    private fun showSendInvitationDialog(
        userId: String,
        eventId: String,
        guestId: String,
        language: String,
        onDialogClosed: () -> Unit // ✅ Callback function
    ) {
        val alertDialog = AlertDialog.Builder(context)
            .setTitle("Send Invitation")
            .setMessage("Would you like to send an invitation to this guest?")
            .setPositiveButton("Send") { _, _ ->
                sendRSVPInvitation(userId, eventId, guestId, language)
                onDialogClosed()
            }
            .setNegativeButton("Don't Send") { _, _ ->
                onDialogClosed() // ✅ Ensure `finish()` is called even if the user declines
            }
            .setCancelable(false)
            .create()

        alertDialog.show()
    }

    private fun sendRSVPInvitation(userId: String, eventId: String, guestId: String, language: String) {
        val functions = FirebaseFunctions.getInstance()

        val requestData  = hashMapOf(
            "userId" to userId,
            "eventId" to eventId,
            "guestId" to guestId,
            "language" to language
        )

        // ✅ Debugging: Print request data before sending
        Log.d("RSVP", "Sending data to Cloud Function: $requestData ")

        functions
            .getHttpsCallable("sendRSVPInvitation")
            .call(requestData)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val response = task.result?.data as? Map<String, Any>
                    Log.d("RSVP", "Response received: $response")
                } else {
                    Log.e("RSVP", "Error sending invitation email", task.exception)
                    task.exception?.let {
                        if (it is FirebaseFunctionsException) {
                            Log.e("RSVP", "Cloud Function Error Code: ${it.code}")
                            Log.e("RSVP", "Cloud Function Error Details: ${it.details}")
                        }
                    }
                }
            }
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
                AnalyticsManager.getInstance().trackUserInteraction(SCREEN_NAME, "dismiss", "click")
                binding.bannerCardView.visibility = View.INVISIBLE
            }
        }
    }

    private fun displayErrorMsg(message: String) {
        Toast.makeText(
            requireContext(),
            message,
            Toast.LENGTH_LONG
        ).show()
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


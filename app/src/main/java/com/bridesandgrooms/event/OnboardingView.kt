package com.bridesandgrooms.event

import Application.AnalyticsManager
import Application.PaymentCreationException
import Application.TaskCreationException
import Application.UserOnboardingException
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bridesandgrooms.event.UI.Dialogs.DatePickerFragment
import TimePickerFragment
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.widget.Button
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.lifecycleScope
import com.bridesandgrooms.event.Functions.onBoarding
import com.bridesandgrooms.event.Model.Event
import com.bridesandgrooms.event.Model.User
//import kotlinx.android.synthetic.main.onboarding_name.*
import kotlinx.coroutines.*
import android.util.Log
import android.view.View

import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import com.bridesandgrooms.event.Functions.PermissionUtils
import com.bridesandgrooms.event.Functions.addPayment
import com.bridesandgrooms.event.Functions.addTask
import com.bridesandgrooms.event.Model.Payment
import com.bridesandgrooms.event.Model.Task
import com.bridesandgrooms.event.Functions.RemoteConfigSingleton.getautocreateTaskPayment
import com.bridesandgrooms.event.Functions.getUserCountry
import com.bridesandgrooms.event.Model.Permission
import com.bridesandgrooms.event.UI.FieldValidators.InputValidator
import com.bridesandgrooms.event.UI.Fragments.TaskCreateEdit
import com.bridesandgrooms.event.databinding.OnboardingNameBinding
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.TypeFilter
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import java.util.*

class OnboardingView : AppCompatActivity() {

    private var eventlocationname: String? = null
    private var eventplaceid: String? = null
    private var eventlatitude = 0.0
    private var eventlongitude = 0.0
    private var eventaddress: String? = null

    private val TIME_DELAY = 2000
    private var back_pressed: Long = 0

    private var userSession: User = User()
    private lateinit var binding: OnboardingNameBinding
    private lateinit var autocompleteLauncher: ActivityResultLauncher<Intent>

    private var languagePlaces = ""
    private var countryPlaces = ""

    private val focusChangeListener = View.OnFocusChangeListener { view, hasFocus ->
        if (hasFocus && view is TextInputEditText) {
            val parentLayout = view.parent.parent as? TextInputLayout
            parentLayout?.error = null
        }
    }
    private val autoCreateTaskPayment = getautocreateTaskPayment()

    private val languageMap = mapOf(
        "English" to "en",
        "Español" to "es",
        "Portugues" to "pt",
        "Français" to "fr"
    )

    private val languages =
        arrayOf("English", "Español", "Portugues", "Français") // Example languages

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //---------- Places loading -------------
        if (!Places.isInitialized()) {
            languagePlaces = this@OnboardingView.resources.configuration.locales.get(0).language
            countryPlaces = getUserCountry(this@OnboardingView)
            val newLocale = Locale(languagePlaces, countryPlaces)
            Places.initialize(
                this@OnboardingView,
                getString(R.string.google_maps_key),
                newLocale
            )
        }

        autocompleteLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val data = result.data
                    val place = Autocomplete.getPlaceFromIntent(data!!)

                    binding.etlocation.setText(place.name)
                    eventplaceid = place.id
                    eventlatitude = place.latLng!!.latitude
                    eventlongitude = place.latLng!!.longitude
                    eventaddress = place.address
                    eventlocationname = place.name
                } else if (result.resultCode == AutocompleteActivity.RESULT_ERROR) {
                    val status = Autocomplete.getStatusFromIntent(result.data!!)
                    Log.e("Places", "Error: ${status.statusMessage}")
                    AnalyticsManager.getInstance().trackError(
                        SCREEN_NAME,
                        "errorPlaces",
                        "getPlaces", null
                    )
                }
            }

        binding = DataBindingUtil.setContentView(this, R.layout.onboarding_name)

        if (!checkOnboardingPermissions()) {
            requestPermissions()
        } else {
            continueOnboardingFlow()
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onResume() {
        super.onResume()

        if (checkOnboardingPermissions()) {
            continueOnboardingFlow()
        }
    }

    private fun continueOnboardingFlow() {
        showLanguageOnboarding()
    }

    // Save language selection in SharedPreferences
    private fun saveLanguagePreference(languageCode: String) {
        val prefs = getSharedPreferences("settings", Context.MODE_PRIVATE)
        prefs.edit().putString("app_language", languageCode).apply()
    }

    // Retrieve saved language
    private fun getSavedLanguage(): String {
        val prefs = getSharedPreferences("settings", Context.MODE_PRIVATE)
        return prefs.getString("app_language", "en") ?: "en"
    }

    // Change app language dynamically
    private fun setAppLocale(languageCode: String) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)

        val config = Configuration()
        config.setLocale(locale)
        baseContext.resources.updateConfiguration(config, baseContext.resources.displayMetrics)
    }

    private fun showPermissionsNeeded() {
        AnalyticsManager.getInstance().trackNavigationEvent(SCREEN_NAME, "PermissionsNeeded")
        binding.BGLogo.visibility = ConstraintLayout.INVISIBLE
        binding.languageonboarding.visibility = ConstraintLayout.INVISIBLE
        binding.submitlanguage.visibility = Button.INVISIBLE

        binding.permissions.root.visibility = ConstraintLayout.VISIBLE
    }

    private fun showLanguageOnboarding() {
        AnalyticsManager.getInstance().trackNavigationEvent(SCREEN_NAME, "LanguageOnboarding")
        binding.languageonboarding.visibility = ConstraintLayout.VISIBLE
        binding.submitlanguage.visibility = Button.VISIBLE
        binding.nameonboarding.visibility = ConstraintLayout.INVISIBLE
        binding.submituser.visibility = Button.INVISIBLE
        binding.eventonboarding.visibility = ConstraintLayout.INVISIBLE

        val languageAdapter =
            ArrayAdapter(
                this@OnboardingView,
                android.R.layout.simple_dropdown_item_1line,
                languages
            )

        binding.languageAutocomplete.setAdapter(languageAdapter)
        binding.languageAutocomplete.setOnItemClickListener { _, _, position, _ ->
            AnalyticsManager.getInstance()
                .trackUserInteraction(SCREEN_NAME, "LanguageAutoComplete", "click")
            val selectedLanguage = languages[position] // Get selected language name
            val languageCode =
                languageMap[selectedLanguage] ?: "en" // Default to English if not found

            // Save and apply the new language
            saveLanguagePreference(languageCode)
            setAppLocale(languageCode)

            // Restart activity to apply changes
            recreate()
        }

        // Apply saved language on load
        val savedLanguage = getSavedLanguage()
        setAppLocale(savedLanguage)

        val countries = getAllCountriesInEnglish()
        val countryAdapter =
            ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, countries)
        binding.countryAutocomplete.setAdapter(countryAdapter)

        //---------------------------------------
        // Hide Layout for Onboarding Event
        binding.submitlanguage.setOnClickListener {
            AnalyticsManager.getInstance()
                .trackNavigationEvent(SCREEN_NAME, "nextNameOnboarding")
            if (validateLanguageOnboard()) { //All fields corresponding to the Language section have been filled out correctly
                val selectedLanguageName = binding.languageAutocomplete.text.toString()
                val languageCode = languageMap[selectedLanguageName] ?: "en"
                userSession.language = languageCode
                val selectedCountryName = binding.countryAutocomplete.text.toString()
                userSession.country = selectedCountryName

                // Convert country name to ISO 2-letter code
                val countryCode = Locale.getISOCountries()
                    .firstOrNull { code ->
                        Locale("", code).getDisplayCountry(Locale.ENGLISH)
                            .equals(selectedCountryName, ignoreCase = true)
                    }
                    ?.uppercase() ?: "US" // Default to US if not found

                // Define countries using miles
                val countriesUsingMiles = setOf("US", "LR", "MM")

                // Store distance unit
                userSession.distanceunit =
                    if (countryCode in countriesUsingMiles) "miles" else "kilometers"

                showNameOnboarding()
            }
        }
    }


    private fun showNameOnboarding() {
        AnalyticsManager.getInstance().trackNavigationEvent(SCREEN_NAME, "NameOnboarding")
        binding.languageonboarding.visibility = ConstraintLayout.INVISIBLE
        binding.submitlanguage.visibility = Button.INVISIBLE
        binding.nameonboarding.visibility = ConstraintLayout.VISIBLE
        binding.submituser.visibility = Button.VISIBLE
        binding.eventonboarding.visibility = ConstraintLayout.INVISIBLE
        binding.submitevent.visibility = Button.INVISIBLE
        binding.submitevent2.visibility = Button.INVISIBLE

        binding.nameinputedit.onFocusChangeListener = focusChangeListener

        val genderMap = mapOf(
            "male" to getString(R.string.gender_male),
            "female" to getString(R.string.gender_female),
            "unspecified" to getString(R.string.gender_unspecified)
        )
        val genderReverseMap = genderMap.entries.associate { (k, v) -> v to k }
        val genderOptions = genderMap.values.toList()
        val genderAdapter = ArrayAdapter(
            this@OnboardingView,
            android.R.layout.simple_spinner_item,
            genderOptions
        )

        val ageRangeOptions =
            this@OnboardingView.resources.getStringArray(R.array.age_ranges).toList()
        val ageAdapter = ArrayAdapter(
            this@OnboardingView,
            android.R.layout.simple_dropdown_item_1line,
            ageRangeOptions
        )
        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        val roleCanonicalMap = mapOf(
            "bride" to getString(R.string.bride),
            "groom" to getString(R.string.groom)
        )
        val roleDisplayMap = roleCanonicalMap.entries.associate { (k, v) -> v to k }
        val roles = roleCanonicalMap.values.toList()
        val roleAdapter = ArrayAdapter(
            this@OnboardingView,
            android.R.layout.simple_dropdown_item_1line,
            roles
        )

        binding.genderAutocomplete.setAdapter(genderAdapter)
        binding.agerangeAutocomplete.setAdapter(ageAdapter)
        binding.roleAutocomplete.setAdapter(roleAdapter)

        binding.submituser.setOnClickListener {
            AnalyticsManager.getInstance()
                .trackNavigationEvent(SCREEN_NAME, "nextEventOnboarding_1")
            if (validateAllInputsNameOnboard()) {
                userSession.shortname = binding.nameinputedit.text.toString()
                val selectedLabel = binding.genderAutocomplete.text.toString()
                userSession.gender = genderReverseMap[selectedLabel] ?: "unspecified"
                userSession.agerange = binding.agerangeAutocomplete.text.toString()
                val selectedRoleDisplay = binding.roleAutocomplete.text.toString()
                val roleCode = roleDisplayMap[selectedRoleDisplay]
                if (!roleCode.isNullOrBlank()) {
                    userSession.role = roleCode
                }
                showEventOnboarding()
            }
        }
    }

    private fun showEventOnboarding() {
        AnalyticsManager.getInstance().trackNavigationEvent(SCREEN_NAME, "EventOnboarding_1")
        binding.nameonboarding.visibility = ConstraintLayout.INVISIBLE
        binding.submituser.visibility = Button.INVISIBLE
        binding.eventonboarding.visibility = ConstraintLayout.VISIBLE
        binding.eventonboarding.findViewById<ConstraintLayout>(R.id.page1).visibility =
            ConstraintLayout.VISIBLE
        binding.eventonboarding.findViewById<ConstraintLayout>(R.id.page2).visibility =
            ConstraintLayout.INVISIBLE
        binding.submitevent.visibility = Button.VISIBLE
        binding.submitevent2.visibility = Button.INVISIBLE

        binding.etname.onFocusChangeListener = focusChangeListener
        binding.etbudget.onFocusChangeListener = focusChangeListener
        binding.etnumberguests.onFocusChangeListener = focusChangeListener

        binding.submitevent.setOnClickListener {
            AnalyticsManager.getInstance()
                .trackNavigationEvent(SCREEN_NAME, "nextEventOnboarding_2")
            if (validateAllInputsEventOnboard()) {
                val rawFormatted = binding.etbudget.text.toString()
                val cleaned = rawFormatted.replace(Regex("[^\\d.,]"), "")
                val normalized =
                    cleaned.replace(".", "")
                        .replace(",", ".") // for EU-style input
                val parsedValue = normalized.toDoubleOrNull() ?: 0.0
                userSession.eventbudget =
                    String.format(Locale.US, "%.2f", parsedValue)
                userSession.numberguests =
                    binding.etnumberguests.text.toString().toInt()
                showEventOnboarding2()
            }
        }
    }

    private fun showEventOnboarding2() {
        AnalyticsManager.getInstance().trackNavigationEvent(SCREEN_NAME, "EventOnboarding_2")
        binding.nameonboarding.visibility = ConstraintLayout.INVISIBLE
        binding.submituser.visibility = Button.INVISIBLE
        binding.eventonboarding.visibility = ConstraintLayout.VISIBLE
        binding.eventonboarding.findViewById<ConstraintLayout>(R.id.page1).visibility =
            ConstraintLayout.INVISIBLE
        binding.eventonboarding.findViewById<ConstraintLayout>(R.id.page2).visibility =
            ConstraintLayout.VISIBLE
        binding.submitevent.visibility = Button.INVISIBLE
        binding.submitevent2.visibility = Button.VISIBLE

        binding.etPlannedDate.setOnClickListener {
            AnalyticsManager.getInstance()
                .trackUserInteraction(
                    SCREEN_NAME,
                    "etPlannedDate",
                    "click"
                )
            showDatePickerDialog()
        }

        binding.etPlannedTime.setOnClickListener {
            AnalyticsManager.getInstance()
                .trackUserInteraction(
                    SCREEN_NAME,
                    "etPlannedTime",
                    "click"
                )
            showTimePickerDialog()
        }

        binding.etlocation.setOnClickListener {
            AnalyticsManager.getInstance()
                .trackUserInteraction(
                    SCREEN_NAME,
                    "etlocation",
                    "click"
                )
            val fields = listOf(
                Place.Field.ID,
                Place.Field.NAME,
                Place.Field.LAT_LNG,
                Place.Field.ADDRESS
            )
            val intent = Autocomplete.IntentBuilder(
                AutocompleteActivityMode.OVERLAY,
                fields
            )
                .setCountry(countryPlaces)
                .setTypeFilter(TypeFilter.ESTABLISHMENT)
                .build(this@OnboardingView)

            if (!isFinishing) {
                binding.etlocation.post {
                    autocompleteLauncher.launch(intent)
                }
            }
        }

        binding.submitevent2.setOnClickListener {
            AnalyticsManager.getInstance().trackUserInteraction(
                SCREEN_NAME,
                "submitOnboarding",
                "click"
            )
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

            lifecycleScope.launch {
                try {
                    userSession.apply {
                        userid = intent.getStringExtra("userid").toString()
                        email = intent.getStringExtra("email").toString()
                        authtype = intent.getStringExtra("authtype").toString()
                        status =
                            "0" // Hardcoded value indicating the user is new and needs to go through the Welcome sequence
                    }
                    onBoarding(userSession, event)
                    showBanner(
                        getString(R.string.successadduser),
                        false
                    )
                    showBanner(
                        getString(R.string.eventcreated),
                        false
                    )

                    if (autoCreateTaskPayment) {
                        val taskitem = Task(dummy = true)
                        val paymentitem = Payment(dummy = true)
                        try {
                            addTask(
                                taskitem
                            )
                            addPayment(
                                paymentitem
                            )
                        } catch (e: TaskCreationException) {
                            displayToastMsg(getString(R.string.errorTaskCreation) + e.toString())
                            AnalyticsManager.getInstance()
                                .trackError(
                                    SCREEN_NAME,
                                    "errorTask",
                                    "TaskCreation", null
                                )
                        } catch (e: PaymentCreationException) {
                            displayToastMsg(getString(R.string.errorPaymentCreation) + e.toString())
                            AnalyticsManager.getInstance()
                                .trackError(
                                    SCREEN_NAME,
                                    "errorPayment",
                                    "PaymentCreation", null
                                )
                        }
                    }
                    AnalyticsManager.getInstance()
                        .setUserProperties(
                            userSession.userid,
                            userSession.role,
                            userSession.numberguests,
                            userSession.eventbudget,
                            userSession.gender,
                            userSession.agerange
                        )

                } catch (e: UserOnboardingException) {
                    displayToastMsg(getString(R.string.errorUserOnboarding) + e.toString())
                    AnalyticsManager.getInstance().trackError(
                        SCREEN_NAME,
                        "errorOnboarding",
                        "OnboardingProcess", null
                    )
                } catch (e: Exception) {
                    Log.e(TAG, e.toString())
                }
                finish()
            }
            val resultIntent = Intent()
            setResult(Activity.RESULT_OK, resultIntent)
        }
    }

    private fun validateLanguageOnboard(): Boolean {
        var isValid = true
        val validator = InputValidator(this)

        val spinnerlanguageValidation =
            validator.validateSpinner(binding.languageAutocomplete.toString())
        if (!spinnerlanguageValidation) {
            binding.languageAutocomplete.error = validator.errorCode
            isValid = false
        }

        val spinnercountryValidation =
            validator.validateSpinner(binding.countryAutocomplete.toString())
        if (!spinnercountryValidation) {
            binding.countryAutocomplete.error = validator.errorCode
            isValid = false
        }
        return isValid
    }

    private fun validateAllInputsNameOnboard(): Boolean {
        var isValid = true
        val validator = InputValidator(this)

        val nameValidation =
            validator.validate(binding.nameinputedit)
        if (!nameValidation) {
            binding.nameinputedit.error = validator.errorCode
            isValid = false
        }

        val genderValidation =
            validator.validateSpinner(binding.genderAutocomplete.toString())
        if (!genderValidation) {
            binding.genderAutocomplete.error = validator.errorCode
            isValid = false
        }

        val ageValidation =
            validator.validateSpinner(binding.agerangeAutocomplete.toString())
        if (!ageValidation) {
            binding.agerangeAutocomplete.error = validator.errorCode
            isValid = false
        }

        val roleValidation =
            validator.validateSpinner(binding.roleAutocomplete.toString())
        if (!roleValidation) {
            binding.roleAutocomplete.error = validator.errorCode
            isValid = false
        }
        return isValid
    }

    private fun validateAllInputsEventOnboard(): Boolean {
        var isValid = true
        val validator = InputValidator(this)

        val nameValidation =
            validator.validate(binding.etname)
        if (!nameValidation) {
            binding.etname.error = validator.errorCode
            isValid = false
        }

        val budgetValidation =
            validator.validate(binding.etbudget)
        if (!budgetValidation) {
            binding.etbudget.error = validator.errorCode
            isValid = false
        }

        val guestValidation =
            validator.validate(binding.etnumberguests)
        if (!guestValidation) {
            binding.etnumberguests.error = validator.errorCode
            isValid = false
        }
        return isValid
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun alertBox() {
        val builder = android.app.AlertDialog.Builder(this)
        builder.setTitle(getString(R.string.lackpermissions_message))
        builder.setMessage(getString(R.string.lackpermissions_message))

        builder.setPositiveButton(
            getString(R.string.accept)
        ) { _, _ ->
            requestPermissions()
            AnalyticsManager.getInstance()
                .trackContentInteraction(SCREEN_NAME, "AcceptPermissionsRequest")
        }
        builder.setNegativeButton(
            "Cancel"
        ) { p0, _ ->
            p0!!.dismiss()
            AnalyticsManager.getInstance()
                .trackContentInteraction(SCREEN_NAME, "CancelPermissionsRequest")
        }

        val dialog = builder.create()
        dialog.show()
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun checkOnboardingPermissions(): Boolean {
        return PermissionUtils.checkPermissions(this, "calendar")
                //&& PermissionUtils.checkPermissions(this, "storage")
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun requestPermissions() {
        val calendarPermissions = PermissionUtils.requestPermissionsList("calendar")
        //val storagePermissions = PermissionUtils.requestPermissionsList("storage")
        //val allPermissions = calendarPermissions + storagePermissions
        ActivityCompat.requestPermissions(this, calendarPermissions, PERMISSION_CODE)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == PERMISSION_CODE) {
            if (grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                // ✅ Permissions granted, continue
                continueOnboardingFlow()
            } else {
                // ❌ Permissions denied — show fallback layout
                showPermissionsNeeded()

                // Get localized wording and icon
                val language = resources.configuration.locales[0].language
                val permission = Permission.getPermission("calendar") // Change as needed

                val iconResId = resources.getIdentifier(
                    permission.drawable, "drawable", packageName
                )
                binding.permissions.permissionicon.setImageResource(iconResId)

                val wording = when (language) {
                    "es" -> permission.permission_wording_es
                    "fr" -> permission.permission_wording_fr
                    "pt" -> permission.permission_wording_pt
                    else -> permission.permission_wording_en
                }
                binding.permissions.permissionwording.text = wording

                // Button to open app settings
                binding.permissions.permissionsbutton.setOnClickListener {
                    AnalyticsManager.getInstance()
                        .trackSettingsChange(SCREEN_NAME, "permissionwording")

                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                        data = Uri.fromParts("package", packageName, null)
                    }
                    startActivity(intent)
                }
            }
        }
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
            val inputMethodManager =
                getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
        }
    }

    private fun getAllCountriesInEnglish(): List<String> {
        val locales = Locale.getISOCountries().map { countryCode ->
            Locale("", countryCode).getDisplayCountry(Locale.ENGLISH)
        }
        return locales.sorted()
    }


    private fun displayToastMsg(message: String) {
        Toast.makeText(
            this@OnboardingView,
            message,
            Toast.LENGTH_LONG
        ).show()
    }

    private fun showBanner(message: String, dismiss: Boolean) {
        //binding.taskname.visibility = View.INVISIBLE
        val fadeInAnimation = AnimationUtils.loadAnimation(this, R.anim.fade_in)
        binding.bannerCardView.startAnimation(fadeInAnimation)

        binding.bannerCardView.visibility = View.VISIBLE
        binding.bannerText.text = message
        //getString(R.string.number_guests)
        if (dismiss) {
            binding.dismissButton.visibility = View.VISIBLE
            binding.dismissButton.setOnClickListener {
                AnalyticsManager.getInstance()
                    .trackUserInteraction(SCREEN_NAME, "dismissButton", "click")
                binding.bannerCardView.visibility = View.INVISIBLE
            }
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

    companion object {
        private const val TAG = "OnboardingView"
        private const val SCREEN_NAME = "onboarding_name.xml"
        internal const val PERMISSION_CODE = 1001
    }
}


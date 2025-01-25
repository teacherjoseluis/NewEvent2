package com.bridesandgrooms.event.UI.Fragments

import Application.AnalyticsManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.bridesandgrooms.event.Functions.editUser
import Application.MyFirebaseApp
import Application.UserEditionException
import Application.UserRetrievalException
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import com.bridesandgrooms.event.DashboardEvent
import com.bridesandgrooms.event.IOnBackPressed
import com.bridesandgrooms.event.Model.User
import com.bridesandgrooms.event.R
import com.bridesandgrooms.event.UI.FieldValidators.InputValidator
import com.bridesandgrooms.event.databinding.SettingsBinding
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.analytics.FirebaseAnalytics
import kotlinx.coroutines.launch

class Settings : Fragment(), IOnBackPressed {

    private lateinit var binding: SettingsBinding
    private lateinit var context: Context
    private lateinit var userSession: User

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

        binding = DataBindingUtil.inflate(inflater, R.layout.settings, container, false)

        val toolbar = requireActivity().findViewById<MaterialToolbar>(R.id.toolbar)
        toolbar.findViewById<TextView>(R.id.appbartitle)?.text = getString(R.string.settings)

        lifecycleScope.launch {
            try {
                userSession = User.getUserAsync()
            } catch (e: UserRetrievalException) {
                displayErrorMsg(getString(R.string.errorretrieveuser))
            } catch (e: Exception) {
                displayErrorMsg(getString(R.string.error_unknown) + " - " + e.toString())
            }

            if (userSession.shortname.isNotEmpty()) {
                binding.nameinput.setText(userSession.shortname)
            }

            if (userSession.eventbudget.isNotEmpty()) {
                binding.budgetinput.setText(userSession.eventbudget)
            }

            if (userSession.numberguests.toString().isNotEmpty()) {
                binding.numberguestsinput.setText(userSession.numberguests.toString())
            }

            val roles = arrayOf("Bride", "Groom") // Example roles
            val roleAdapter =
                ArrayAdapter(context, android.R.layout.simple_dropdown_item_1line, roles)
            binding.roleAutocomplete.setAdapter(roleAdapter)

            if (userSession.role.isNotEmpty()) {
                val matchingRole =
                    roles.firstOrNull { it.equals(userSession.role, ignoreCase = true) }
                if (matchingRole != null) {
                    binding.roleAutocomplete.setText(
                        matchingRole,
                        false
                    ) // Set text without filtering
                }
            }

            val countries = arrayOf("MX", "CL", "PE") // Example countries
            val countryAdapter = ArrayAdapter(
                context,
                android.R.layout.simple_dropdown_item_1line,
                countries
            )
            binding.countryAutocomplete.setAdapter(countryAdapter)

            if (userSession.country.isNotEmpty()) {
                val matchingCountry =
                    countries.firstOrNull { it.equals(userSession.country, ignoreCase = true) }
                if (matchingCountry != null) {
                    binding.countryAutocomplete.setText(
                        matchingCountry,
                        false
                    ) // Set text without filtering
                }
            }

            val distances = arrayOf("miles", "kilometers") // Example distances
            val distanceAdapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                distances
            )
            binding.distanceAutocomplete.setAdapter(distanceAdapter)

            if (userSession.distanceunit.isNotEmpty()) {
                val matchingDistance =
                    distances.firstOrNull { it.equals(userSession.distanceunit, ignoreCase = true) }
                if (matchingDistance != null) {
                    binding.distanceAutocomplete.setText(
                        matchingDistance,
                        false
                    ) // Set text without filtering
                }
            }

            binding.roleAutocomplete.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                        userSession.role = when (p2) {
                            0 -> "Bride"
                            1 -> "Groom"
                            else -> "Bride"
                        }
                    }

                    override fun onNothingSelected(p0: AdapterView<*>?) {
                        TODO("Not yet implemented")
                    }
                }


            binding.nameinput.onFocusChangeListener = focusChangeListener
            binding.budgetinput.onFocusChangeListener = focusChangeListener
            binding.numberguestsinput.onFocusChangeListener = focusChangeListener

            fun validateAllInputs(): Boolean {
                var isValid = true
                val validator = InputValidator(context)

                val nameValidation =
                    validator.validate(binding.nameinput)
                if (!nameValidation) {
                    binding.nameinput.error = validator.errorCode
                    isValid = false
                }

                val spinnerValidation =
                    validator.validateSpinner(binding.roleAutocomplete.toString())
                if (!spinnerValidation) {
                    binding.roleAutocomplete.error = validator.errorCode
                    isValid = false
                }

                val spinnerValidation2 =
                    validator.validateSpinner(binding.countryAutocomplete.toString())
                if (!spinnerValidation2) {
                    binding.countryAutocomplete.error = validator.errorCode
                    isValid = false
                }

                val budgetValidation =
                    validator.validate(binding.budgetinput)
                if (!budgetValidation) {
                    binding.budgetinput.error = validator.errorCode
                    isValid = false
                }

                val guestNumberValidation =
                    validator.validate(binding.numberguestsinput)
                if (!guestNumberValidation) {
                    binding.numberguestsinput.error = validator.errorCode
                    isValid = false
                }

                val spinnerValidation3 =
                    validator.validateSpinner(binding.distanceAutocomplete.toString())
                if (!spinnerValidation3) {
                    binding.distanceAutocomplete.error = validator.errorCode
                    isValid = false
                }
                return isValid
            }

            binding.settingsbutton.setOnClickListener()
            {
                AnalyticsManager.getInstance()
                    .trackUserInteraction(MainActivity.SCREEN_NAME, "Save_Settings")
                val isValid = validateAllInputs()
                if (isValid) {

                    userSession.apply {
                        shortname = binding.nameinput.text.toString()
                        role = binding.roleAutocomplete.text.toString()
                        country = binding.countryAutocomplete.text.toString()
                        eventbudget = binding.budgetinput.text.toString()
                        numberguests = binding.numberguestsinput.text.toString().toIntOrNull() ?: 0
                        distanceunit = binding.distanceAutocomplete.text.toString()
                    }

                    lifecycleScope.launch {
                        try {
                            editUser(userSession)
                            finish()
                        } catch (e: UserEditionException) {
                            AnalyticsManager.getInstance().trackError(
                                SCREEN_NAME,
                                e.message.toString(),
                                "EditUser",
                                e.stackTraceToString()
                            )
                            Log.e(TAG, e.message.toString())
                        }
                    }
                }
            }
        }

        binding.privacypolicy.setOnClickListener {
            // ------- Analytics call ----------------
            val bundle = Bundle()
            bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "PRIVACYPOLICY")
            MyFirebaseApp.mFirebaseAnalytics.logEvent(
                FirebaseAnalytics.Event.SELECT_ITEM,
                bundle
            )
            //----------------------------------------
            val uris = Uri.parse(getString(R.string.privacypolicy))
            val intents = Intent(Intent.ACTION_VIEW, uris)
            val b = Bundle()
            b.putBoolean("new_window", true)
            intents.putExtras(b)
            requireContext().startActivity(intents)
        }

        binding.termsandconditions.setOnClickListener {
            // ------- Analytics call ----------------
            val bundle = Bundle()
            bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "TERMS&CONDITIONS")
            MyFirebaseApp.mFirebaseAnalytics.logEvent(
                FirebaseAnalytics.Event.SELECT_ITEM,
                bundle
            )
            //----------------------------------------
            val uris = Uri.parse(getString(R.string.termsandconditions))
            val intents = Intent(Intent.ACTION_VIEW, uris)
            val b = Bundle()
            b.putBoolean("new_window", true)
            intents.putExtras(b)
            requireContext().startActivity(intents)
        }
        return binding.root
    }

    fun Fragment.hideSoftKeyboard() {
        val activity = activity ?: return // Get the associated activity, return if null
        val inputMethodManager =
            activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        // Find the currently focused view, so we can grab the correct window token from it.
        var view = activity.currentFocus
        // If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = View(activity)
        }
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

    override fun onBackPressed(): Boolean {
        return false
    }

    fun finish() {
        val fragment = DashboardEvent()
        Handler(Looper.getMainLooper()).postDelayed({
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit()
        }, 500)
    }

    private fun displayErrorMsg(message: String) {
        Toast.makeText(
            requireContext(),
            message,
            Toast.LENGTH_LONG
        ).show()
    }

    companion object {
        const val SCREEN_NAME = "Settings"
        const val TAG = "Settings"
    }
}
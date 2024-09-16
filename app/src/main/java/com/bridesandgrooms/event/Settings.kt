package com.bridesandgrooms.event

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
import android.util.Log
import android.widget.ArrayAdapter
import com.bridesandgrooms.event.Model.User
import com.bridesandgrooms.event.databinding.SettingsBinding
import com.google.firebase.analytics.FirebaseAnalytics
//import kotlinx.android.synthetic.main.settings.*
//import kotlinx.android.synthetic.main.settings.view.*
import kotlinx.coroutines.launch

class Settings : Fragment(), IOnBackPressed {

    //lateinit var usersession: User
    private lateinit var inf: SettingsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        inf = DataBindingUtil.inflate(inflater, R.layout.settings, container, false)

        //Get information about the logged user
        //usersession = userdbhelper.getUser(userdbhelper.getUserKey())!!
        var usersession = User().getUser(requireContext())

        //Load the spinner with whatever comes from the user role
        inf.textinput.setText(usersession.shortname)

        val roles = arrayOf("Bride", "Groom") // Example roles
        val roleAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, roles)
        inf.roleAutocomplete.setAdapter(roleAdapter)

        // Now using post to safely set selection
        inf.roleAutocomplete.post {
            val position = when (usersession.role) {
                "Bride" -> 0
                "Groom" -> 1
                else -> 0
            }

            // Ensure the adapter is not null and has enough items
            if (inf.roleAutocomplete.adapter != null && inf.roleAutocomplete.adapter.count > position) {
                val item = inf.roleAutocomplete.adapter.getItem(position) as String
                inf.roleAutocomplete.setText(item, false)  // Set text without filtering
            }
        }

        val countries = arrayOf("MX", "CL", "PE") // Example countries
        val countryAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, countries)
        inf.countryAutocomplete.setAdapter(countryAdapter)

        inf.countryAutocomplete.post {
            val position2 = when (usersession.country) {
                "MX" -> 0
                "CL" -> 1
                "PE" -> 2
                else -> 0
            }
            if (inf.countryAutocomplete.adapter != null && inf.countryAutocomplete.adapter.count > position2) {
                inf.countryAutocomplete.setSelection(position2)
            }
        }

        val distances = arrayOf("miles", "kilometers") // Example distances
        val distanceAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, distances)
        inf.distanceAutocomplete.setAdapter(distanceAdapter)

        inf.distanceAutocomplete.post {
            val position3 = when(usersession.distanceunit){
                "miles" -> 0
                "kilometers" -> 1
                else -> 0
            }
            if (inf.distanceAutocomplete.adapter != null && inf.distanceAutocomplete.adapter.count > position3) {
                inf.distanceAutocomplete.setSelection(position3)
            }
        }

        //Load the spinner with the language selected for the user
//        val language = when (usersession.language) {
//            "en" -> 0
//            "es" -> 1
//            else -> 0
//        }
//        inf.spinner4.setSelection(language)

        inf.textinput.setOnClickListener()
        {
            inf.textinput.error = null
        }
        //-----------------------------------------------------------------------------------
        inf.budgetinput.setText(usersession.eventbudget.toString())
        inf.budgetinput.setOnClickListener() {
            inf.budgetinput.error = null
        }

        inf.budgetinput.onFocusChangeListener = object : View.OnFocusChangeListener {
            override fun onFocusChange(v: View?, hasFocus: Boolean) {
                hideSoftKeyboard()
            }
        }

        inf.numberguestsinput.setText(usersession.numberguests.toString())
        inf.numberguestsinput.setOnClickListener() {
            inf.numberguestsinput.error = null
        }


        //When the user selects via the spinner Bride or Groom, that will be saved in his/her profile
        inf.roleAutocomplete.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                usersession.role = when (p2) {
                    0 -> "Bride"
                    1 -> "Groom"
                    else -> "Bride"
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        }

        inf.countryAutocomplete.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                usersession.country = when (p2) {
                    0 -> "MX"
                    1 -> "CL"
                    2 -> "PE"
                    else -> "MX"
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        }

        inf.distanceAutocomplete.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                usersession.distanceunit = when (p2) {
                    0 -> "miles"
                    1 -> "kilometers"
                    else -> "miles"
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        }

        //When the user selects via the spinner English or Spanish, that will be saved in his/her profile
//        inf.spinner4.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
//
//            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
//                usersession.language = when (inf.spinner4.selectedItemPosition) {
//                    0 -> "en"
//                    1 -> "es"
//                    else -> "en"
//                }
//                // ------- Analytics call ----------------
//                val bundle = Bundle()
//                bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "LANGUAGE")
//                bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, usersession.language)
//                bundle.putString(FirebaseAnalytics.Param.SCREEN_NAME, javaClass.simpleName)
//                MyFirebaseApp.mFirebaseAnalytics.logEvent(
//                    FirebaseAnalytics.Event.SELECT_ITEM,
//                    bundle
//                )
//                //----------------------------------------
//            }
//
//            override fun onNothingSelected(p0: AdapterView<*>?) {
//                TODO("Not yet implemented")
//            }
//        }

        inf.settingsbutton.setOnClickListener()
        {
            var inputvalflag = true
            if (inf.textinput.text.toString().isEmpty()) {
                inf.textinput.error = getString(R.string.error_tasknameinput)
                inputvalflag = false
            } else {
                usersession.shortname = inf.textinput.text.toString()
            }
            if (inf.budgetinput.text.toString().isEmpty()) {
                inf.budgetinput.error = getString(R.string.error_budgetrequired)
                inputvalflag = false
            } else {
                usersession.eventbudget = inf.budgetinput.text.toString()
            }
            if (inf.numberguestsinput.text.toString().isEmpty()) {
                inf.numberguestsinput.error = getString(R.string.error_numberguestsrequired)
                inputvalflag = false
            } else {
                usersession.numberguests = inf.numberguestsinput.text.toString().toInt()
            }
            if (inputvalflag) {
                //savesettings()
                lifecycleScope.launch {
                    try {
                        editUser(requireContext(), usersession)
                    } catch (e: UserEditionException){
                        AnalyticsManager.getInstance().trackError(SCREEN_NAME,e.message.toString(), "EditUser", e.stackTraceToString())
                        Log.e(TAG, e.message.toString())
                    }
                }
            }
        }

        inf.privacypolicy.setOnClickListener {
            // ------- Analytics call ----------------
            val bundle = Bundle()
            bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "PRIVACYPOLICY")
            MyFirebaseApp.mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_ITEM, bundle)
            //----------------------------------------

            val uris = Uri.parse(getString(R.string.privacypolicy))
            val intents = Intent(Intent.ACTION_VIEW, uris)
            val b = Bundle()
            b.putBoolean("new_window", true)
            intents.putExtras(b)
            requireContext().startActivity(intents)
        }

        inf.termsandconditions.setOnClickListener {
            // ------- Analytics call ----------------
            val bundle = Bundle()
            bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "TERMS&CONDITIONS")
            MyFirebaseApp.mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_ITEM, bundle)
            //----------------------------------------

            val uris = Uri.parse(getString(R.string.termsandconditions))
            val intents = Intent(Intent.ACTION_VIEW, uris)
            val b = Bundle()
            b.putBoolean("new_window", true)
            intents.putExtras(b)
            requireContext().startActivity(intents)
        }
        return inf.root
    }

    fun Fragment.hideSoftKeyboard() {
        val activity = activity ?: return // Get the associated activity, return if null

        val inputMethodManager = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
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

    companion object {
        const val SCREEN_NAME = "Settings"
        const val TAG = "Settings"
    }
}
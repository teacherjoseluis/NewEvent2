package com.bridesandgrooms.event

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.bridesandgrooms.event.Functions.editUser
import com.bridesandgrooms.event.Functions.userdbhelper
import com.bridesandgrooms.event.Model.MyFirebaseApp
import com.bridesandgrooms.event.Model.User
import com.google.firebase.analytics.FirebaseAnalytics
import kotlinx.android.synthetic.main.settings.*
import kotlinx.android.synthetic.main.settings.view.*
import kotlinx.coroutines.launch

class Settings : Fragment(), IOnBackPressed {

    lateinit var usersession: User

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val inf = inflater.inflate(R.layout.settings, container, false)

        //Get information about the logged user
        usersession = userdbhelper.getUser(userdbhelper.getUserKey())

        //Load the spinner with whatever comes from the user role
        inf.textinput.setText(usersession.shortname)
        val position = when (usersession.role) {
            "Bride" -> 0
            "Groom" -> 1
            else -> 0
        }
        inf.spinner.setSelection(position)

        //Load the spinner with the language selected for the user
//        val language = when (usersession.language) {
//            "en" -> 0
//            "es" -> 1
//            else -> 0
//        }
//        inf.spinner4.setSelection(language)

        inf.textinput.setOnClickListener()
        {
            textinput.error = null
        }

        //When the user selects via the spinner Bride or Groom, that will be saved in his/her profile
        inf.spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                usersession.role = when (inf.spinner.selectedItemPosition) {
                    0 -> "Bride"
                    1 -> "Groom"
                    else -> "Bride"
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
            if (inputvalflag) {
                //savesettings()
                lifecycleScope.launch {
                    editUser(requireContext(), usersession)
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
        return inf
    }

    override fun onBackPressed(): Boolean {
        return false
    }
}
package com.example.newevent2

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.newevent2.Functions.editUser
import com.example.newevent2.Functions.getUserSession
import com.example.newevent2.Model.MyFirebaseApp
import com.example.newevent2.Model.User
import com.example.newevent2.Model.UserModel
import com.google.firebase.analytics.FirebaseAnalytics
import kotlinx.android.synthetic.main.settings.*
import kotlinx.android.synthetic.main.settings.view.*

class Settings : Fragment() {

    lateinit var userEntity: UserModel
    lateinit var usersession: User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val inf = inflater.inflate(R.layout.settings, container, false)

        usersession = getUserSession(context!!)

        inf.textinput.setText(usersession.shortname)
        val position = when (usersession.role) {
            "Bride" -> 0
            "Groom" -> 1
            else -> 0
        }
        inf.spinner.setSelection(position)

        val language = when (usersession.language) {
            "en" -> 0
            "es" -> 1
            else -> 0
        }
        inf.spinner4.setSelection(language)

        inf.textinput.setOnClickListener()
        {
            textinput.error = null
        }

        inf.spinner.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
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
        })

        inf.spinner4.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                usersession.language = when (inf.spinner4.selectedItemPosition) {
                    0 -> "en"
                    1 -> "es"
                    else -> "en"
                }
                // ------- Analytics call ----------------
                val bundle = Bundle()
                bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "LANGUAGE")
                bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, usersession.language)
                bundle.putString(FirebaseAnalytics.Param.SCREEN_NAME, javaClass.simpleName)
                MyFirebaseApp.mFirebaseAnalytics!!.logEvent(
                    FirebaseAnalytics.Event.SELECT_ITEM,
                    bundle
                )
                //----------------------------------------
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        })

        inf.settingsbutton.setOnClickListener()
        {
            var inputvalflag = true
            if (inf.textinput.text.toString().isEmpty()) {
                inf.textinput.error = "Name is required!"
                inputvalflag = false
            }
            if (inputvalflag) {
                //savesettings()
                editUser(context!!, usersession)
            }
        }

        inf.privacypolicy.setOnClickListener {
            // ------- Analytics call ----------------
            val bundle = Bundle()
            bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "PRIVACYPOLICY")
            MyFirebaseApp.mFirebaseAnalytics!!.logEvent(FirebaseAnalytics.Event.SELECT_ITEM, bundle)
            //----------------------------------------

            val uris = Uri.parse("https://brides-grooms.flycricket.io/privacy.html")
            val intents = Intent(Intent.ACTION_VIEW, uris)
            val b = Bundle()
            b.putBoolean("new_window", true)
            intents.putExtras(b)
            context!!.startActivity(intents)
        }

        inf.termsandconditions.setOnClickListener {
            // ------- Analytics call ----------------
            val bundle = Bundle()
            bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "TERMS&CONDITIONS")
            MyFirebaseApp.mFirebaseAnalytics!!.logEvent(FirebaseAnalytics.Event.SELECT_ITEM, bundle)
            //----------------------------------------

            val uris = Uri.parse("https://brides-grooms.flycricket.io/terms.html")
            val intents = Intent(Intent.ACTION_VIEW, uris)
            val b = Bundle()
            b.putBoolean("new_window", true)
            intents.putExtras(b)
            context!!.startActivity(intents)
        }

        return inf
    }

//    private fun savesettings() {
//        try {
////            userEntity = UserModel(usersession.key)
////            userEntity.editUser(usersession)
//            editUser(context!!, usersession)
//            usersession.saveUserSession(context!!)
//            Toast.makeText(context, "Settings were saved successully", Toast.LENGTH_LONG).show()
//        } catch (e: Exception) {
//            Toast.makeText(
//                context,
//                "There was an error trying to save the settings ${e.message}",
//                Toast.LENGTH_LONG
//            ).show()
//        }
//    }
}
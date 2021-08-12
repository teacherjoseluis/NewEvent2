package com.example.newevent2

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.newevent2.Functions.getUserSession
import com.example.newevent2.Model.User
import com.example.newevent2.Model.UserModel
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
                savesettings()
            }
        }
        return inf
    }

    private fun savesettings() {
        try {
            userEntity = UserModel(usersession.key)
            userEntity.editUser(usersession)
            usersession.saveUserSession(context!!)
            Toast.makeText(context, "Settings were saved successully", Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            Toast.makeText(
                context,
                "There was an error trying to save the settings ${e.message}",
                Toast.LENGTH_LONG
            ).show()
        }
    }
}
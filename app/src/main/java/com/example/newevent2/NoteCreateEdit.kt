package com.example.newevent2

import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.newevent2.Model.MyFirebaseApp
import com.example.newevent2.Model.Note
import com.example.newevent2.Model.NoteDBHelper
import com.google.firebase.analytics.FirebaseAnalytics
import dev.sasikanth.colorsheet.ColorSheet
import dev.sasikanth.colorsheet.utils.ColorSheetUtils
import kotlinx.android.synthetic.main.new_note.*
import java.sql.Date
import java.sql.Time
import java.text.SimpleDateFormat


class NoteCreateEdit : AppCompatActivity() {

    private lateinit var noteitem: Note
    private lateinit var loadingview: View
    private lateinit var withdataview: View
    lateinit var mContext: Context
    private var colorSelected = ""

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.new_note)

        // Toolbar
        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar!!.setHomeAsUpIndicator(R.drawable.icons8_left_24)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        val apptitle = findViewById<TextView>(R.id.appbartitle)
        apptitle.text = "New Note"

        loadingview = findViewById(R.id.loadingscreen)
        withdataview = findViewById(R.id.withdata)


        val extras = intent.extras
        noteitem = if (extras!!.containsKey("note")) {
            intent.getParcelableExtra("note")!!
        } else {
            Note()
        }

        val noteid = noteitem.noteid

        if (noteid != "") {
            notetitle.setText(noteitem.title)
            editTextTextMultiLine.setText(noteitem.body)
            colorSelected = noteitem.color
        }

        notetitle.setOnClickListener {
            notetitle.error = null
        }

        editTextTextMultiLine.setOnClickListener {
            editTextTextMultiLine.error = null
        }

        btnSelectColorBg.setOnClickListener {
            val colors = resources.getIntArray(R.array.rainbow)
            ColorSheet().colorPicker(
                colors = colors,
                noColorOption = true,
                listener = { color ->
                    it.focusSearch(View.FOCUS_DOWN)?.requestFocus()
//                    btnSelectColorBg.backgroundTintList = ColorStateList.valueOf(color)
                    colorSelected = ColorSheetUtils.colorToHex(color)
                })
                .show(supportFragmentManager)
        }

        button.setOnClickListener {
            var inputvalflag = true
            if (notetitle.text.toString().isEmpty()) {
                notetitle.error = "Note name is required!"
                inputvalflag = false
            }
            if (editTextTextMultiLine.text.toString().isEmpty()) {
                editTextTextMultiLine.error = "Note body is required!"
                inputvalflag = false
            }
            if (inputvalflag) {
                savenote()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.notes_menu, menu)
        val shareitem = menu.findItem(R.id.share_note)

        shareitem.setOnMenuItemClickListener {
            if (notetitle.text.toString().isEmpty() && editTextTextMultiLine.text.toString()
                    .isEmpty()
            ) {
                Toast.makeText(
                    this,
                    "Not possible to share the Note as either the title or body are missing",
                    Toast.LENGTH_LONG
                ).show()
                return@setOnMenuItemClickListener true
            } else {
                // ------- Analytics call ----------------
                val bundle = Bundle()
                bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "SHARENOTE")
                bundle.putString(FirebaseAnalytics.Param.SCREEN_NAME, javaClass.simpleName)
                MyFirebaseApp.mFirebaseAnalytics!!.logEvent(FirebaseAnalytics.Event.SELECT_ITEM, bundle)
                //----------------------------------------

                val intent = Intent(Intent.ACTION_SEND)
                val shareBody =
                    "Title:" + notetitle.text.toString() + " Note:" + editTextTextMultiLine.text.toString()
                intent.type = "text/plain"
                intent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Note")
                intent.putExtra(Intent.EXTRA_TEXT, shareBody)
                startActivity(Intent.createChooser(intent, "Share using"))
                return@setOnMenuItemClickListener true
            }
        }

//        shareitem.setOnMenuItemClickListener {
//            val intent = Intent(Intent.ACTION_SEND)
//            val shareBody =
//                "Title:" + notetitle.text.toString() + " Note:" + editTextTextMultiLine.text.toString()
//            intent.type = "text/plain"
//            intent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Note")
//            intent.putExtra(Intent.EXTRA_TEXT, shareBody)
//            startActivity(Intent.createChooser(intent, "Share using"))
//            return@setOnMenuItemClickListener true
//        }
        return true
    }

    private fun savenote() {
        val timestamp = Time(System.currentTimeMillis())
        val notedatetime = Date(timestamp.time)
        val sdf = SimpleDateFormat("MM/dd/yyyy h:mm:ss a")

        loadingview.visibility = ConstraintLayout.VISIBLE
        withdataview.visibility = ConstraintLayout.GONE

        noteitem.title = notetitle.text.toString()
        noteitem.body = editTextTextMultiLine.text.toString()
        noteitem.color = colorSelected
        noteitem.lastupdateddatetime = sdf.format(notedatetime)

        val notedb = NoteDBHelper(this)
        if (noteitem.noteid == "") {
            try {
                notedb.insert(noteitem)
                MyNotes.notescreated_flag = 1
                Toast.makeText(this, "Note was created successully", Toast.LENGTH_LONG).show()
            } catch (e: Exception) {
                Toast.makeText(
                    this,
                    "There was an error trying to create the note ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        } else if (noteitem.noteid != "") {
            try {
                notedb.update(noteitem)
                MyNotes.notescreated_flag = 1
                Toast.makeText(this, "Note was edited successully", Toast.LENGTH_LONG).show()
            } catch (e: Exception) {
                Toast.makeText(
                    this,
                    "There was an error trying to edit the note ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

        loadingview.visibility = ConstraintLayout.GONE
        withdataview.visibility = ConstraintLayout.VISIBLE
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}
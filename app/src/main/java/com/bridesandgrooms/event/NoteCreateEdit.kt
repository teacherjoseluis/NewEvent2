package com.bridesandgrooms.event

//import dev.sasikanth.colorsheet.ColorSheet
//import dev.sasikanth.colorsheet.utils.ColorSheetUtils
//import kotlinx.android.synthetic.main.new_note.*

import android.content.Context
import android.content.Intent
import android.graphics.PorterDuff
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil
import Application.MyFirebaseApp
import com.bridesandgrooms.event.Model.Note
import com.bridesandgrooms.event.Model.NoteDBHelper
import com.bridesandgrooms.event.databinding.NewNoteBinding
import com.google.firebase.analytics.FirebaseAnalytics
import com.madrapps.pikolo.listeners.SimpleColorSelectionListener
import java.lang.String
import java.sql.Date
import java.sql.Time
import java.text.SimpleDateFormat


class NoteCreateEdit : AppCompatActivity() {

    private lateinit var noteitem: Note
    private lateinit var loadingview: View
    private lateinit var withdataview: View
    private lateinit var binding: NewNoteBinding

    lateinit var mContext: Context
    private var colorSelected = ""

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.new_note)

        // Toolbar
        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar!!.setHomeAsUpIndicator(R.drawable.icons8_left_24)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        val apptitle = findViewById<TextView>(R.id.appbartitle)
        apptitle.text = getString(R.string.new_note)

        loadingview = findViewById(R.id.loadingscreen)
        withdataview = findViewById(R.id.withdata)


        val extras = intent.extras
        noteitem = if (extras?.containsKey("note") == true) {
            intent.getParcelableExtra("note")!!
        } else {
            Note()
        }

        val noteid = noteitem.noteid

        if (noteid != "") {
            binding.notetitle.setText(noteitem.title)
            binding.editTextTextMultiLine.setText(noteitem.body)
            colorSelected = noteitem.color
        }

        binding.notetitle.setOnClickListener {
            binding.notetitle.error = null
        }

        binding.editTextTextMultiLine.setOnClickListener {
            binding.editTextTextMultiLine.error = null
        }

        binding.colorPicker.setColorSelectionListener(object : SimpleColorSelectionListener() {
            override fun onColorSelected(color: Int) {
                // Do whatever you want with the color
                colorSelected = String.format("#%06X", 0xFFFFFF and color)
                binding.editTextTextMultiLine.background.setColorFilter(color, PorterDuff.Mode.MULTIPLY)
            }
        })

        binding.button.setOnClickListener {
            var inputvalflag = true
            if (binding.notetitle.text.toString().isEmpty()) {
                binding.notetitle.error = getString(R.string.error_tasknameinput)
                inputvalflag = false
            }
            if (binding.editTextTextMultiLine.text.toString().isEmpty()) {
                binding.editTextTextMultiLine.error = getString(R.string.error_bodyinput)
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
            if (binding.notetitle.text.toString().isEmpty() && binding.editTextTextMultiLine.text.toString()
                    .isEmpty()
            ) {
                Toast.makeText(
                    this,
                    getString(R.string.errorsharenote),
                    Toast.LENGTH_LONG
                ).show()
                return@setOnMenuItemClickListener true
            } else {
                // ------- Analytics call ----------------
                val bundle = Bundle()
                bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "SHARENOTE")
                bundle.putString(FirebaseAnalytics.Param.SCREEN_NAME, javaClass.simpleName)
                MyFirebaseApp.mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_ITEM, bundle)
                //----------------------------------------

                val intent = Intent(Intent.ACTION_SEND)
                val shareBody =
                    getString(R.string.title)+": " + binding.notetitle.text.toString() + getString(R.string.note)+": " + binding.editTextTextMultiLine.text.toString()
                intent.type = "text/plain"
                intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.note))
                intent.putExtra(Intent.EXTRA_TEXT, shareBody)
                startActivity(Intent.createChooser(intent, getString(R.string.shareusing)))
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

        noteitem.title = binding.notetitle.text.toString()
        noteitem.body = binding.editTextTextMultiLine.text.toString()
        noteitem.color = colorSelected
        noteitem.lastupdateddatetime = sdf.format(notedatetime)

        val notedb = NoteDBHelper(this)
        if (noteitem.noteid == "") {
            try {
                notedb.insert(noteitem)
                MyNotes.notescreated_flag = 1
                Toast.makeText(this, getString(R.string.successaddnote), Toast.LENGTH_LONG).show()
            } catch (e: Exception) {
                val errormsg = getString(R.string.erroraddnote)
                errormsg.plus(e.message)
                Toast.makeText(
                    this,
                    errormsg,
                    Toast.LENGTH_LONG
                ).show()
            }
        } else if (noteitem.noteid != "") {
            try {
                notedb.update(noteitem)
                MyNotes.notescreated_flag = 1
                Toast.makeText(this, getString(R.string.successeditnote), Toast.LENGTH_LONG).show()
            } catch (e: Exception) {
                val errormsg = getString(R.string.erroreditnote)
                errormsg.plus(e.message)
                Toast.makeText(
                    this,
                    errormsg,
                    Toast.LENGTH_LONG
                ).show()
            }
        }
        //Thread.sleep(1500)
        finish()
    }

    override fun finish() {
        val returnintent = Intent()
        setResult(RESULT_OK, returnintent)
        super.finish()
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}

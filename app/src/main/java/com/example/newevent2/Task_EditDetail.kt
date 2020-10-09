package com.example.newevent2

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.newevent2.ui.dialog.DatePickerFragment
import com.google.android.material.chip.Chip
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.task_editdetail.*

class Task_EditDetail : AppCompatActivity() {

    lateinit var taskitem: Task

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.task_editdetail)
        taskitem = Task()

        val intent = intent
        val taskkey = intent.getStringExtra("taskkey").toString()
        val eventid = intent.getStringExtra("eventid").toString()

        val taskname = intent.getStringExtra("name").toString()
        val taskdateextra = intent.getStringExtra("date").toString()
        val taskcategory = intent.getStringExtra("category").toString()
        val taskbudgetextra = intent.getStringExtra("budget").toString()

        if (taskkey != "" && eventid != "") {

            val tasktitle = findViewById<TextView>(R.id.tkname)
            tasktitle.text = taskname

            val taskdate = findViewById<TextView>(R.id.tkdate)
            taskdate.text = taskdateextra

            var selectedchip: TextView
            selectedchip = findViewById<TextView>(R.id.chip)

            if (taskcategory == "flowers") {
                selectedchip = findViewById<TextView>(R.id.chip)
            }

            if (taskcategory == "venue") {
                selectedchip = findViewById<TextView>(R.id.chip2)
            }

            if (taskcategory == "photo") {
                selectedchip = findViewById<TextView>(R.id.chip3)
            }

            if (taskcategory == "entertainment") {
                selectedchip = findViewById<TextView>(R.id.chip4)
            }

            if (taskcategory == "transport") {
                selectedchip = findViewById<TextView>(R.id.chip5)
            }

            if (taskcategory == "ceremony") {
                selectedchip = findViewById<TextView>(R.id.chip6)
            }

            if (taskcategory == "accesories") {
                selectedchip = findViewById<TextView>(R.id.chip7)
            }

            if (taskcategory == "beauty") {
                selectedchip = findViewById<TextView>(R.id.chip8)
            }

            if (taskcategory == "food") {
                selectedchip = findViewById<TextView>(R.id.chip9)
            }

            if (taskcategory == "guests") {
                selectedchip = findViewById<TextView>(R.id.chip10)
            }

            if (taskcategory == "none") {
            }

            groupedit.check(selectedchip.id)
            selectedchip.isSelected = true

            val taskbudget = findViewById<TextView>(R.id.tkbudget)
            taskbudget.text = taskbudgetextra

        }

        //---------------------------------------------------------------------------------//
        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar!!.setHomeAsUpIndicator(R.drawable.icons8_left_24)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        val apptitle = findViewById<TextView>(R.id.appbartitle)
        apptitle.text = "Task Detail"
        //-------------------------------------------------------------------------------//

        groupedit.isSingleSelection = true

        tkdate.setOnClickListener()
        {
            showDatePickerDialog()
        }

        button2.setOnClickListener()
        {
            saveTask(taskkey, eventid)
        }

    }

    private fun showDatePickerDialog() {
        val newFragment =
            DatePickerFragment.newInstance(DatePickerDialog.OnDateSetListener { _, year, month, day ->
                // +1 because January is zero
                val selectedDate = day.toString() + " / " + (month + 1) + " / " + year
                tkdate.setText(selectedDate)
            })

        newFragment.show(supportFragmentManager, "datePicker")
    }


    private fun saveTask(taskkey: String, eventid: String) {
        val database = FirebaseDatabase.getInstance()
        val myRef = database.reference
        val postRef = myRef.child("User").child("Event").child(eventid).child("Task").child(taskkey)
        var category = ""

        if (groupedit.checkedChipId != null) {
            val id = groupedit.checkedChipId
            val chipselected = groupedit.findViewById<Chip>(id)
            val chiptextvalue = chipselected.text.toString()
            category = when (chiptextvalue) {
                "Flowers & Deco" -> "flowers"
                "Venue" -> "venue"
                "Photo & Video" -> "photo"
                "Entertainment" -> "entertainment"
                "Transportation" -> "transport"
                "Ceremony" -> "ceremony"
                "Attire & Accessories" -> "accesories"
                "Health & Beauty" -> "beauty"
                "Food & Drink" -> "food"
                "Guests" -> "guests"
                else -> "none"
            }

        }

        postRef.child("name").setValue(tkname.text.toString())
        postRef.child("budget").setValue(tkbudget.text.toString())
        postRef.child("date").setValue(tkdate.text.toString())
        postRef.child("category").setValue(category)
        this.onBackPressed()
    }
}

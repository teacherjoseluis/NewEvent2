package com.example.newevent2

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.newevent2.ui.dialog.DatePickerFragment
import com.google.android.material.chip.Chip
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.new_task_taskdetail.*
import kotlinx.android.synthetic.main.new_task_taskdetail.view.*
import kotlinx.android.synthetic.main.task_editdetail.*
import kotlinx.android.synthetic.main.task_editdetail.button2
import kotlinx.android.synthetic.main.task_editdetail.tkbudget
import kotlinx.android.synthetic.main.task_editdetail.tkdate
import kotlinx.android.synthetic.main.task_editdetail.tkname

class Task_EditDetail : AppCompatActivity() {

    //var eventkey = ""
    var taskstatus=""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.task_editdetail)

       val taskkey = intent.getStringExtra("taskkey").toString()
        //eventkey = intent.getStringExtra("eventid").toString()
        val taskname = intent.getStringExtra("name").toString()
        val taskdateextra = intent.getStringExtra("date").toString()
        val taskcategory = intent.getStringExtra("category").toString()
        val taskbudgetextra = intent.getStringExtra("budget").toString()
        taskstatus = intent.getStringExtra("status").toString()

        //---------------------------------------------------------------------------------//
        //setSupportActionBar(findViewById(R.id.toolbar))
        //supportActionBar!!.setHomeAsUpIndicator(R.drawable.icons8_left_24)
        //supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        val apptitle = findViewById<TextView>(R.id.appbartitle)
        apptitle.text = "Task Detail"
        //-------------------------------------------------------------------------------//

        val tasktitle = findViewById<TextView>(R.id.tkname)
        tasktitle.text = taskname

        val taskdate = findViewById<TextView>(R.id.tkdate)
        taskdate.text = taskdateextra

        val selectedchip = when (taskcategory) {
                "flowers" -> findViewById<TextView>(R.id.chip)
                "venue" -> findViewById<TextView>(R.id.chip2)
                "photo" -> findViewById<TextView>(R.id.chip3)
                "entertainment" -> findViewById<TextView>(R.id.chip4)
                "transport" -> findViewById<TextView>(R.id.chip5)
                "ceremony" -> findViewById<TextView>(R.id.chip6)
                "accesories" -> findViewById<TextView>(R.id.chip7)
                "beauty" -> findViewById<TextView>(R.id.chip8)
                "food" -> findViewById<TextView>(R.id.chip9)
                "guests" -> findViewById<TextView>(R.id.chip10)
                else -> findViewById<TextView>(R.id.chip)
            }

        selectedchip.isSelected = true
        groupedit.check(selectedchip.id)
        groupedit.isSingleSelection = true

        val taskbudget = findViewById<TextView>(R.id.tkbudget)
        taskbudget.text = taskbudgetextra

        tkname.setOnClickListener {
            tkname.error = null
        }

        tkdate.setOnClickListener {
            tkdate.error = null
            showDatePickerDialog()
        }

        button2.setOnClickListener()
        {
            var inputvalflag = true
            if (tkname.text.toString().isEmpty()) {
                tkname.error = "Task name is required!"
                inputvalflag = false
            }
            if (tkdate.text.toString().isEmpty()) {
                tkdate.error = "Task due date is required!"
                inputvalflag = false
            }
            if (groupedit.checkedChipId == -1) {
                Toast.makeText(this, "Category is required!", Toast.LENGTH_SHORT).show()
                inputvalflag = false
            }
            if (inputvalflag) {
                saveTask(taskkey)
                onBackPressed()
                //this.onBackPressed()
            }
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

    private fun saveTask(taskkey: String) {
        //val database = FirebaseDatabase.getInstance()
        //val myRef = database.reference
        //val postRef = myRef.child("User").child("Event").child(eventid).child("Task").child(taskkey)
        var taskcategory = ""


        val id = groupedit.checkedChipId
        val chipselected = groupedit.findViewById<Chip>(id)
        val chiptextvalue = chipselected.text.toString()
        taskcategory = when (chiptextvalue) {
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

        val taskentity = TaskEntity().apply {
            key= taskkey
            name = tkname.text.toString()
            budget = tkbudget.text.toString()
            date = tkdate.text.toString()
            category = taskcategory
            //this.eventid = eventkey
            status=taskstatus

        }
        taskentity.editTask(this)
    }
}
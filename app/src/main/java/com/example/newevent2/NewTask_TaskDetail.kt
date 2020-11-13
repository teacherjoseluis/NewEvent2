package com.example.newevent2

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.newevent2.ui.dialog.DatePickerFragment
import com.google.android.material.chip.Chip
import kotlinx.android.synthetic.main.new_task_taskdetail.button2
import kotlinx.android.synthetic.main.new_task_taskdetail.tkbudget
import kotlinx.android.synthetic.main.new_task_taskdetail.tkdate
import kotlinx.android.synthetic.main.new_task_taskdetail.tkname
import kotlinx.android.synthetic.main.task_editdetail.*


class NewTask_TaskDetail : AppCompatActivity() {
    private var eventkey: String = ""
    private var taskcategory: String = ""

    private var chiptextvalue: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.task_editdetail)

        eventkey = intent.getStringExtra("eventkey").toString()

        groupedit.isSingleSelection = true

        tkname.setOnClickListener {
            tkname.error = null
        }

        tkdate.setOnClickListener {
            tkdate.error = null
            showDatePickerDialog()
        }

        button2.setOnClickListener {
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
                saveTask()
                onBackPressed()
            }
        }
    }


    private fun showDatePickerDialog() {
        val newFragment =
            DatePickerFragment.newInstance(DatePickerDialog.OnDateSetListener { _, year, month, day ->
                // +1 because January is zero
                val selectedDate = day.toString() + "/" + (month + 1) + "/" + year
                tkdate.setText(selectedDate)
            })

        newFragment.show(supportFragmentManager, "datePicker")
    }

    private fun saveTask() {
            val id = groupedit.checkedChipId
            val chipselected = groupedit.findViewById<Chip>(id)
            chiptextvalue = chipselected.text.toString()
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
            name = tkname.text.toString()
            budget = tkbudget.text.toString()
            date = tkdate.text.toString()
            category = taskcategory
            eventid = eventkey
        }
        taskentity.addTask()
    }
}
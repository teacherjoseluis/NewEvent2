package com.example.newevent2

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.DatePicker
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.newevent2.Functions.addTask
import com.example.newevent2.Functions.editTask
import com.example.newevent2.Functions.validateOldDate
import com.example.newevent2.Model.Task
import com.example.newevent2.Model.TaskDBHelper
import com.example.newevent2.Model.TaskModel
import com.example.newevent2.Model.User
import com.example.newevent2.Model.UserModel
import com.example.newevent2.ui.TextValidate
import com.example.newevent2.ui.dialog.DatePickerFragment
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import kotlinx.android.synthetic.main.task_editdetail.*
import kotlinx.android.synthetic.main.task_editdetail.taskdate
import kotlinx.android.synthetic.main.task_editdetail.taskname
import java.util.*

class TaskCreateEdit() : AppCompatActivity() {

    private lateinit var taskitem: Task

    //var taskmodel = TaskModel()
    //lateinit var taskdbhelper: TaskDBHelper
    //lateinit var usermodel: UserModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.task_editdetail)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.setHomeAsUpIndicator(R.drawable.icons8_left_24)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        val extras = intent.extras
        taskitem = if (extras!!.containsKey("task")) {
            intent.getParcelableExtra("task")!!
        } else {
            Task()
        }

        val taskid = taskitem.key

        if (taskid != "") {
            val taskmodel = TaskModel()
            val user = com.example.newevent2.Functions.getUserSession(applicationContext!!)
            taskmodel.getTaskdetail(user.key, user.eventid, taskid, object :
                TaskModel.FirebaseSuccessTask {
                @RequiresApi(Build.VERSION_CODES.O)
                override fun onTask(task: Task) {
                    taskname.setText(task.name)
                    taskdate.setText(task.date)
                    taskbudget.setText(task.budget)

                    taskitem.key = task.key
                    taskitem.budget = task.budget
                    taskitem.category = task.category
                    taskitem.date = task.date
                    taskitem.name = task.name
                    taskitem.createdatetime = task.createdatetime
                    taskitem.status = task.status
                }
            })
        }

        taskname.onFocusChangeListener = View.OnFocusChangeListener { _, p1 ->
            if (!p1) {
                val validationmessage = TextValidate(taskname).namefieldValidate()
                if (validationmessage != "") {
                    taskname.error = "Error in Task name: $validationmessage"
                }
            }
        }

        taskbudget.setOnClickListener {
            taskbudget.error = null
        }

        taskdate.setOnClickListener {
            taskdate.error = null
            showDatePickerDialog()
        }

        val chipgroupedit = findViewById<ChipGroup>(R.id.groupedittask)

        // Create chips and select the one matching the category
        val list = ArrayList<Category>(EnumSet.allOf(Category::class.java))
        for (category in list) {
            val chip = Chip(this)
            chip.text = category.en_name
            chip.isClickable = true
            chip.isCheckable = true
            chipgroupedit.addView(chip)
            if (taskitem.category == category.code) {
                chip.isSelected = true
                chipgroupedit.check(chip.id)
            }
        }

        savebuttontask.setOnClickListener {
            var inputvalflag = true
            taskname.clearFocus()
            if (taskname.text.toString().isEmpty()) {
                taskname.error = "Error in Task name: Task name is required!"
                inputvalflag = false
            } else {
                val validationmessage = TextValidate(taskname).namefieldValidate()
                if (validationmessage != "") {
                    taskname.error = "Error in Task name: $validationmessage"
                    inputvalflag = false
                }
            }

            taskdate.clearFocus()
            if (taskdate.text.toString().isEmpty()) {
                taskdate.error = "Task date is required!"
                inputvalflag = false
            }
            taskbudget.clearFocus()
            if (taskbudget.text.toString().isEmpty()) {
                taskbudget.error = "Task budget is required!"
                inputvalflag = false
            }
            if (groupedittask.checkedChipId == -1) {
                Toast.makeText(this, "Category is required!", Toast.LENGTH_SHORT).show()
                inputvalflag = false
            }
            if (inputvalflag) {
                saveTask()
            }
        }
    }

    private fun getCategory(): String {
        var mycategorycode = ""
        val categoryname = groupedittask.findViewById<Chip>(groupedittask.checkedChipId).text

        val list = ArrayList<Category>(EnumSet.allOf(Category::class.java))
        for (category in list) {
            if (categoryname == category.en_name) {
                mycategorycode = category.code
            }
        }
        return mycategorycode
    }

    private fun showDatePickerDialog() {
        val newFragment =
            DatePickerFragment.newInstance((object : DatePickerDialog.OnDateSetListener {
                @RequiresApi(Build.VERSION_CODES.O)
                override fun onDateSet(p0: DatePicker?, p1: Int, p2: Int, p3: Int) {
                    if (validateOldDate(p1, p2 + 1, p3)) {
                        val selectedDate = p3.toString() + "/" + (p2 + 1) + "/" + p1
                        taskdate.setText(selectedDate)
                    } else {
                        taskdate.error = "Task date is invalid!"
                    }
                }
            }))
        newFragment.show(supportFragmentManager, "datePicker")
    }

    private fun saveTask() {
        taskitem.name = taskname.text.toString()
        taskitem.date = taskdate.text.toString()
        taskitem.budget = taskbudget.text.toString()
        taskitem.category = getCategory()

        if (taskitem.key == "") {
            addTask(applicationContext, taskitem)
        } else if (taskitem.key != "") {
            editTask(applicationContext, taskitem)
        }
        val resultIntent = Intent()
        setResult(Activity.RESULT_OK, resultIntent)
        finish()
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    companion object {
        const val TASKENTITY = "Task"
        const val TAG = "TaskCreateEdit"
    }
}


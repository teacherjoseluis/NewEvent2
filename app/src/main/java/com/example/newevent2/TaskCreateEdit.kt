package com.example.newevent2

import Application.Cache
import TimePickerFragment
import android.Manifest
import android.app.Activity
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.DatePicker
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.example.newevent2.Functions.*
import com.example.newevent2.Functions.validateOldDate
import com.example.newevent2.MVP.ImagePresenter
import com.example.newevent2.Model.Event
import com.example.newevent2.Model.EventModel
import com.example.newevent2.Model.Task
import com.example.newevent2.Model.TaskModel
import com.example.newevent2.ui.dialog.DatePickerFragment
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.theartofdev.edmodo.cropper.CropImage
import kotlinx.android.synthetic.main.eventform_layout.*
import kotlinx.android.synthetic.main.eventform_layout.savebutton
import kotlinx.android.synthetic.main.new_task_taskdetail.*
import kotlinx.android.synthetic.main.task_editdetail.*
import kotlinx.android.synthetic.main.task_editdetail.taskdate
import kotlinx.android.synthetic.main.task_editdetail.taskname
import kotlinx.android.synthetic.main.task_item_layout.*
import java.util.*

class TaskCreateEdit() : AppCompatActivity() {

    private var userid = ""
    private var eventid = ""
    //private var taskid = ""
    private lateinit var taskitem: Task

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.task_editdetail)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.setHomeAsUpIndicator(R.drawable.icons8_left_24)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        taskitem = intent.getParcelableExtra("task")!!
        userid = intent.getStringExtra("userid").toString()
        eventid = intent.getStringExtra("eventid").toString()
        val taskid = taskitem.key

        if (taskid != "") {
            val taskmodel = TaskModel()
            taskmodel.getTaskdetail(userid, eventid, taskid, object :
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

        taskname.setOnClickListener {
            taskname.error = null
        }

        taskdate.setOnClickListener {
            taskdate.error = null
            showDatePickerDialog()
        }

        taskbudget.setOnClickListener {
            taskbudget.error = null
        }

        val chipgroupedit = findViewById<ChipGroup>(R.id.groupedittask)
        chipgroupedit.isSingleSelection = true

        // Create chips and select the one matching the category
        val list = ArrayList<Category>(EnumSet.allOf(Category::class.java))
        for (category in list) {
            val chip = Chip(this)
            chip.text = category.en_name
            chipgroupedit.addView(chip)
            if (taskitem.category == category.code) {
                chip.isSelected = true
                chipgroupedit.check(chip.id)
            }
        }

        savebuttontask.setOnClickListener {
            var inputvalflag = true
            if (taskname.text.toString().isEmpty()) {
                taskname.error = "Task name is required!"
                inputvalflag = false
            }
            if (taskdate.text.toString().isEmpty()) {
                taskdate.error = "Task date is required!"
                inputvalflag = false
            }
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

//    private fun getCategory(): String {
//        var mycategory = ""
//        val chipselected = groupedit.findViewById<Chip>(groupedit.checkedChipId)
//        val chiptextvalue = chipselected.text.toString()
//        val list = ArrayList<Category>(EnumSet.allOf(Category::class.java))
//        for (category in list) {
//            if (chiptextvalue == category.en_name) {
//                mycategory = category.code
//            }
//        }
//        return mycategory
//    }

    private fun showDatePickerDialog() {
        val newFragment =
            DatePickerFragment.newInstance((object : DatePickerDialog.OnDateSetListener {
                @RequiresApi(Build.VERSION_CODES.O)
                override fun onDateSet(p0: DatePicker?, p1: Int, p2: Int, p3: Int) {
                    if (validateOldDate(p1, p2 + 1, p3)) {
                        val selectedDate = p3.toString() + "/" + (p2 + 1) + "/" + p1
                        eventdate.setText(selectedDate)
                    } else {
                        eventdate.error = "Task date is invalid!"
                    }
                }
            }))
        newFragment.show(supportFragmentManager, "datePicker")
    }

    private fun saveTask() {
        taskitem.name = taskname.text.toString()
        taskitem.date = taskdate.text.toString()
        taskitem.budget = taskbudget.text.toString()
        taskitem.category = groupedittask.findViewById<Chip>(groupedittask.checkedChipId).toString()

        val taskmodel = TaskModel()
        if (taskitem.key != "") {
            taskmodel.editTask(
                userid,
                eventid,
                taskitem,
                object : TaskModel.FirebaseAddEditTaskSuccess {
                    override fun onTaskAddedEdited(flag: Boolean) {
                        if (flag) {
                            //Deleting all instances of Task from cache
                            Cache.deletefromStorage(TASKENTITY, applicationContext)
                        }
                    }

                })
        } else {
            val usersession =
                application.getSharedPreferences("USER_SESSION", Context.MODE_PRIVATE)
            val tasksactive = usersession.getInt("tasksactive", 0)
            val sessionEditor = usersession!!.edit()
            sessionEditor.putInt("tasksactive", tasksactive + 1)
            sessionEditor.apply()

            taskmodel.addTask(
                userid,
                eventid,
                taskitem,
                tasksactive,
                object : TaskModel.FirebaseAddEditTaskSuccess {
                    override fun onTaskAddedEdited(flag: Boolean) {
                        if (flag) {
                            //Deleting all instances of Task from cache
                            Cache.deletefromStorage(TASKENTITY, applicationContext)
                        }
                    }
                })
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
    }
}


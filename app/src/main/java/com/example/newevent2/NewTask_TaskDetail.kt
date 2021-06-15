package com.example.newevent2

import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.newevent2.Model.Task
import com.example.newevent2.Model.TaskModel
import com.example.newevent2.ui.dialog.DatePickerFragment
import com.google.android.material.chip.Chip
import kotlinx.android.synthetic.main.new_task_taskdetail.button2
import kotlinx.android.synthetic.main.new_task_taskdetail.tkbudget
import kotlinx.android.synthetic.main.new_task_taskdetail.tkdate
import kotlinx.android.synthetic.main.new_task_taskdetail.tkname
import kotlinx.android.synthetic.main.task_editdetail.*
import java.util.*
import kotlin.collections.ArrayList


class NewTask_TaskDetail : AppCompatActivity() {
    var userid = ""
    var eventid = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.task_editdetail)
        userid = intent.getStringExtra("userid").toString()
        eventid = intent.getStringExtra("eventid").toString()

        // Toolbar
        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar!!.setHomeAsUpIndicator(R.drawable.icons8_left_24)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        val apptitle = findViewById<TextView>(R.id.appbartitle)
        apptitle.text = "New Task"

        //groupedit.isSingleSelection = true

        tkname.setOnClickListener {
            tkname.error = null
        }

        tkdate.setOnClickListener {
            tkdate.error = null
            tkdate.setText(
                com.example.newevent2.ui.Functions.showDatePickerDialog(
                    supportFragmentManager
                ).replace(" ","")
            )
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
//            if (groupedit.checkedChipId == -1) {
//                Toast.makeText(this, "Category is required!", Toast.LENGTH_SHORT).show()
//                inputvalflag = false
//            }
            if (inputvalflag) {
                saveTask()
                onBackPressed()
            }
        }
    }

    private fun saveTask() {
        val task = Task()
        task.name = tkname.text.toString()
        task.date = tkdate.text.toString()
        task.budget = tkbudget.text.toString()
        task.status = ACTIVESTATUS

        val usersession =
            application.getSharedPreferences("USER_SESSION", Context.MODE_PRIVATE)
        val tasksactive = usersession.getInt("tasksactive", 0)
        val sessionEditor = usersession!!.edit()
        sessionEditor.putInt("tasksactive", tasksactive + 1)
        sessionEditor.apply()

        //val chipselected = groupedit.findViewById<Chip>(groupedit.checkedChipId)
        //val chiptextvalue = chipselected.text.toString()

//        val list = ArrayList<Category>(EnumSet.allOf(Category::class.java))
//        for (category in list) {
//            if (chiptextvalue == category.en_name) {task.key
//                task.category = category.code
//            }
//        }
        val taskmodel = TaskModel()
        taskmodel.addTask(userid, eventid, task, tasksactive, object: TaskModel.FirebaseAddEditTaskSuccess{
            override fun onTaskAddedEdited(flag: Boolean) {
                TODO("Not yet implemented")
            }

        })
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    companion object {
        const val ACTIVESTATUS = "A"
    }
}
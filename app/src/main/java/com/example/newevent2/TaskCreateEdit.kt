package com.example.newevent2

import Application.Notification
import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.NotificationManager
import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.ComponentName
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.DatePicker
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import com.example.newevent2.Functions.*
import com.example.newevent2.Functions.addTask
import com.example.newevent2.Functions.editTask
import com.example.newevent2.Functions.getMockUserSetTime
import com.example.newevent2.Functions.getUserSession
import com.example.newevent2.Functions.validateOldDate
import com.example.newevent2.MVP.ContactsAllPresenter
import com.example.newevent2.Model.Task
import com.example.newevent2.Model.TaskDBHelper
import com.example.newevent2.Model.TaskModel
import com.example.newevent2.Model.User
import com.example.newevent2.Model.UserModel
import com.example.newevent2.ui.TextValidate
import com.example.newevent2.ui.dialog.DatePickerFragment
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.play.core.review.ReviewInfo
import com.google.android.play.core.review.ReviewManager
import com.google.android.play.core.review.ReviewManagerFactory
import com.google.android.play.core.review.testing.FakeReviewManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.gson.Gson
import kotlinx.android.synthetic.main.dashboardcharts.view.*
import kotlinx.android.synthetic.main.task_editdetail.*
import kotlinx.android.synthetic.main.task_editdetail.taskdate
import kotlinx.android.synthetic.main.task_editdetail.taskname
import java.util.*

class TaskCreateEdit() : AppCompatActivity() {

    private lateinit var taskitem: Task

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.task_editdetail)

        //This call checks the status of Firebase connection
        checkFirebaseconnection()

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

//        val taskid = taskitem.key
//        if (taskid != "") {
//            val taskmodel = TaskModel()
//            val user = com.example.newevent2.Functions.getUserSession(applicationContext!!)
//            taskmodel.getTaskdetail(user.key, user.eventid, taskid, object :
//                TaskModel.FirebaseSuccessTask {
//                @RequiresApi(Build.VERSION_CODES.O)
//                override fun onTask(task: Task) {
//                    taskitem.key = task.key
//                    taskitem.budget = task.budget
//                    taskitem.category = task.category
//                    taskitem.date = task.date
//                    taskitem.name = task.name
//                    taskitem.createdatetime = task.createdatetime
//                    taskitem.status = task.status
//
//                    taskname.setText(taskitem.name)
//                    taskdate.setText(taskitem.date)
//                    taskbudget.setText(taskitem.budget)
//                }
//
//                override fun onError(errorcode: String) {
//                    Toast.makeText(
//                        applicationContext,
//                        "There was an error trying to retrieve the task $errorcode",
//                        Toast.LENGTH_SHORT
//                    ).show()
//                }
//            })
//        }

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

        if (taskitem.key != "") {
            taskname.setText(taskitem.name)
            taskdate.setText(taskitem.date)
            taskbudget.setText(taskitem.budget)
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

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        if (taskitem.key != "") {
            menuInflater.inflate(R.menu.tasks_menu, menu)
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.delete_task -> {
                AlertDialog.Builder(this)
                    .setTitle("Delete entry")
                    .setMessage("Are you sure you want to delete this entry?") // Specifying a listener allows you to take an action before dismissing the dialog.
                    // The dialog is automatically dismissed when a dialog button is clicked.
                    .setPositiveButton(android.R.string.yes,
                        DialogInterface.OnClickListener { dialog, which ->
                            deleteTask(this, taskitem)
                            finish()
                        }) // A null listener allows the button to dismiss the dialog and take no further action.
                    .setNegativeButton(android.R.string.no, null)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show()
                true
            }
            R.id.complete_task -> {
                taskitem.status = Rv_TaskAdapter.COMPLETETASK
                editTask(this, taskitem)
                finish()
                true
            }
            else -> {
                true
            }
        }
    }


    //-------------------------------------------------------------------------------
    // Creating Notification for Tasks
    //-------------------------------------------------------------------------------
//    private fun addNotification(task: Task) {
//        // Job ID must be unique if you have multiple jobs scheduled
//        var jobID = NotificationID.getID()
//
//        var gson = Gson()
//        var json = gson.toJson(task)
//        var bundle = PersistableBundle()
//        bundle.putString("task", json)
//
//        // Get fake user set time (a future time 1 min from current time)
//        val (userSetHourOfDay, userSetMinute) = getMockUserSetTime()
//        val timeToWaitBeforeExecuteJob = calculateTimeDifferenceMs(userSetHourOfDay, userSetMinute)
//        (getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler).run {
//            schedule(
//                JobInfo.Builder(
//                    jobID,
//                    ComponentName(baseContext, NotificationJobService::class.java)
//                )
//                    // job execution will be delayed by this amount of time
//                    .setMinimumLatency(timeToWaitBeforeExecuteJob)
//                    // job will be run by this deadline
//                    .setOverrideDeadline(timeToWaitBeforeExecuteJob)
//                    .setExtras(bundle)
//                    .build()
//            )
//        }
//    }

    //-------------------------------------------------------------------------------
//    private fun delNotification(task: Task) {
//        val notificationManager =
//            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//        notificationManager.cancel(task.key, 0)
//    }
    //-------------------------------------------------------------------------------

    // Returns a pair ( hourOfDay, minute ) that represents a future time,
    // 1 minute after the current time
//    private fun getMockUserSetTime(): Pair<Int, Int> {
//        val calendar = Calendar.getInstance().apply {
//            // add just 1 min from current time
//            add(Calendar.MINUTE, 1)
//        }
//        return Pair(calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE))
//    }
//
//    // Calculate time difference relative to current time in ms
//    private fun calculateTimeDifferenceMs(hourOfDay: Int, minute: Int): Long {
//        val now = Calendar.getInstance()
//        val then = (now.clone() as Calendar).apply {
//            set(Calendar.HOUR_OF_DAY, hourOfDay)
//            set(Calendar.MINUTE, minute)
//        }
//        return then.timeInMillis - now.timeInMillis
//    }

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

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if ((checkSelfPermission(Manifest.permission.READ_CALENDAR) ==
                        PackageManager.PERMISSION_DENIED
                        ) && (checkSelfPermission(Manifest.permission.WRITE_CALENDAR) ==
                        PackageManager.PERMISSION_DENIED
                        )
            ) {
                //permission denied
                val permissions =
                    arrayOf(Manifest.permission.READ_CALENDAR, Manifest.permission.WRITE_CALENDAR)
                //show popup to request runtime permission
                requestPermissions(permissions, PERMISSION_CODE)
            } else {
                if (taskitem.key == "") {
                    addTask(applicationContext, taskitem)
                } else if (taskitem.key != "") {
                    editTask(applicationContext, taskitem)
                }
                val resultIntent = Intent()
                setResult(Activity.RESULT_OK, resultIntent)
            }
        }
        //------------------------------------------------
        // Request User's feedback
        val reviewManager: ReviewManager = ReviewManagerFactory.create(this)

        val requestReviewTask: com.google.android.play.core.tasks.Task<ReviewInfo> = reviewManager.requestReviewFlow()
        requestReviewTask.addOnCompleteListener { request ->
            if (request.isSuccessful) {
                // Request succeeded and a ReviewInfo instance was received
                val reviewInfo: ReviewInfo = request.result
                val launchReviewTask: com.google.android.play.core.tasks.Task<*> = reviewManager.launchReviewFlow(this, reviewInfo)
                launchReviewTask.addOnCompleteListener { _ ->
                    // The review has finished, continue your app flow.
                }
            } else {
                // Request failed
            }
        }
        //------------------------------------------------
    }
    
    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    private fun checkFirebaseconnection() {
        val connectedRef = FirebaseDatabase.getInstance().getReference(".info/connected")
        connectedRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val connected = snapshot.getValue(Boolean::class.java) ?: false
                if (connected) {
                    Log.d(TaskModel.TAG, "connected")
                } else {
                    val notification = Notification()
                    notification.sendnotification(
                        baseContext,
                        "No connectivity",
                        "Connectivity to Internet was lost"
                    )
                    Log.d(TaskModel.TAG, "not connected")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w(TaskModel.TAG, "Listener was cancelled")
            }
        })
    }

    companion object {
        const val TAG = "TaskCreateEdit"
        internal val PERMISSION_CODE = 42
    }
}


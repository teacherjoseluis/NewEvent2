package com.bridesandgrooms.event

import Application.AnalyticsManager
import Application.TaskCreationException
import Application.TaskDeletionException
import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil
import com.bridesandgrooms.event.Functions.*
import com.bridesandgrooms.event.Model.Category
import com.bridesandgrooms.event.Model.Permission
import com.bridesandgrooms.event.Model.Task
import com.bridesandgrooms.event.Model.TaskDBHelper
import com.bridesandgrooms.event.Model.User
import com.bridesandgrooms.event.databinding.TaskEditdetailBinding
import com.bridesandgrooms.event.UI.FieldValidators.TextValidate
import com.bridesandgrooms.event.UI.Dialogs.DatePickerFragment
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.play.core.review.ReviewInfo
import com.google.android.play.core.review.ReviewManager
import com.google.android.play.core.review.ReviewManagerFactory
import java.util.*

class TaskCreateEdit : AppCompatActivity() {

    //private lateinit var taskitem: Task
    private lateinit var optionsmenu: Menu
    private lateinit var adManager: AdManager
    private lateinit var binding: TaskEditdetailBinding

    private lateinit var userSession: User
    private lateinit var taskItem: Task

    private var thisTaskBudget: Float = 0F
    private var thisEventBudget: Float = 0F
    private var language = ""

    //private val receiver = TaskNotificationReceiver()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.task_editdetail)

        userSession = User().getUser(this)
        val eventBudget = userSession.eventbudget
        val apptitle = findViewById<TextView>(R.id.appbartitle)
        val chipgroupedit = findViewById<ChipGroup>(R.id.groupedittask)
        language = this.resources.configuration.locales.get(0).language
        val list = ArrayList(EnumSet.allOf(Category::class.java))
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        val extras = intent.extras
        setSupportActionBar(toolbar)
        supportActionBar!!.setHomeAsUpIndicator(R.drawable.icons8_left_24)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        if (extras?.containsKey("task") == true) {
            apptitle.text = getString(R.string.edit_task)
            taskItem = intent.getParcelableExtra<Task>("task")!!
        } else {
            apptitle.text = getString(R.string.new_task)
            taskItem = Task()
        }

        for (category in list) {
            val chip = Chip(this)
            chip.text = when (language) {
                "en" -> category.en_name
                else -> category.es_name
            }
            chip.isClickable = true
            chip.isCheckable = true
            chipgroupedit.addView(chip)
            if (taskItem.category == category.code) {
                chip.isSelected = true
                chipgroupedit.check(chip.id)
            }
        }

//        binding.taskname.onFocusChangeListener = View.OnFocusChangeListener { _, p1 ->
//            if (!p1) {
//                val validationmessage = TextValidate(binding.taskname).nameFieldValidate()
//                if (validationmessage != "") {
//                    binding.taskname.error = "Error in Task name: $validationmessage"
//                }
//            }
//        }

        //As soon as it's touched, the error message disappears
        binding.taskbudget.setOnClickListener {
            AnalyticsManager.getInstance().trackUserInteraction(SCREEN_NAME, "Task_Budget")
            binding.taskbudget.error = null
        }

        binding.taskdate.setOnClickListener {
            AnalyticsManager.getInstance().trackUserInteraction(SCREEN_NAME, "Task_Date")
            binding.taskdate.error = null
            showDatePickerDialog()
        }


        if (taskItem.key.isNotEmpty()) {
            binding.taskname.setText(taskItem.name)
            binding.taskdate.setText(taskItem.date)
            binding.taskbudget.setText(taskItem.budget)
        }

        binding.savebuttontask.setOnClickListener {
            Log.i(TAG, "Clicked on Save")
//            val intent = Intent("com.bridesandgrooms.event.NOTIFICATION_RECEIVED")
//            intent.putExtra("TASK_NAME", "My Dummy Task") // Pass taskName as an extra to the intent
//
//
//            try {
//                this.sendBroadcast(intent)
//                Log.i(TAG, "broadcast is called")
//            } catch (e: Exception) {
//                Log.e(TAG, e.message.toString())
//            }

            AnalyticsManager.getInstance().trackUserInteraction(SCREEN_NAME, "Save_Task")
            var inputvalflag = true
            binding.taskname.clearFocus()
            if (binding.taskname.text.toString().isEmpty()) {
                binding.taskname.error = getString(R.string.error_tasknameinput)
                inputvalflag = false
            } else {
//                val validationmessage = TextValidate(binding.taskname).nameFieldValidate()
//                if (validationmessage != "") {
//                    binding.taskname.error =
//                        getString(R.string.error_in_task_name, validationmessage)
//                    inputvalflag = false
//                }
            }

            binding.taskdate.clearFocus()
            if (binding.taskdate.text.toString().isEmpty()) {
                binding.taskdate.error = getString(R.string.error_taskdateinput)
                inputvalflag = false
            }
            binding.taskbudget.clearFocus()
            if (binding.taskbudget.text.toString().isEmpty()) {
                binding.taskbudget.error = getString(R.string.error_taskbudgetinput)
                inputvalflag = false
            } else {
                val thisTaskBudgetSt =
                    binding.taskbudget.text.toString().replace("[^\\d.]".toRegex(), "")
                val thisEventBudgetSt = eventBudget.replace("[^\\d.]".toRegex(), "")
                // Convert the sanitized string to Float
                thisTaskBudget = thisTaskBudgetSt.toFloatOrNull() ?: 0.0f
                thisEventBudget = thisEventBudgetSt.toFloatOrNull() ?: 0.0f
            }
            if (binding.groupedittask.checkedChipId == -1) {
                Toast.makeText(
                    this,
                    getString(R.string.error_taskcategoryinput),
                    Toast.LENGTH_SHORT
                ).show()
                inputvalflag = false
            }
            if (inputvalflag) {
                val taskEvent = TaskDBHelper(this)
                val taskBudget = taskEvent.getTaskBudget(this)!!
                val newEventBalance =
                    thisEventBudget - (taskBudget + thisTaskBudget)
                if (newEventBalance > 0) {
                    showBanner(getString(R.string.banner_congrats), false)
                    saveTask()
                } else {
                    AnalyticsManager.getInstance().trackError(
                        SCREEN_NAME,
                        "Exceeding Event Budget",
                        newEventBalance.toString(),
                        null
                    )
                    showBanner(getString(R.string.banner_beware), true)
                }
            }
        }
        val showads = RemoteConfigSingleton.get_showads()
        if (showads) {
            adManager = AdManagerSingleton.getAdManager()
            adManager.loadAndShowRewardedAd(this)
        }
    }

//    override fun onResume() {
//        super.onResume()
//        val filter = IntentFilter("android.intent.action.NOTIFICATION_RECEIVED")
//        registerReceiver(receiver, filter)
//    }
//
//    override fun onPause() {
//        super.onPause()
//        unregisterReceiver(receiver)
//    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        if (taskItem.key.isNotEmpty()) {
            optionsmenu = menu
            menuInflater.inflate(R.menu.tasks_menu, menu)

            if (taskItem.status == Rv_TaskAdapter.ACTIVETASK) {
                optionsmenu.findItem(R.id.complete_task).title = getString(R.string.complete_task)
            } else if (taskItem.status == Rv_TaskAdapter.COMPLETETASK) {
                optionsmenu.findItem(R.id.complete_task).title = getString(R.string.reactivate_task)
            }
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.delete_task -> {
                AlertDialog.Builder(this)
                    .setTitle(getString(R.string.delete_message))
                    .setMessage(getString(R.string.delete_entry))
                    .setPositiveButton(
                        android.R.string.yes
                    ) { _, _ ->
                        AnalyticsManager.getInstance()
                            .trackUserInteraction(SCREEN_NAME, "Delete_Task")
                        if (!PermissionUtils.checkPermissions(this, "calendar")) {
                            val permissions = PermissionUtils.requestPermissionsList("calendar")
                            requestPermissions(permissions, PERMISSION_CODE)
                        } else {
                            //lifecycleScope.launch {
                            try {
                                deleteTask(this@TaskCreateEdit, userSession, taskItem)
                                disableControls()
                            } catch (e: TaskDeletionException) {
                                //withContext(Dispatchers.Main){
                                displayToastMsg(getString(R.string.errorTaskDeletion) + e.toString())
                                //}
                                AnalyticsManager.getInstance().trackError(
                                    SCREEN_NAME,
                                    e.message.toString(),
                                    "deleteTask()",
                                    e.stackTraceToString()
                                )
                                Log.e(TAG, e.message.toString())
                            }
                            //}
                        }
                    }
                    .setNegativeButton(android.R.string.no, null)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show()
                true
            }

            R.id.complete_task -> {
                AnalyticsManager.getInstance().trackUserInteraction(SCREEN_NAME, "Complete_Task")
                if (taskItem.status == Rv_TaskAdapter.ACTIVETASK) {
                    taskItem.status = Rv_TaskAdapter.COMPLETETASK
                } else if (taskItem.status == Rv_TaskAdapter.COMPLETETASK) {
                    taskItem.status = Rv_TaskAdapter.ACTIVETASK
                }
                if (!PermissionUtils.checkPermissions(this, "calendar")) {
                    val permissions = PermissionUtils.requestPermissionsList("calendar")
                    requestPermissions(permissions, PERMISSION_CODE)
                } else {
                    //lifecycleScope.launch {
                    try {
                        editTask(this@TaskCreateEdit, userSession, taskItem)
                        disableControls()
                    } catch (e: TaskCreationException) {
                        displayToastMsg(getString(R.string.errorTaskCreation) + e.toString())
                        AnalyticsManager.getInstance().trackError(
                            SCREEN_NAME,
                            e.message.toString(),
                            "editTask()",
                            e.stackTraceToString()
                        )
                        Log.e(TAG, e.message.toString())
                    }
                    //}
                }
                true
            }

            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }

    private fun disableControls() {
        binding.taskname.isEnabled = false
        binding.taskbudget.isEnabled = false
        binding.taskdate.isEnabled = false
        binding.groupedittask.isEnabled = false
        binding.savebuttontask.isEnabled = false
        optionsmenu.clear()

        setResult(Activity.RESULT_OK, Intent())
//        delay(1500) // Replace Thread.sleep with delay from coroutines
        finish()
    }

//    private fun getCategory(): String {
//        var mycategorycode = ""
//        val categoryname =
//            binding.groupedittask.findViewById<Chip>(binding.groupedittask.checkedChipId).text
//
//        val list = ArrayList(EnumSet.allOf(Category::class.java))
//        for (category in list) {
//            if (categoryname == category.en_name) {
//                mycategorycode = category.code
//            }
//        }
//        return mycategorycode
//    }

    private fun showDatePickerDialog() {
        val newFragment =
            DatePickerFragment.newInstance((DatePickerDialog.OnDateSetListener { _, p1, p2, p3 ->
                if (validateOldDate(p1, p2 + 1, p3)) {
                    val selectedDate = p3.toString() + "/" + (p2 + 1) + "/" + p1
                    binding.taskdate.setText(selectedDate)
                } else {
                    binding.taskdate.error = getString(R.string.error_invaliddate)
                }
            }))
        newFragment.show(supportFragmentManager, "datePicker")
    }

    private fun saveTask() {
        taskItem.name = binding.taskname.text.toString()
        taskItem.date = binding.taskdate.text.toString()
        taskItem.budget = binding.taskbudget.text.toString()
        taskItem.category = getCategory()

        if (!PermissionUtils.checkPermissions(this, "calendar")) {
            val permissions = PermissionUtils.requestPermissionsList("calendar")
            requestPermissions(permissions, PERMISSION_CODE)
        } else {
            if (taskItem.key.isEmpty()) {
                //lifecycleScope.launch {
                try {
                    addTask(applicationContext, userSession, taskItem)
                } catch (e: TaskCreationException) {
                    AnalyticsManager.getInstance().trackError(
                        SCREEN_NAME,
                        e.message.toString(),
                        "addTask()",
                        e.stackTraceToString()
                    )
                    //withContext(Dispatchers.Main) {
                    displayToastMsg(getString(R.string.errorTaskCreation) + e.toString())
                    //}
                    Log.e(TAG, e.message.toString())
                }
                //}
            } else {
                //lifecycleScope.launch {
                try {
                    editTask(applicationContext, userSession, taskItem)
                } catch (e: TaskCreationException) {
                    AnalyticsManager.getInstance().trackError(
                        SCREEN_NAME,
                        e.message.toString(),
                        "editTask()",
                        e.stackTraceToString()
                    )
                    //withContext(Dispatchers.Main) {
                    displayToastMsg(getString(R.string.errorTaskCreation) + e.toString())
                    //}
                    Log.e(TAG, e.message.toString())
                }
                //}
            }
            val resultIntent = Intent()
            setResult(Activity.RESULT_OK, resultIntent)

            //------------------------------------------------
            // Request User's feedback
            val reviewbox = RemoteConfigSingleton.get_reviewbox()
            if (reviewbox) {
                val reviewManager: ReviewManager = ReviewManagerFactory.create(this)

                val requestReviewTask: com.google.android.play.core.tasks.Task<ReviewInfo> =
                    reviewManager.requestReviewFlow()
                requestReviewTask.addOnCompleteListener { request ->
                    if (request.isSuccessful) {
                        // Request succeeded and a ReviewInfo instance was received
                        val reviewInfo: ReviewInfo = request.result
                        val launchReviewTask: com.google.android.play.core.tasks.Task<*> =
                            reviewManager.launchReviewFlow(this, reviewInfo)
                        launchReviewTask.addOnCompleteListener {
                            // The review has finished, continue your app flow.
                        }
                    }
                }
            }
            val showads = RemoteConfigSingleton.get_showads()
            if (showads) {
                if (adManager.mRewardedAd != null) {
                    adManager.mRewardedAd?.show(this) {}
                } else {
                    Log.d(TAG, "The rewarded ad wasn't ready yet.")
                }
            }
            //Thread.sleep(2000)
            finish()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            TaskCreateEdit.PERMISSION_CODE -> {
                // Check if all permissions were granted
                if (grantResults.isNotEmpty() && grantResults[0] ==
                    PackageManager.PERMISSION_GRANTED
                ) {
                    binding.withdata.visibility = ConstraintLayout.INVISIBLE
                    binding.permissions.root.visibility = ConstraintLayout.VISIBLE
                    val calendarpermissions = Permission.getPermission("calendar")
                    val resourceId = this.resources.getIdentifier(
                        calendarpermissions.drawable, "drawable",
                        this.packageName
                    )
                    binding.permissions.root.findViewById<ImageView>(R.id.permissionicon)
                        .setImageResource(resourceId)

                    val language = this.resources.configuration.locales.get(0).language
                    val permissionwording = when (language) {
                        "en" -> calendarpermissions.permission_wording_en
                        else -> calendarpermissions.permission_wording_es
                    }
                    binding.permissions.root.findViewById<TextView>(R.id.permissionwording).text =
                        permissionwording

                    val openSettingsButton =
                        binding.permissions.root.findViewById<Button>(R.id.permissionsbutton)
                    openSettingsButton.setOnClickListener {
                        // Create an intent to open the app settings for your app
                        val intent = Intent()
                        intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                        val packageName = packageName
                        val uri = Uri.fromParts("package", packageName, null)
                        intent.data = uri

                        // Start the intent
                        startActivity(intent)
                    }
                }
            }
        }


    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    private fun showBanner(message: String, dismiss: Boolean) {
        val fadeInAnimation = AnimationUtils.loadAnimation(this, R.anim.fade_in)
        binding.bannerCardView.startAnimation(fadeInAnimation)

        binding.bannerCardView.visibility = View.VISIBLE
        binding.bannerText.text = message
        if (dismiss) {
            binding.dismissButton.visibility = View.VISIBLE
            binding.dismissButton.setOnClickListener {
                binding.bannerCardView.visibility = View.INVISIBLE
            }
        }
    }

    private fun getCategory(): String {
        val categoryName =
            binding.groupedittask.findViewById<Chip>(binding.groupedittask.checkedChipId).text.toString()
        var myCategory = ""
        val list = ArrayList(EnumSet.allOf(Category::class.java))
        for (category in list) {
            when (language) {
                "en" -> {
                    if (categoryName == category.en_name) {
                        myCategory = category.code
                    }
                }

                else -> {
                    if (categoryName == category.es_name) {
                        myCategory = category.code
                    }
                }
            }
        }
        return myCategory
    }

    private fun displayToastMsg(message: String) {
        Toast.makeText(
            this@TaskCreateEdit,
            message,
            Toast.LENGTH_LONG
        ).show()
    }

    override fun finish() {
        val returnintent = Intent()
        setResult(RESULT_OK, returnintent)
        super.finish()
    }

    companion object {
        const val TAG = "TaskCreateEdit"
        const val SCREEN_NAME = "Task_CreateEdit"
        internal const val PERMISSION_CODE = 42
    }
}


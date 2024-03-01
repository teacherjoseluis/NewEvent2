package com.bridesandgrooms.event

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Message
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.bridesandgrooms.event.Functions.*
import com.bridesandgrooms.event.Model.Category
import com.bridesandgrooms.event.Model.Event
import com.bridesandgrooms.event.Model.Permission
import com.bridesandgrooms.event.Model.Task
import com.bridesandgrooms.event.Model.User
import com.bridesandgrooms.event.Model.UserDBHelper
import com.bridesandgrooms.event.databinding.TaskEditdetailBinding
import com.bridesandgrooms.event.UI.TextValidate
import com.bridesandgrooms.event.UI.dialog.DatePickerFragment
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.play.core.review.ReviewInfo
import com.google.android.play.core.review.ReviewManager
import com.google.android.play.core.review.ReviewManagerFactory
//import kotlinx.android.synthetic.main.task_editdetail.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*

class TaskCreateEdit : AppCompatActivity() {

    private lateinit var taskitem: Task
    private lateinit var optionsmenu: Menu
    private lateinit var adManager: AdManager
    private lateinit var binding: TaskEditdetailBinding

    //--------------------------------------------------
    private lateinit var usersession: User

    private var thisTaskBudget: Float = 0F
    private var thisEventBudget: Float = 0F

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userdbhelper = UserDBHelper(this)
        usersession = userdbhelper.getUser(userdbhelper.getUserKey())!!
        val eventBudget = usersession.eventbudget

        binding = DataBindingUtil.setContentView(this, R.layout.task_editdetail)

        //This call checks the status of Firebase connection
        //checkFirebaseconnection()

        //Declaring and enabling the toolbar for this view
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.setHomeAsUpIndicator(R.drawable.icons8_left_24)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        val apptitle = findViewById<TextView>(R.id.appbartitle)

        val extras = intent.extras
        if (extras?.containsKey("task") == true) {
            apptitle.text = getString(R.string.edit_task)
        } else {
            apptitle.text = getString(R.string.new_task)
        }

        //If nothing comes in the extras, we can assume this is a new task but if this is
        //coming populated we can assume it's coming from an existing task which in this case
        //loads taskitem

        taskitem = if (extras?.containsKey("task") == true) {
            intent.getParcelableExtra("task")!!
        } else {
            Task()
        }

        //I intend here to validate the name field and not invalid text is coming here
        //it's validated as soon as the user moves away
        binding.taskname.onFocusChangeListener = View.OnFocusChangeListener { _, p1 ->
            if (!p1) {
                val validationmessage = TextValidate(binding.taskname).namefieldValidate()
                if (validationmessage != "") {
                    //I'm afraid I'm not localizing this one for the moment
                    binding.taskname.error = "Error in Task name: $validationmessage"
                }
            }
        }

        //As soon as it's touched, the error message disappears
        binding.taskbudget.setOnClickListener {
            binding.taskbudget.error = null
        }

        binding.taskdate.setOnClickListener {
            binding.taskdate.error = null
            showDatePickerDialog()
        }

        val chipgroupedit = findViewById<ChipGroup>(R.id.groupedittask)

        // Create chips and select the one matching the category

        val language = this.resources.configuration.locales.get(0).language
        val list = ArrayList(EnumSet.allOf(Category::class.java))
        for (category in list) {
            val chip = Chip(this)
            chip.text = when (language) {
                "en" -> category.en_name
                else -> category.es_name
            }
            chip.isClickable = true
            chip.isCheckable = true
            chipgroupedit.addView(chip)
            if (taskitem.category == category.code) {
                chip.isSelected = true
                chipgroupedit.check(chip.id)
            }
        }

        //Loads task fields as we are editing an existing task
        if (taskitem.key != "") {
            binding.taskname.setText(taskitem.name)
            binding.taskdate.setText(taskitem.date)
            binding.taskbudget.setText(taskitem.budget)
        }

        binding.savebuttontask.setOnClickListener {
            var inputvalflag = true
            binding.taskname.clearFocus()
            if (binding.taskname.text.toString().isEmpty()) {
                binding.taskname.error = getString(R.string.error_tasknameinput)
                inputvalflag = false
            } else {
                val validationmessage = TextValidate(binding.taskname).namefieldValidate()
                if (validationmessage != "") {
                    binding.taskname.error = "Error in Task name: $validationmessage"
                    inputvalflag = false
                }
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
                val thisTaskBudgetSt = binding.taskbudget.text.toString().replace("[^\\d.]".toRegex(), "")
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
                val taskEvent = Task()
                val taskBudget = taskEvent.getTaskBudget(applicationContext)!!
                val newEventBalance =
                    thisEventBudget - (taskBudget + thisTaskBudget)
                if (newEventBalance > 0) {
                    showBanner(getString(R.string.banner_congrats), false)
                    saveTask()
                } else {
                    showBanner(getString(R.string.banner_beware), true)
                }
            }
        }

        // Loading the Ad at this point
//        val adRequest = AdRequest.Builder().build()
//        RewardedAd.load(
//            this,
//            "ca-app-pub-3940256099942544/5224354917",
//            adRequest,
//            object : RewardedAdLoadCallback() {
//                override fun onAdFailedToLoad(adError: LoadAdError) {
//                    Log.d(TAG, adError.message)
//                    mRewardedAd = null
//                }
//
//                override fun onAdLoaded(rewardedAd: RewardedAd) {
//                    Log.d(TAG, "Ad was loaded.")
//                    mRewardedAd = rewardedAd
//                }
//            })
//
//        //Listeners on the Ad loading and displaying in case I want to use them later
//        mRewardedAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
//            override fun onAdShowedFullScreenContent() {
//                // Called when ad is shown.
//                Log.d(TAG, "Ad was shown.")
//            }
//
//            override fun onAdFailedToShowFullScreenContent(adError: AdError?) {
//                // Called when ad fails to show.
//                Log.d(TAG, "Ad failed to show.")
//            }
//
//            override fun onAdDismissedFullScreenContent() {
//                // Called when ad is dismissed.
//                // Set the ad reference to null so you don't show the ad a second time.
//                Log.d(TAG, "Ad was dismissed.")
//                mRewardedAd = null
//            }
//        }

        val showads = RemoteConfigSingleton.get_showads()

        if (showads) {
            adManager = AdManagerSingleton.getAdManager()
            adManager.loadAndShowRewardedAd(this)
        }
    }

    // The menu will allow the user to mark tasks as deleted or completed but this
    // cannot happen when the tasks is brand new
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        if (taskitem.key != "") {
            optionsmenu = menu
            menuInflater.inflate(R.menu.tasks_menu, menu)

            //val resourceName = resources.getResourceName(R.id.complete_task)
            if (taskitem.status == Rv_TaskAdapter.ACTIVETASK) {
                optionsmenu.findItem(R.id.complete_task).title = getString(R.string.complete_task)
            } else if (taskitem.status == Rv_TaskAdapter.COMPLETETASK) {
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
                    // Specifying a listener allows you to take an action before dismissing the dialog.
                    // The dialog is automatically dismissed when a dialog button is clicked.
                    .setPositiveButton(
                        android.R.string.yes
                    ) { _, _ ->
                        if (!PermissionUtils.checkPermissions(applicationContext, "calendar")) {
                            val permissions = PermissionUtils.requestPermissionsList("calendar")
                            requestPermissions(permissions, PERMISSION_CODE)
                        } else {
                            lifecycleScope.launch {
                                deleteTask(this@TaskCreateEdit, taskitem)
                                disableControls()
                            }
                        }
                    }
                    // A null listener allows the button to dismiss the dialog and take no
                    // further action.
                    .setNegativeButton(android.R.string.no, null)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show()
//                    val resultIntent = Intent()
//                    setResult(Activity.RESULT_OK, resultIntent)
//                    Thread.sleep(1500)
//                    finish()
//                    super.onOptionsItemSelected(item)
                true
            }

            R.id.complete_task -> {
                if (taskitem.status == Rv_TaskAdapter.ACTIVETASK) {
                    taskitem.status = Rv_TaskAdapter.COMPLETETASK
                } else if (taskitem.status == Rv_TaskAdapter.COMPLETETASK) {
                    taskitem.status = Rv_TaskAdapter.ACTIVETASK
                }
                if (!PermissionUtils.checkPermissions(applicationContext, "calendar")) {
                    val permissions = PermissionUtils.requestPermissionsList("calendar")
                    requestPermissions(permissions, PERMISSION_CODE)
                } else {
                    lifecycleScope.launch {
                        editTask(this@TaskCreateEdit, taskitem)
                        disableControls()
                    }
                    // disable controls
//                    taskname.isEnabled = false
//                    taskbudget.isEnabled = false
//                    taskdate.isEnabled = false
//                    groupedittask.isEnabled = false
//                    savebuttontask.isEnabled = false
//                    optionsmenu.clear()
                }
//                val resultIntent = Intent()
//                setResult(Activity.RESULT_OK, resultIntent)
//                Thread.sleep(1500)
//                finish()
//                super.onOptionsItemSelected(item)
                true
            }

            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }

    private suspend fun disableControls() {
        binding.taskname.isEnabled = false
        binding.taskbudget.isEnabled = false
        binding.taskdate.isEnabled = false
        binding.groupedittask.isEnabled = false
        binding.savebuttontask.isEnabled = false
        optionsmenu.clear()

        setResult(Activity.RESULT_OK, Intent())
        delay(1500) // Replace Thread.sleep with delay from coroutines
        finish()
    }

    // Based on the chip that has been selected,
    // this function retrieves the name of the category to be saved in the DB
    private fun getCategory(): String {
        var mycategorycode = ""
        val categoryname =
            binding.groupedittask.findViewById<Chip>(binding.groupedittask.checkedChipId).text

        val list = ArrayList(EnumSet.allOf(Category::class.java))
        for (category in list) {
            if (categoryname == category.en_name) {
                mycategorycode = category.code
            }
        }
        return mycategorycode
    }

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
        taskitem.name = binding.taskname.text.toString()
        taskitem.date = binding.taskdate.text.toString()
        taskitem.budget = binding.taskbudget.text.toString()
        taskitem.category = getCategory()

//        if (!checkPermissions()) {
//            alertBox()
        if (!PermissionUtils.checkPermissions(applicationContext, "calendar")) {
            val permissions = PermissionUtils.requestPermissionsList("calendar")
            requestPermissions(permissions, PERMISSION_CODE)
        } else {
            if (taskitem.key == "") {
                addTask(applicationContext, taskitem)
            } else if (taskitem.key != "") {
                editTask(applicationContext, taskitem)
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
            //------------------------------------------------
            val showads = RemoteConfigSingleton.get_showads()

            if (showads) {
                if (adManager.mRewardedAd != null) {
                    adManager.mRewardedAd?.show(this) {}
                } else {
                    Log.d(TAG, "The rewarded ad wasn't ready yet.")
                }
            }
            Thread.sleep(2000)
            finish()
        }
    }

//    private fun alertBox() {
//        val builder = android.app.AlertDialog.Builder(this)
//        builder.setTitle(getString(R.string.lackpermissions_message))
//        builder.setMessage(getString(R.string.lackpermissions_message))
//
//        builder.setPositiveButton(
//            getString(R.string.accept)
//        ) { _, _ ->
//            requestPermissions()
//        }
//        builder.setNegativeButton(
//            "Cancel"
//        ) { p0, _ -> p0!!.dismiss() }
//
//        val dialog = builder.create()
//        dialog.show()
//    }

//    private fun checkPermissions(): Boolean {
//        return ((checkSelfPermission(Manifest.permission.READ_CALENDAR) ==
//                PackageManager.PERMISSION_GRANTED
//                ) && (checkSelfPermission(Manifest.permission.WRITE_CALENDAR) ==
//                PackageManager.PERMISSION_GRANTED
//                ) && (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) ==
//                PackageManager.PERMISSION_GRANTED
//                ) && (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
//                PackageManager.PERMISSION_GRANTED
//                ))
//    }

//    private fun requestPermissions() {
//        val permissions =
//            arrayOf(
//                Manifest.permission.READ_CALENDAR,
//                Manifest.permission.WRITE_CALENDAR,
//                Manifest.permission.READ_EXTERNAL_STORAGE,
//                Manifest.permission.WRITE_EXTERNAL_STORAGE
//            )
//        //show popup to request runtime permission
//        requestPermissions(permissions, TaskCreateEdit.PERMISSION_CODE)
//    }

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
                    saveTask()
                } else {
                    // At least one permission was denied.
                    // You can handle the denial scenario here, such as displaying a message or disabling functionality that requires the permissions.
                    // Here goes what happens when the permission is not given
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
            // Add other request codes and handling logic for other permission requests if needed.
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    //Not sure about kkeping with this function as I think is not very useful.
    // Simply checks if there is connectivity with Firebase and sends a notification to the user
//    private fun checkFirebaseconnection() {
//        val connectedRef = FirebaseDatabase.getInstance().getReference(".info/connected")
//        connectedRef.addValueEventListener(object : ValueEventListener {
//            override fun onDataChange(snapshot: DataSnapshot) {
//                val connected = snapshot.getValue(Boolean::class.java) ?: false
//                if (connected) {
//                    Log.d(TaskModel.TAG, "connected")
//                } else {
//                    val notification = Notification()
//                    notification.sendnotification(
//                        baseContext,
//                        "No connectivity",
//                        "Connectivity to Internet was lost"
//                    )
//                    Log.d(TaskModel.TAG, "not connected")
//                }
//            }
//
//            override fun onCancelled(error: DatabaseError) {
//                Log.w(TaskModel.TAG, "Listener was cancelled")
//            }
//        })
//    }

    private fun showBanner(message: String, dismiss: Boolean) {
        //binding.taskname.visibility = View.INVISIBLE
        val fadeInAnimation = AnimationUtils.loadAnimation(applicationContext, R.anim.fade_in)
        binding.bannerCardView.startAnimation(fadeInAnimation)

        binding.bannerCardView.visibility = View.VISIBLE
        binding.bannerText.text = message
        //getString(R.string.number_guests)
        if (dismiss) {
            binding.dismissButton.visibility = View.VISIBLE
            binding.dismissButton.setOnClickListener { binding.bannerCardView.visibility = View.INVISIBLE }
        }
    }

    override fun finish() {
        val returnintent = Intent()
        setResult(RESULT_OK, returnintent)
        super.finish()
    }

    companion object {
        const val TAG = "TaskCreateEdit"
        internal const val PERMISSION_CODE = 42
    }
}


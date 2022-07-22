package com.example.newevent2

import Application.Notification
import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.newevent2.Functions.*
import com.example.newevent2.Model.*
import com.example.newevent2.Model.Task
import com.example.newevent2.ui.TextValidate
import com.example.newevent2.ui.dialog.DatePickerFragment
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.play.core.review.ReviewInfo
import com.google.android.play.core.review.ReviewManager
import com.google.android.play.core.review.ReviewManagerFactory
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.task_editdetail.*
import java.util.*

class TaskCreateEdit : AppCompatActivity() {

    private lateinit var taskitem: Task
    private var mRewardedAd: RewardedAd? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.task_editdetail)

        //This call checks the status of Firebase connection
        checkFirebaseconnection()

        //Declaring and enabling the toolbar for this view
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.setHomeAsUpIndicator(R.drawable.icons8_left_24)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        //If nothing comes in the extras, we can assume this is a new task but if this is
        //coming populated we can assume it's coming from an existing task which in this case
        //loads taskitem
        val extras = intent.extras
        taskitem = if (extras?.containsKey("task") == true) {
            intent.getParcelableExtra("task")!!
        } else {
            Task()
        }

        //I intend here to validate the name field and not invalid text is coming here
        //it's validated as soon as the user moves away
        taskname.onFocusChangeListener = View.OnFocusChangeListener { _, p1 ->
            if (!p1) {
                val validationmessage = TextValidate(taskname).namefieldValidate()
                if (validationmessage != "") {
                    //I'm afraid I'm not localizing this one for the moment
                    taskname.error = "Error in Task name: $validationmessage"
                }
            }
        }

        //As soon as it's touched, the error message disappears
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

        //Loads task fields as we are editing an existing task
        if (taskitem.key != "") {
            taskname.setText(taskitem.name)
            taskdate.setText(taskitem.date)
            taskbudget.setText(taskitem.budget)
        }

        savebuttontask.setOnClickListener {
            var inputvalflag = true
            taskname.clearFocus()
            if (taskname.text.toString().isEmpty()) {
                taskname.error = getString(R.string.error_tasknameinput)
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
                taskdate.error = getString(R.string.error_taskdateinput)
                inputvalflag = false
            }
            taskbudget.clearFocus()
            if (taskbudget.text.toString().isEmpty()) {
                taskbudget.error = getString(R.string.error_taskbudgetinput)
                inputvalflag = false
            }
            if (groupedittask.checkedChipId == -1) {
                Toast.makeText(
                    this,
                    getString(R.string.error_taskcategoryinput),
                    Toast.LENGTH_SHORT
                ).show()
                inputvalflag = false
            }
            if (inputvalflag) {
                saveTask()
            }
        }

        // Loading the Ad at this point
        val adRequest = AdRequest.Builder().build()
        RewardedAd.load(
            this,
            "ca-app-pub-3940256099942544/5224354917",
            adRequest,
            object : RewardedAdLoadCallback() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    Log.d(TAG, adError.message)
                    mRewardedAd = null
                }

                override fun onAdLoaded(rewardedAd: RewardedAd) {
                    Log.d(TAG, "Ad was loaded.")
                    mRewardedAd = rewardedAd
                }
            })

        //Listeners on the Ad loading and displaying in case I want to use them later
        mRewardedAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdShowedFullScreenContent() {
                // Called when ad is shown.
                Log.d(TAG, "Ad was shown.")
            }

            override fun onAdFailedToShowFullScreenContent(adError: AdError?) {
                // Called when ad fails to show.
                Log.d(TAG, "Ad failed to show.")
            }

            override fun onAdDismissedFullScreenContent() {
                // Called when ad is dismissed.
                // Set the ad reference to null so you don't show the ad a second time.
                Log.d(TAG, "Ad was dismissed.")
                mRewardedAd = null
            }
        }

    }

    // The menu will allow the user to mark tasks as deleted or completed but this
    // cannot happen when the tasks is brand new
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
                    .setTitle(getString(R.string.delete_message))
                    .setMessage(getString(R.string.delete_entry))
                    // Specifying a listener allows you to take an action before dismissing the dialog.
                    // The dialog is automatically dismissed when a dialog button is clicked.
                    .setPositiveButton(android.R.string.yes
                    ) { _, _ ->
                        deleteTask(this, taskitem)
                        finish()
                    }
                    // A null listener allows the button to dismiss the dialog and take no
                    // further action.
                    .setNegativeButton(android.R.string.no, null)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show()
                super.onOptionsItemSelected(item);
            }
            R.id.complete_task -> {
                taskitem.status = Rv_TaskAdapter.COMPLETETASK
                editTask(this, taskitem)
                finish()
                super.onOptionsItemSelected(item);
            }
            else -> {
                super.onOptionsItemSelected(item);
            }
        }
    }

    // Based on the chip that has been selected,
    // this function retrieves the name of the category to be saved in the DB
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
            DatePickerFragment.newInstance((DatePickerDialog.OnDateSetListener { _, p1, p2, p3 ->
                if (validateOldDate(p1, p2 + 1, p3)) {
                    val selectedDate = p3.toString() + "/" + (p2 + 1) + "/" + p1
                    taskdate.setText(selectedDate)
                } else {
                    taskdate.error = getString(R.string.error_invaliddate)
                }
            }))
        newFragment.show(supportFragmentManager, "datePicker")
    }

    private fun saveTask() {
        taskitem.name = taskname.text.toString()
        taskitem.date = taskdate.text.toString()
        taskitem.budget = taskbudget.text.toString()
        taskitem.category = getCategory()

        // Since we are going to add the entry in the calendar for the task
        // we need to ask for permission on the Calendar
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
        //------------------------------------------------
        // Request User's feedback
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
        //------------------------------------------------
        if (mRewardedAd != null) {
            mRewardedAd?.show(this) {}
        } else {
            Log.d(TAG, "The rewarded ad wasn't ready yet.")
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    //Not sure about kkeping with this function as I think is not very useful.
    // Simply checks if there is connectivity with Firebase and sends a notification to the user
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
        internal const val PERMISSION_CODE = 42
    }
}


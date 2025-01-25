package com.bridesandgrooms.event.UI.Fragments

import Application.AnalyticsManager
import Application.TaskCreationException
import Application.TaskDeletionException
import Application.UserRetrievalException
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.children
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.bridesandgrooms.event.AdManager
import com.bridesandgrooms.event.Functions.*
import com.bridesandgrooms.event.Model.Category
import com.bridesandgrooms.event.Model.Permission
import com.bridesandgrooms.event.Model.Task
import com.bridesandgrooms.event.Model.TaskDBHelper
import com.bridesandgrooms.event.Model.User
import com.bridesandgrooms.event.R
import com.bridesandgrooms.event.UI.Adapters.TaskAdapter
import com.bridesandgrooms.event.databinding.TaskEditdetailBinding
import com.bridesandgrooms.event.UI.Dialogs.DatePickerFragment
import com.bridesandgrooms.event.UI.FieldValidators.InputValidator
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.chip.Chip
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.android.play.core.review.ReviewInfo
import com.google.android.play.core.review.ReviewManager
import com.google.android.play.core.review.ReviewManagerFactory
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class TaskCreateEdit : Fragment() {

    private lateinit var optionsmenu: Menu
    private lateinit var adManager: AdManager
    private lateinit var binding: TaskEditdetailBinding

    private lateinit var userSession: User
    private lateinit var taskItem: Task
    private lateinit var context: Context
    private lateinit var backPressCallback: OnBackPressedCallback

    private var thisTaskBudget: Float = 0F
    private var thisEventBudget: Float = 0F
    private var taskDate: Date = Date()


    private val focusChangeListener = View.OnFocusChangeListener { view, hasFocus ->
        if (hasFocus && view is TextInputEditText) {
            val parentLayout = view.parent.parent as? TextInputLayout
            parentLayout?.error = null
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        context = requireContext()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        try {
            userSession = User.getUser()
        } catch (e: UserRetrievalException) {
            displayErrorMsg(getString(R.string.errorretrieveuser))
        } catch (e: Exception) {
            displayErrorMsg(getString(R.string.error_unknown) + " - " + e.toString())
        }

        val eventBudget = userSession.eventbudget

        val thisEventBudgetSt = eventBudget.replace("[^\\d.]".toRegex(), "")
        thisEventBudget = thisEventBudgetSt.toFloatOrNull() ?: 0.0f

        binding = DataBindingUtil.inflate(inflater, R.layout.task_editdetail, container, false)

        val toolbar = requireActivity().findViewById<MaterialToolbar>(R.id.toolbar)
        toolbar.findViewById<TextView>(R.id.appbartitle)?.text = getString(R.string.new_task)

        val list = ArrayList(EnumSet.allOf(Category::class.java))
        val language = context.resources.configuration.locales.get(0).language

        for (category in list) {
            val chip = Chip(context)
            chip.text = when (language) {
                "en" -> category.en_name
                else -> category.es_name
            }
            chip.apply {
                isClickable = true
                isCheckable = true

                chipBackgroundColor = ColorStateList.valueOf(
                    ContextCompat.getColor(
                        context,
                        R.color.SecondaryContainer_cream
                    )
                )
                setTextColor(ContextCompat.getColor(context, R.color.OnSecondaryContainer_cream))
                chipCornerRadius = TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    8f,
                    resources.displayMetrics
                )
                rippleColor =
                    ColorStateList.valueOf(ContextCompat.getColor(context, R.color.OnPrimary_cream))
                chipStrokeColor =
                    ColorStateList.valueOf(ContextCompat.getColor(context, R.color.Outline_cream))
                chipStrokeWidth = TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    1f,
                    resources.displayMetrics
                ) // Assuming 1dp is defined in dimens.xml
                setTextAppearance(R.style.Body_Small)
            }
            binding.groupedittask.addView(chip)
        }

        taskItem = arguments?.getParcelable("task") ?: Task()

        if (taskItem.key.isNotEmpty()) {
            toolbar.findViewById<TextView>(R.id.appbartitle)?.text = getString(R.string.edit_guest)
            binding.tasknameinputedit.setText(taskItem.name)
            binding.taskdateinputedit.setText(taskItem.date)
            binding.taskbudgetinputedit.setText(taskItem.budget)

            val selectedChipId = binding.groupedittask.children
                .filterIsInstance<Chip>()
                .find { it.text == if (language == "en") list.find { category -> category.code == taskItem.category }?.en_name else list.find { category -> category.code == taskItem.category }?.es_name }
                ?.id

            selectedChipId?.let {
                binding.groupedittask.check(it)
            }
        }

        if (arguments?.containsKey("task_date") == true) {
            taskDate = arguments?.getSerializable("task_date") as Date
            val formatter = SimpleDateFormat("dd/MM/yyyy")
            binding.taskdateinputedit.setText(formatter.format(taskDate))
            binding.taskdateinputedit.isEnabled = false

        }

        binding.tasknameinputedit.onFocusChangeListener = focusChangeListener
        binding.taskdateinputedit.onFocusChangeListener = focusChangeListener
        binding.taskdateinputedit.setOnClickListener {
            showDatePickerDialog()
        }
        binding.taskbudgetinputedit.onFocusChangeListener = focusChangeListener

        binding.savebuttontask.setOnClickListener {
            AnalyticsManager.getInstance()
                .trackUserInteraction(GuestCreateEdit.SCREEN_NAME, "Save_Task")
            val isValid = validateAllInputs()
            if (isValid) {
                saveTask()
            }
        }
//        val showads = RemoteConfigSingleton.get_showads()
//        if (showads) {
//            adManager = AdManagerSingleton.getAdManager()
//            adManager.loadAndShowRewardedAd(requireActivity())
//        }
        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        if (taskItem.key.isNotEmpty()) {
            optionsmenu = menu
            inflater.inflate(R.menu.tasks_menu, menu)
            if (taskItem.status == TaskAdapter.ACTIVETASK) {
                optionsmenu.findItem(R.id.complete_task).title = getString(R.string.complete_task)
            } else if (taskItem.status == TaskAdapter.COMPLETETASK) {
                optionsmenu.findItem(R.id.complete_task).title = getString(R.string.reactivate_task)
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.delete_task -> {
                AlertDialog.Builder(context)
                    .setTitle(getString(R.string.delete_message))
                    .setMessage(getString(R.string.delete_entry))
                    .setPositiveButton(
                        android.R.string.yes
                    ) { _, _ ->
                        AnalyticsManager.getInstance()
                            .trackUserInteraction(SCREEN_NAME, "Delete_Task")
                        if (!PermissionUtils.checkPermissions(context, "calendar")) {
                            val permissions = PermissionUtils.requestPermissionsList("calendar")
                            requestPermissions(permissions, PERMISSION_CODE)
                        } else {
                            try {
                                deleteTask(taskItem.key)
                            } catch (e: TaskDeletionException) {
                                displayErrorMsg(getString(R.string.errorTaskDeletion) + e.toString())
                                AnalyticsManager.getInstance().trackError(
                                    SCREEN_NAME,
                                    e.message.toString(),
                                    "deleteTask()",
                                    e.stackTraceToString()
                                )
                                Log.e(TAG, e.message.toString())
                            }
                        }
                    }
                    .setNegativeButton(android.R.string.no, null)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show()
                true
            }

            R.id.complete_task -> {
                AnalyticsManager.getInstance().trackUserInteraction(SCREEN_NAME, "Complete_Task")
                if (taskItem.status == TaskAdapter.ACTIVETASK) {
                    taskItem.status = TaskAdapter.COMPLETETASK
                } else if (taskItem.status == TaskAdapter.COMPLETETASK) {
                    taskItem.status = TaskAdapter.ACTIVETASK
                }
                if (!PermissionUtils.checkPermissions(requireActivity(), "calendar")) {
                    val permissions = PermissionUtils.requestPermissionsList("calendar")
                    requestPermissions(permissions, PERMISSION_CODE)
                } else {
                    //lifecycleScope.launch {
                    try {
                        editTask(taskItem)
                    } catch (e: TaskCreationException) {
                        displayErrorMsg(getString(R.string.errorTaskCreation) + e.toString())
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

    private fun validateAllInputs(): Boolean {
        var isValid = true
        val validator = InputValidator(context)

        val nameValidation =
            validator.validate(binding.tasknameinputedit)
        if (!nameValidation) {
            binding.tasknameinputedit.error = validator.errorCode
            isValid = false
        }

        val dateValidation =
            validator.validate(binding.taskdateinputedit)
        if (!dateValidation) {
            binding.taskdateinputedit.error = validator.errorCode
            isValid = false
        }

        val budgetValidation =
            validator.validate(binding.taskbudgetinputedit)
        if (!budgetValidation) {
            binding.taskbudgetinputedit.error = validator.errorCode
            isValid = false
        } else {
            val thisTaskBudgetSt =
                binding.taskbudgetinputedit.text.toString().replace("[^\\d.]".toRegex(), "")
            thisTaskBudget = thisTaskBudgetSt.toFloatOrNull() ?: 0.0f
        }

        val taskEvent = TaskDBHelper()
        val taskBudget = taskEvent.getTaskBudget(context)!!
        val newEventBalance =
            thisEventBudget - (taskBudget + thisTaskBudget)
        if (newEventBalance > 0) {
            showBanner(getString(R.string.banner_congrats), false)
        } else {
            AnalyticsManager.getInstance().trackError(
                SCREEN_NAME,
                "Exceeding Event Budget",
                newEventBalance.toString(),
                null
            )
            showBanner(getString(R.string.banner_beware), true)
            isValid = false
        }
        return isValid
    }

    private fun showDatePickerDialog() {
        val newFragment =
            DatePickerFragment.newInstance((DatePickerDialog.OnDateSetListener { _, p1, p2, p3 ->
                if (validateOldDate(p1, p2 + 1, p3)) {
                    val selectedDate = p3.toString() + "/" + (p2 + 1) + "/" + p1
                    binding.taskdateinputedit.setText(selectedDate)
                } else {
                    binding.taskdateinputedit.error = getString(R.string.error_invaliddate)
                }
            }))
        newFragment.show(parentFragmentManager, "datePicker")
    }

    private fun saveTask() {
        taskItem.name = binding.tasknameinputedit.text.toString()
        taskItem.date = binding.taskdateinputedit.text.toString()
        taskItem.budget = binding.taskbudgetinputedit.text.toString()
        taskItem.category = getCategory()

        if (!PermissionUtils.checkPermissions(requireActivity(), "calendar")) {
            val permissions = PermissionUtils.requestPermissionsList("calendar")
            requestPermissions(permissions, PERMISSION_CODE)
        } else {
            if (taskItem.key.isEmpty()) {
                //lifecycleScope.launch {
                try {
                    addTask(taskItem)
                } catch (e: TaskCreationException) {
                    AnalyticsManager.getInstance().trackError(
                        SCREEN_NAME,
                        e.message.toString(),
                        "addTask()",
                        e.stackTraceToString()
                    )
                    displayErrorMsg(getString(R.string.errorTaskCreation) + e.toString())
                    Log.e(TAG, e.message.toString())
                }
            } else {
                try {
                    editTask(taskItem)
                } catch (e: TaskCreationException) {
                    AnalyticsManager.getInstance().trackError(
                        SCREEN_NAME,
                        e.message.toString(),
                        "editTask()",
                        e.stackTraceToString()
                    )
                    displayErrorMsg(getString(R.string.errorTaskCreation) + e.toString())
                    Log.e(TAG, e.message.toString())
                }
            }

            //------------------------------------------------
            // Request User's feedback
            val reviewbox = RemoteConfigSingleton.get_reviewbox()
            if (reviewbox) {
                val reviewManager: ReviewManager = ReviewManagerFactory.create(requireActivity())

                val requestReviewTask: com.google.android.play.core.tasks.Task<ReviewInfo> =
                    reviewManager.requestReviewFlow()
                requestReviewTask.addOnCompleteListener { request ->
                    if (request.isSuccessful) {
                        // Request succeeded and a ReviewInfo instance was received
                        val reviewInfo: ReviewInfo = request.result
                        val launchReviewTask: com.google.android.play.core.tasks.Task<*> =
                            reviewManager.launchReviewFlow(requireActivity(), reviewInfo)
                        launchReviewTask.addOnCompleteListener {
                            // The review has finished, continue your app flow.
                        }
                    }
                }
            }
//            val showads = RemoteConfigSingleton.get_showads()
//            if (showads) {
//                if (adManager.mRewardedAd != null) {
//                    adManager.mRewardedAd?.show(requireActivity()) {}
//                } else {
//                    Log.d(TAG, "The rewarded ad wasn't ready yet.")
//                }
//            }
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
            PERMISSION_CODE -> {
                // Check if all permissions were granted
                if (grantResults.isNotEmpty() && grantResults[0] ==
                    PackageManager.PERMISSION_GRANTED
                ) {
                    binding.withdata.visibility = ConstraintLayout.INVISIBLE
                    binding.permissions.root.visibility = ConstraintLayout.VISIBLE
                    val calendarpermissions = Permission.getPermission("calendar")
                    val resourceId = this.resources.getIdentifier(
                        calendarpermissions.drawable, "drawable",
                        requireActivity().packageName
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
                        val uri = Uri.fromParts("package", requireActivity().packageName, null)
                        intent.data = uri
                        // Start the intent
                        startActivity(intent)
                    }
                }
            }
        }
    }

    private fun showBanner(message: String, dismiss: Boolean) {
        val fadeInAnimation = AnimationUtils.loadAnimation(requireActivity(), R.anim.fade_in)
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
        val language = context.resources.configuration.locales.get(0).language
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

    private fun displayErrorMsg(message: String) {
        Toast.makeText(
            context,
            message,
            Toast.LENGTH_LONG
        ).show()
    }

    fun finish() {
        val callingFragment = arguments?.getString("calling_fragment")
        val fragment = when (callingFragment) {
            "EventCategories" -> EventCategories()
            "TasksAllCalendar" -> DashboardActivity()
            "DashboardActivity" -> DashboardActivity()
            "TaskPaymentTasks" -> EventCategories()
            "EmptyState" -> EventCategories()
            else -> EventCategories()
        }
        Handler(Looper.getMainLooper()).postDelayed({
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit()
        }, 500)
    }

    companion object {
        const val TAG = "TaskCreateEdit"
        const val SCREEN_NAME = "Task_CreateEdit"
        internal const val PERMISSION_CODE = 42
    }
}


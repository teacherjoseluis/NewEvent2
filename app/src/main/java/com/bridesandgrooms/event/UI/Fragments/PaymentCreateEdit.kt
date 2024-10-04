package com.bridesandgrooms.event.UI.Fragments

import Application.AnalyticsManager
import Application.PaymentCreationException
import Application.PaymentDeletionException
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
import android.widget.*
import com.bridesandgrooms.event.MVP.VendorPaymentPresenter
import com.bridesandgrooms.event.Model.*
import com.bridesandgrooms.event.UI.Dialogs.DatePickerFragment
import com.google.android.material.chip.Chip
//import kotlinx.android.synthetic.main.payment_editdetail.*
import java.util.*
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.children
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.bridesandgrooms.event.AdManager

import com.bridesandgrooms.event.Functions.*
import com.bridesandgrooms.event.Functions.addPayment
import com.bridesandgrooms.event.Functions.editPayment
import com.bridesandgrooms.event.Functions.validateOldDate
import com.bridesandgrooms.event.R
import com.bridesandgrooms.event.UI.FieldValidators.InputValidator
import com.bridesandgrooms.event.databinding.PaymentEditdetailBinding
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import java.text.SimpleDateFormat

/*
* I don't think the vendor selection is saved to the payment
* */



class PaymentCreateEdit : Fragment(), VendorPaymentPresenter.VAVendors {

    private lateinit var optionsmenu: Menu
    private lateinit var adManager: AdManager
    private lateinit var binding: PaymentEditdetailBinding

    private lateinit var userSession: User
    private lateinit var paymentItem: Payment
    private lateinit var context: Context

    private lateinit var presentervendor: VendorPaymentPresenter
    private var paymentDate: Date = Date()

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
        userSession = User().getUser(context)

        binding = DataBindingUtil.inflate(inflater, R.layout.payment_editdetail, container, false)

        val toolbar = requireActivity().findViewById<MaterialToolbar>(R.id.toolbar)
        toolbar.findViewById<TextView>(R.id.appbartitle)?.text = getString(R.string.new_payment)

        val list = ArrayList(EnumSet.allOf(Category::class.java))
        val language = this.resources.configuration.locales.get(0).language

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
                        R.color.SecondaryContainer
                    )
                )
                setTextColor(ContextCompat.getColor(context, R.color.OnSecondaryContainer))
                chipCornerRadius = TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    8f,
                    resources.displayMetrics
                )
                rippleColor =
                    ColorStateList.valueOf(ContextCompat.getColor(context, R.color.OnPrimary))
                chipStrokeColor =
                    ColorStateList.valueOf(ContextCompat.getColor(context, R.color.Outline))
                chipStrokeWidth = TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    1f,
                    resources.displayMetrics
                ) // Assuming 1dp is defined in dimens.xml
                setTextAppearance(R.style.Body_Small)
            }
            binding.groupeditpayment.addView(chip)
        }

        paymentItem = arguments?.getParcelable("payment") ?: Payment()

        if (paymentItem .key.isNotEmpty()) {
            toolbar.findViewById<TextView>(R.id.appbartitle)?.text = getString(R.string.edit_guest)
            binding.paymentnameinputedit.setText(paymentItem.name)
            binding.paymentdateinputedit.setText(paymentItem.date)
            binding.paymentamountinputedit.setText(paymentItem.amount)

            val selectedChipId = binding.groupeditpayment.children
                .filterIsInstance<Chip>()
                .find { it.text == if (language == "en") list.find { category -> category.code == paymentItem.category }?.en_name else list.find { category -> category.code == paymentItem.category }?.es_name }
                ?.id

            selectedChipId?.let {
                binding.groupeditpayment.check(it)
            }
        }

        if (arguments?.containsKey("payment_date") == true) {
            paymentDate = arguments?.getSerializable("payment_date") as Date
            val formatter = SimpleDateFormat("dd/MM/yyyy")
            binding.paymentdateinputedit.setText(formatter.format(paymentDate))
            binding.paymentdateinputedit.isEnabled = false

        }

        binding.paymentnameinputedit.onFocusChangeListener = focusChangeListener
        binding.paymentdateinputedit.onFocusChangeListener = focusChangeListener
        binding.paymentdateinputedit.setOnClickListener {
            showDatePickerDialog()
        }
        binding.paymentamountinputedit.onFocusChangeListener = focusChangeListener

        binding.savebuttonpayment.setOnClickListener {
            AnalyticsManager.getInstance()
                .trackUserInteraction(GuestCreateEdit.SCREEN_NAME, "Save_Payment")
            val isValid = validateAllInputs()
            if (isValid) {
                savePayment()
            }
        }

        try {
            presentervendor = VendorPaymentPresenter(context, this)
            presentervendor.getVendorList()

        } catch (e: Exception) {
            displayToastMsg(getString(R.string.error_getting_vendors) + e.toString())
            AnalyticsManager.getInstance().trackError(SCREEN_NAME,e.message.toString(),"VendorPaymentPresenter",e.stackTraceToString())
            Log.e(TAG, e.message.toString())
        }

        val showads = RemoteConfigSingleton.get_showads()
        if (showads) {
            adManager = AdManagerSingleton.getAdManager()
            adManager.loadAndShowRewardedAd(requireActivity())
        }
        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        if (paymentItem.key.isNotEmpty()) {
            optionsmenu = menu
            inflater.inflate(R.menu.payments_menu, menu)
            optionsmenu.findItem(R.id.delete_payment).title = getString(R.string.delete_payment)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.delete_payment -> {
                AlertDialog.Builder(context)
                    .setTitle(getString(R.string.delete_message))
                    .setMessage(getString(R.string.delete_entry))
                    .setPositiveButton(
                        android.R.string.yes
                    ) { _, _ ->
                        AnalyticsManager.getInstance()
                            .trackUserInteraction(SCREEN_NAME, "Delete_Payment")
                        if (!PermissionUtils.checkPermissions(context, "calendar")) {
                            val permissions = PermissionUtils.requestPermissionsList("calendar")
                            requestPermissions(permissions, PERMISSION_CODE)
                        } else {
                            try {
                                deletePayment(context, userSession, paymentItem)
                            } catch (e: PaymentDeletionException) {
                                displayToastMsg(getString(R.string.errorPaymentDeletion) + e.toString())
                                AnalyticsManager.getInstance().trackError(
                                    SCREEN_NAME,
                                    e.message.toString(),
                                    "deletePayment()",
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
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }

    private fun validateAllInputs(): Boolean {
        var isValid = true
        val validator = InputValidator(context)

        val nameValidation =
            validator.validate(binding.paymentnameinputedit)
        if (!nameValidation) {
            binding.paymentnameinputedit.error = validator.errorCode
            isValid = false
        }

        val dateValidation =
            validator.validate(binding.paymentdateinputedit)
        if (!dateValidation) {
            binding.paymentdateinputedit.error = validator.errorCode
            isValid = false
        }

        val amountValidation =
            validator.validate(binding.paymentamountinputedit)
        if (!amountValidation) {
            binding.paymentamountinputedit.error = validator.errorCode
            isValid = false
        }

        return isValid
    }

    private fun showDatePickerDialog() {
        val newFragment =
            DatePickerFragment.newInstance((DatePickerDialog.OnDateSetListener { _, p1, p2, p3 ->
                if (validateOldDate(p1, p2 + 1, p3)) {
                    val selectedDate = p3.toString() + "/" + (p2 + 1) + "/" + p1
                    binding.paymentdateinputedit.setText(selectedDate)
                } else {
                    binding.paymentdateinputedit.error = getString(R.string.error_invaliddate)
                }
            }))
        newFragment.show(parentFragmentManager, "datePicker")
    }

    private fun savePayment() {
        paymentItem.name = binding.paymentnameinputedit.text.toString()
        paymentItem.date = binding.paymentdateinputedit.text.toString()
        paymentItem.amount = binding.paymentamountinputedit.text.toString()
        paymentItem.category =  getCategory()

        if (!PermissionUtils.checkPermissions(requireActivity(), "calendar")) {
            val permissions = PermissionUtils.requestPermissionsList("calendar")
            requestPermissions(permissions, PERMISSION_CODE)
        } else {
            if (paymentItem.key.isEmpty()) {
                try {
                    addPayment(context, userSession, paymentItem)
                } catch (e: PaymentCreationException) {
                    AnalyticsManager.getInstance().trackError(SCREEN_NAME, e.message.toString(),"addPayment()", e.stackTraceToString())
                    displayToastMsg(getString(R.string.errorPaymentCreation) + e.toString())
                    Log.e(TAG, e.message.toString())
                }
            } else {
                try {
                    editPayment(context, userSession, paymentItem)
                } catch (e: PaymentCreationException){
                    AnalyticsManager.getInstance().trackError(SCREEN_NAME, e.message.toString(),"editPayment()", e.stackTraceToString())
                    displayToastMsg(getString(R.string.errorPaymentCreation) + e.toString())
                    Log.e(TAG, e.message.toString())
                }
            }

            val showads = RemoteConfigSingleton.get_showads()
            if (showads) {
                if (adManager.mRewardedAd != null) {
                    adManager.mRewardedAd?.show(requireActivity()) {}
                } else {
                    Log.d(TaskCreateEdit.TAG, "The rewarded ad wasn't ready yet.")
                }
            }
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
                var allPermissionsGranted = true
                for (result in grantResults) {
                    if (result != PackageManager.PERMISSION_GRANTED) {
                        allPermissionsGranted = false
                        break
                    }
                }

                if (allPermissionsGranted) {
                    // All permissions were granted. Proceed with the desired functionality.
                    // For example, you can call a method that requires the permissions here.
                } else {
                    // At least one permission was denied.
                    // You can handle the denial scenario here, such as displaying a message or disabling functionality that requires the permissions.
                    // Here goes what happens when the permission is not given
                    binding.withdata.visibility = ConstraintLayout.INVISIBLE
                    binding.permissions.root.visibility = ConstraintLayout.VISIBLE
                    val calendarpermissions = Permission.getPermission("calendar")
                    val resourceId = this.resources.getIdentifier(
                        calendarpermissions.drawable, "drawable",
                        requireActivity().packageName
                    )
                    binding.permissions.root.findViewById<ImageView>(R.id.permissionicon).setImageResource(resourceId)

                    val language = this.resources.configuration.locales.get(0).language
                    val permissionwording = when (language) {
                        "en" -> calendarpermissions.permission_wording_en
                        else -> calendarpermissions.permission_wording_es
                    }
                    binding.permissions.root.findViewById<TextView>(R.id.permissionwording).text = permissionwording

                    val openSettingsButton = binding.permissions.root.findViewById<Button>(R.id.permissionsbutton)
                    openSettingsButton.setOnClickListener {
                        // Create an intent to open the app settings for your app
                        val intent = Intent()
                        intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                        val packageName = requireActivity().packageName
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



    private fun getCategory(): String {
        val categoryName =
            binding.groupeditpayment.findViewById<Chip>(binding.groupeditpayment.checkedChipId).text.toString()
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

    override fun onVAVendors(list: ArrayList<String>) {
        list.add(0, getString(R.string.selectvendor))
        val adapter =
            ArrayAdapter(context, android.R.layout.simple_dropdown_item_1line, list)
        binding.vendorAutocomplete.setAdapter(adapter)
        if (paymentItem.vendorid != "") {
            try {
                val vendordb = VendorDBHelper(context)
                val vendorname = vendordb.getVendorNameById(paymentItem.vendorid)
                binding.vendorAutocomplete.setSelection(list.indexOf(vendorname))
            } catch (e: Exception) {
                displayToastMsg(getString(R.string.error_getting_vendors) + e.toString())
                AnalyticsManager.getInstance().trackError(SCREEN_NAME,e.message.toString(),"getVendorNameById",e.stackTraceToString())
                Log.e(TAG, e.message.toString())
            }
        } else {
            binding.vendorAutocomplete.setSelection(0)
        }
    }

    override fun onVAVendorsError(errcode: String) {
        //This absolutely needs to be handled as it has been generating very nasty exceptions
        binding.vendorpaymentsection.visibility = View.GONE
    }

    private fun displayToastMsg(message: String) {
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
        const val TAG = "PaymentCreateEdit"
        const val SCREEN_NAME = "Payment_CreateEdit"
        internal const val PERMISSION_CODE = 42
    }
}

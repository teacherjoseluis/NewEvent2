package com.bridesandgrooms.event

import Application.AnalyticsManager
import Application.PaymentCreationException
import Application.PaymentDeletionException
import android.app.Activity
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
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.bridesandgrooms.event.MVP.VendorPaymentPresenter
import com.bridesandgrooms.event.Model.*
import com.bridesandgrooms.event.UI.FieldValidators.TextValidate
import com.bridesandgrooms.event.UI.Dialogs.DatePickerFragment
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
//import kotlinx.android.synthetic.main.payment_editdetail.*
import java.util.*
import android.view.View.OnFocusChangeListener
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil

import com.bridesandgrooms.event.Functions.*
import com.bridesandgrooms.event.Functions.addPayment
import com.bridesandgrooms.event.Functions.editPayment
import com.bridesandgrooms.event.Functions.validateOldDate
import com.bridesandgrooms.event.databinding.PaymentEditdetailBinding

class PaymentCreateEdit : AppCompatActivity(), VendorPaymentPresenter.VAVendors {

    private lateinit var paymentItem: Payment
    private lateinit var optionsmenu: Menu
    private lateinit var presentervendor: VendorPaymentPresenter
    private lateinit var adManager: AdManager
    private lateinit var binding: PaymentEditdetailBinding
    private lateinit var userSession: User

    private var language = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.payment_editdetail)

        userSession = User().getUser(this)
        val taskhelper = TaskDBHelper(this)
        val tasklist = taskhelper.getTasksNames()

        val vendorpaymentsection = findViewById<LinearLayout>(R.id.vendorpaymentsection)
        language = this.resources.configuration.locales.get(0).language
        val list = ArrayList(EnumSet.allOf(Category::class.java))
        val chipgroupedit = findViewById<ChipGroup>(R.id.groupeditpayment)
        val itemsAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, tasklist!!)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        val apptitle = findViewById<TextView>(R.id.appbartitle)
        setSupportActionBar(toolbar)
        supportActionBar!!.setHomeAsUpIndicator(R.drawable.icons8_left_24)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        val extras = intent.extras
        if (extras!!.containsKey("payment")) {
            apptitle.text = getString(R.string.edit_payment)
            paymentItem = intent.getParcelableExtra("payment")!!
        } else {
            apptitle.text = getString(R.string.new_payment)
            paymentItem = Payment()
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
            if (paymentItem.category == category.code) {
                chip.isSelected = true
                chipgroupedit.check(chip.id)
            }
        }

        binding.paymentname.setAdapter(itemsAdapter)
//        binding.paymentname.onFocusChangeListener = OnFocusChangeListener { _, p1 ->
//            if (!p1) {
//                binding.paymentname.hint = ""
//                val validationmessage = TextValidate(binding.paymentname).nameFieldValidate()
//                if (validationmessage != "") {
//                    binding.paymentname.error =
//                        getString(R.string.error_in_payment_name, validationmessage)
//                }
//            }
//        }

        binding.paymentamount.setOnClickListener {
            AnalyticsManager.getInstance().trackUserInteraction(SCREEN_NAME, "Payment_Amount")
            binding.paymentamount.error = null
        }

        binding.paymentdate.setOnClickListener {
            AnalyticsManager.getInstance().trackUserInteraction(SCREEN_NAME, "Payment_Date")
            binding.paymentdate.error = null
            showDatePickerDialog()
        }

        if (paymentItem.key != "") {
            binding.paymentname.setText(paymentItem.name)
            binding.paymentdate.setText(paymentItem.date)
            binding.paymentamount.setText(paymentItem.amount)
        }

        binding.savebuttonpayment.setOnClickListener {
            AnalyticsManager.getInstance().trackUserInteraction(SCREEN_NAME, "Save_Payment")
            var inputvalflag = true
            binding.paymentname.clearFocus()
            if (binding.paymentname.text.toString().isEmpty()) {
                binding.paymentname.error = getString(R.string.error_tasknameinput)
                inputvalflag = false
            } else {
//                val validationmessage = TextValidate(binding.paymentname).nameFieldValidate()
//                if (validationmessage != "") {
//                    binding.paymentname.error =
//                        getString(R.string.error_in_payment_name, validationmessage)
//                    inputvalflag = false
//                }
            }
            binding.paymentdate.clearFocus()
            if (binding.paymentdate.text.toString().isEmpty()) {
                binding.paymentdate.error = getString(R.string.error_taskdateinput)
                inputvalflag = false
            }
            binding.paymentamount.clearFocus()
            if (binding.paymentamount.text.toString().isEmpty()) {
                binding.paymentamount.error = getString(R.string.error_taskbudgetinput)
                inputvalflag = false
            }
            if (binding.groupeditpayment.checkedChipId == -1) {
                Toast.makeText(
                    this,
                    getString(R.string.error_taskcategoryinput),
                    Toast.LENGTH_SHORT
                ).show()
                inputvalflag = false
            }

            if (vendorpaymentsection.visibility != View.GONE) {
                val actv = findViewById<Spinner>(R.id.vendorspinner)
                if (actv.selectedItem.toString() != getString(R.string.selectvendor)) {
                    val vendordb = VendorDBHelper(this)
                    val vendorid = vendordb.getVendorIdByName(actv.selectedItem.toString())!!
                    if (vendorid.isEmpty()) {
                        Toast.makeText(this, getString(R.string.vendornotfound), Toast.LENGTH_SHORT)
                            .show()
                        inputvalflag = false
                    } else {
                        paymentItem.vendorid = vendorid
                    }
                }
            }
            if (inputvalflag) {
                savePayment()
            }
        }

        try {
            presentervendor = VendorPaymentPresenter(this, this)
            presentervendor.getVendorList()

        } catch (e: Exception) {
            displayToastMsg(getString(R.string.error_getting_vendors) + e.toString())
            AnalyticsManager.getInstance().trackError(SCREEN_NAME,e.message.toString(),"VendorPaymentPresenter",e.stackTraceToString())
            Log.e(TAG, e.message.toString())
        }

        val showads = RemoteConfigSingleton.get_showads()
        if (showads) {
            adManager = AdManagerSingleton.getAdManager()
            adManager.loadAndShowRewardedAd(this)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        if (paymentItem.key != "") {
            optionsmenu = menu
            menuInflater.inflate(R.menu.payments_menu, menu)
            optionsmenu.findItem(R.id.delete_payment).title = getString(R.string.delete_payment)
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.delete_payment -> {
                if (!PermissionUtils.checkPermissions(applicationContext, "calendar")) {
                    val permissions = PermissionUtils.requestPermissionsList("calendar")
                    requestPermissions(permissions, PERMISSION_CODE)
                } else {
                    //lifecycleScope.launch {
                        try {
                            deletePayment(this@PaymentCreateEdit, userSession, paymentItem)
                            disableControls()
                        } catch(e: PaymentDeletionException) {
                            displayToastMsg(getString(R.string.errorPaymentDeletion) + e.toString())
                            AnalyticsManager.getInstance().trackError(SCREEN_NAME,e.message.toString(),"deletePayment()",e.stackTraceToString())
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
        binding.paymentname.isEnabled = false
        binding.paymentamount.isEnabled = false
        binding.paymentdate.isEnabled = false
        binding.groupeditpayment.isEnabled = false
        binding.savebuttonpayment.isEnabled = false
        optionsmenu.clear()

        setResult(Activity.RESULT_OK, Intent())
        //delay(1500) // Replace Thread.sleep with delay from coroutines
        finish()
    }

    private fun getCategory(): String {
        val categoryName =
            binding.groupeditpayment.findViewById<Chip>(binding.groupeditpayment.checkedChipId).text.toString()
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

    private fun showDatePickerDialog() {
        val newFragment =
            DatePickerFragment.newInstance((DatePickerDialog.OnDateSetListener { _, p1, p2, p3 ->
                if (validateOldDate(p1, p2 + 1, p3)) {
                    val selectedDate = p3.toString() + "/" + (p2 + 1) + "/" + p1
                    binding.paymentdate.setText(selectedDate)
                } else {
                    binding.paymentdate.error = getString(R.string.error_invaliddate)
                }
            }))
        newFragment.show(supportFragmentManager, "datePicker")
    }

    private fun savePayment() {
        paymentItem.name = binding.paymentname.text.toString()
        paymentItem.date = binding.paymentdate.text.toString()
        paymentItem.amount = binding.paymentamount.text.toString()
        paymentItem.category =  getCategory()

        if (!PermissionUtils.checkPermissions(this, "calendar")) {
            val permissions = PermissionUtils.requestPermissionsList("calendar")
            requestPermissions(permissions, PERMISSION_CODE)
        } else {
            if (paymentItem.key.isEmpty()) {
                try {
                    addPayment(this, userSession, paymentItem)
                } catch (e: PaymentCreationException) {
                    AnalyticsManager.getInstance().trackError(SCREEN_NAME, e.message.toString(),"addPayment()", e.stackTraceToString())
                    displayToastMsg(getString(R.string.errorPaymentCreation) + e.toString())
                    Log.e(TAG, e.message.toString())
                }
            } else {
                try {
                    editPayment(this, userSession, paymentItem)
                } catch (e: PaymentCreationException){
                    AnalyticsManager.getInstance().trackError(SCREEN_NAME, e.message.toString(),"editPayment()", e.stackTraceToString())
                    displayToastMsg(getString(R.string.errorPaymentCreation) + e.toString())
                    Log.e(TAG, e.message.toString())
                }
            }
            val resultIntent = Intent()
            setResult(Activity.RESULT_OK, resultIntent)

            val showads = RemoteConfigSingleton.get_showads()
            if (showads) {
                if (adManager.mRewardedAd != null) {
                    adManager.mRewardedAd?.show(this) {}
                } else {
                    Log.d(TaskCreateEdit.TAG, "The rewarded ad wasn't ready yet.")
                }
            }
            //Thread.sleep(1500)
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
                        this.packageName
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

    override fun onVAVendors(list: ArrayList<String>) {
        list.add(0, getString(R.string.selectvendor))
        val actv = findViewById<Spinner>(R.id.vendorspinner)
        val adapteractv =
            ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, list)
        actv.adapter = adapteractv
        if (paymentItem.vendorid != "") {
            try {
                val vendordb = VendorDBHelper(this)
                val vendorname = vendordb.getVendorNameById(paymentItem.vendorid)
                actv.setSelection(list.indexOf(vendorname))
            } catch (e: Exception) {
                displayToastMsg(getString(R.string.error_getting_vendors) + e.toString())
                AnalyticsManager.getInstance().trackError(SCREEN_NAME,e.message.toString(),"getVendorNameById",e.stackTraceToString())
                Log.e(TAG, e.message.toString())
            }
        } else {
            actv.setSelection(0)
        }
    }

    override fun onVAVendorsError(errcode: String) {
        //This absolutely needs to be handled as it has been generating very nasty exceptions
        val actv = findViewById<LinearLayout>(R.id.vendorpaymentsection)
        actv.visibility = View.GONE
    }

    private fun displayToastMsg(message: String) {
        Toast.makeText(
            this@PaymentCreateEdit,
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
        const val TAG = "PaymentCreateEdit"
        const val SCREEN_NAME = "Payment_CreateEdit"
        internal const val PERMISSION_CODE = 42
    }
}

package com.bridesandgrooms.event

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
import com.bridesandgrooms.event.UI.TextValidate
import com.bridesandgrooms.event.UI.dialog.DatePickerFragment
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
//import kotlinx.android.synthetic.main.payment_editdetail.*
import java.util.*
import android.view.View.OnFocusChangeListener
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil

import androidx.lifecycle.lifecycleScope
import com.bridesandgrooms.event.Functions.*
import com.bridesandgrooms.event.Functions.addPayment
import com.bridesandgrooms.event.Functions.editPayment
import com.bridesandgrooms.event.Functions.validateOldDate
import com.bridesandgrooms.event.databinding.PaymentEditdetailBinding
//import kotlinx.android.synthetic.main.task_editdetail.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class PaymentCreateEdit : AppCompatActivity(), VendorPaymentPresenter.VAVendors {

    private lateinit var paymentitem: Payment
    private lateinit var optionsmenu: Menu
    private lateinit var presentervendor: VendorPaymentPresenter
    private lateinit var adManager: AdManager
    private lateinit var binding: PaymentEditdetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.payment_editdetail)

        // Declaring and enabling the toolbar
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.setHomeAsUpIndicator(R.drawable.icons8_left_24)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        val apptitle = findViewById<TextView>(R.id.appbartitle)

        val extras = intent.extras
        if (extras!!.containsKey("payment")) {
            apptitle.text = getString(R.string.edit_payment)
        } else {
            apptitle.text = getString(R.string.new_payment)
        }

        // Payment item will be blank if nothing comes in the extras
        // but if something comes it will assume this is an edit
        paymentitem = if (extras!!.containsKey("payment")) {
            intent.getParcelableExtra("payment")!!
        } else {
            Payment()
        }

        //Calling the presenter for Vendors that will be used to associate the Payment to a Vendor
        try {
            presentervendor = VendorPaymentPresenter(this, this)
        } catch (e: Exception) {
            println(e.message)
        }

        //We are making sure that only valid text is entered in the name of the payment
        val taskhelper = TaskDBHelper(this)
        val tasklist = taskhelper.getTasksNames()
        val itemsAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, tasklist!!)

        binding.paymentname.setAdapter(itemsAdapter)
        binding.paymentname.onFocusChangeListener = OnFocusChangeListener { _, p1 ->
            if (!p1) {
                binding.paymentname.hint = ""
                val validationmessage = TextValidate(binding.paymentname).namefieldValidate()
                if (validationmessage != "") {
                    binding.paymentname.error = "Error in Payment name: $validationmessage"
                }
            }
        }

        //Erasing any possible error whenever the user clicks again
        binding.paymentamount.setOnClickListener {
            binding.paymentamount.error = null
        }

        binding.paymentdate.setOnClickListener {
            binding.paymentdate.error = null
            showDatePickerDialog()
        }

        val chipgroupedit = findViewById<ChipGroup>(R.id.groupeditpayment)

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
            if (paymentitem.category == category.code) {
                chip.isSelected = true
                chipgroupedit.check(chip.id)
            }
        }

        //This is simply to clear the vendor in case the user clicks on this field
//        vendorautocomplete.setOnClickListener {
        // Here it is where the text clearing takes place
//            if (vendorautocomplete.text.toString().isNotEmpty()) {
//                AlertDialog.Builder(this)
//                    .setTitle(getString(R.string.clearvendor))
//                    .setMessage(getString(R.string.clearvendor_warning)) // Specifying a listener allows you to take an action before dismissing the dialog.
//                    // The dialog is automatically dismissed when a dialog button is clicked.
//                    .setPositiveButton(android.R.string.yes
//                    ) { _, _ ->
//                        vendorautocomplete.setText("")
//                        finish()
//                    } // A null listener allows the button to dismiss the dialog and take no further action.
//                    .setNegativeButton(android.R.string.no, null)
//                    .setIcon(android.R.drawable.ic_dialog_alert)
//                    .show()
//            }
//        }

        if (paymentitem.key != "") {
            binding.paymentname.setText(paymentitem.name)
            binding.paymentdate.setText(paymentitem.date)
            binding.paymentamount.setText(paymentitem.amount)
        }

        binding.savebuttonpayment.setOnClickListener {
            var inputvalflag = true
            binding.paymentname.clearFocus()
            if (binding.paymentname.text.toString().isEmpty()) {
                binding.paymentname.error = getString(R.string.error_tasknameinput)
                inputvalflag = false
            } else {
                val validationmessage = TextValidate(binding.paymentname).namefieldValidate()
                if (validationmessage != "") {
                    binding.paymentname.error = "Error in Payment name: $validationmessage"
                    inputvalflag = false
                }
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
                Toast.makeText(this, "Category is required!", Toast.LENGTH_SHORT).show()
                inputvalflag = false
            }

            // Check that if the vendor is set, that vendor exists
            val vendorpaymentsection = findViewById<LinearLayout>(R.id.vendorpaymentsection)
            if (vendorpaymentsection.visibility != View.GONE) {
                val actv = findViewById<Spinner>(R.id.vendorspinner)
                if (actv.selectedItem.toString() != getString(R.string.selectvendor)) {
                    val vendordb = VendorDBHelper(this)
                    val vendorid = vendordb.getVendorIdByName(actv.selectedItem.toString())!!
                    if (vendorid == "") {
                        Toast.makeText(this, getString(R.string.vendornotfound), Toast.LENGTH_SHORT)
                            .show()
                        inputvalflag = false
                    } else {
                        paymentitem.vendorid = vendorid
                    }
                }
            }
            if (inputvalflag) {
                savePayment()
            }
        }

        // Loading the Ad
//        val adRequest = AdRequest.Builder().build()
//        RewardedAd.load(
//            this,
//            "ca-app-pub-3940256099942544/5224354917",
//            adRequest,
//            object : RewardedAdLoadCallback() {
//                override fun onAdFailedToLoad(adError: LoadAdError) {
//                    Log.d(TaskCreateEdit.TAG, adError.message)
//                    mRewardedAd = null
//                }
//
//                override fun onAdLoaded(rewardedAd: RewardedAd) {
//                    Log.d(TaskCreateEdit.TAG, "Ad was loaded.")
//                    mRewardedAd = rewardedAd
//                }
//            })
//
//        // Ad listener in case I want to add additional behavior
//        mRewardedAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
//            override fun onAdShowedFullScreenContent() {
//                // Called when ad is shown.
//                Log.d(TaskCreateEdit.TAG, "Ad was shown.")
//            }
//
//            override fun onAdFailedToShowFullScreenContent(adError: AdError?) {
//                // Called when ad fails to show.
//                Log.d(TaskCreateEdit.TAG, "Ad failed to show.")
//            }
//
//            override fun onAdDismissedFullScreenContent() {
//                // Called when ad is dismissed.
//                // Set the ad reference to null so you don't show the ad a second time.
//                Log.d(TaskCreateEdit.TAG, "Ad was dismissed.")
//                mRewardedAd = null
//            }
//        }
        val showads = RemoteConfigSingleton.get_showads()

        if (showads) {
            adManager = AdManagerSingleton.getAdManager()
            adManager.loadAndShowRewardedAd(this)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        if (paymentitem.key != "") {
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
                    PermissionUtils.alertBox(this, "calendar")
                } else {
                    lifecycleScope.launch {
                        deletePayment(this@PaymentCreateEdit, paymentitem)
                        disableControls()
                    }
                }
//                val resultIntent = Intent()
//                setResult(Activity.RESULT_OK, resultIntent)
//                    Thread.sleep(1500)
//                    finish()
//                    super.onOptionsItemSelected(item)
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }

    private suspend fun disableControls() {
        binding.paymentname.isEnabled = false
        binding.paymentamount.isEnabled = false
        binding.paymentdate.isEnabled = false
        binding.groupeditpayment.isEnabled = false
        binding.savebuttonpayment.isEnabled = false
        optionsmenu.clear()

        setResult(Activity.RESULT_OK, Intent())
        delay(1500) // Replace Thread.sleep with delay from coroutines
        finish()
    }

    // Repeated from TaskCreateEdit
    private fun getCategory(): String {
        var mycategorycode = ""
        val categoryname = binding.groupeditpayment.findViewById<Chip>(binding.groupeditpayment.checkedChipId).text

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
                    binding.paymentdate.setText(selectedDate)
                } else {
                    binding.paymentdate.error = getString(R.string.error_invaliddate)
                }
            }))
        newFragment.show(supportFragmentManager, "datePicker")
    }

    private fun savePayment() {
        paymentitem.name = binding.paymentname.text.toString()
        paymentitem.date = binding.paymentdate.text.toString()
        paymentitem.amount = binding.paymentamount.text.toString()
        paymentitem.category = getCategory()

//        if (!checkPaymentPermissions()) {
//            alertBox()
        if (!PermissionUtils.checkPermissions(applicationContext, "calendar")) {
            PermissionUtils.alertBox(this, "calendar")
        } else {
            if (paymentitem.key == "") {
                addPayment(this, paymentitem)
            } else if (paymentitem.key != "") {
                editPayment(this, paymentitem)
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
            Thread.sleep(1500)
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
//            requestPaymentPermissions()
//        }
//        builder.setNegativeButton(
//            "Cancel"
//        ) { p0, _ -> p0!!.dismiss() }
//
//        val dialog = builder.create()
//        dialog.show()
//    }
//
//    private fun checkPaymentPermissions(): Boolean {
//        return !((checkSelfPermission(Manifest.permission.READ_CALENDAR) ==
//                PackageManager.PERMISSION_DENIED
//                ) && (checkSelfPermission(Manifest.permission.WRITE_CALENDAR) ==
//                PackageManager.PERMISSION_DENIED
//                ) && (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) ==
//                PackageManager.PERMISSION_DENIED
//                ) && (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
//                PackageManager.PERMISSION_DENIED
//                ))
//    }
//
//    private fun requestPaymentPermissions() {
//        val permissions =
//            arrayOf(
//                Manifest.permission.READ_CALENDAR,
//                Manifest.permission.WRITE_CALENDAR,
//                Manifest.permission.READ_EXTERNAL_STORAGE,
//                Manifest.permission.WRITE_EXTERNAL_STORAGE
//            )
//        //show popup to request runtime permission
//        requestPermissions(permissions, PERMISSION_CODE)
//    }

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
        if (paymentitem.vendorid != "") {
            try {
                val vendordb = VendorDBHelper(this)
                val vendorname = vendordb.getVendorNameById(paymentitem.vendorid)
                actv.setSelection(list.indexOf(vendorname))
            } catch (e: Exception) {
            }
        } else {
            actv.setSelection(0)
        }
        //actv.setTextColor(Color.RED)
    }

    override fun onVAVendorsError(errcode: String) {
        //This absolutely needs to be handled as it has been generating very nasty exceptions
        val actv = findViewById<LinearLayout>(R.id.vendorpaymentsection)
        actv.visibility = View.GONE
    }

    override fun finish() {
        val returnintent = Intent()
        setResult(RESULT_OK, returnintent)
        super.finish()
    }

    companion object {
        const val TAG = "PaymentCreateEdit"
        internal const val PERMISSION_CODE = 42
    }
}

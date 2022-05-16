package com.example.newevent2

import android.Manifest
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.newevent2.Functions.addPayment
import com.example.newevent2.Functions.editPayment
import com.example.newevent2.Functions.validateOldDate
import com.example.newevent2.MVP.VendorPaymentPresenter
import com.example.newevent2.Model.*
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
import kotlinx.android.synthetic.main.payment_editdetail.*
import java.util.*

class PaymentCreateEdit : AppCompatActivity(), VendorPaymentPresenter.VAVendors {

    private lateinit var paymentitem: Payment
    private lateinit var presentervendor: VendorPaymentPresenter
    private var mRewardedAd: RewardedAd? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.payment_editdetail)

        // Declaring and enabling the toolbar
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.setHomeAsUpIndicator(R.drawable.icons8_left_24)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

       //Calling the presenter for Vendors that will be used to associate the Payment to a Vendor
        presentervendor = VendorPaymentPresenter(this, this)

        //Paymentitem will be blank if nothing comes in the extras
        // but if something comes it will assume this is an edit
        val extras = intent.extras
        paymentitem = if (extras!!.containsKey("payment")) {
            intent.getParcelableExtra("payment")!!
        } else {
            Payment()
        }

        //We are making sure that only valid text is entered in the name of the payment
        paymentname.onFocusChangeListener = View.OnFocusChangeListener { _, p1 ->
            if (!p1) {
                val validationmessage = TextValidate(paymentname).namefieldValidate()
                if (validationmessage != "") {
                    paymentname.error = "Error in Payment name: $validationmessage"
                }
            }
        }

        //Erasing any possible error whenever the user clicks again
        paymentamount.setOnClickListener {
            paymentamount.error = null
        }

        paymentdate.setOnClickListener {
            paymentdate.error = null
            showDatePickerDialog()
        }

        val chipgroupedit = findViewById<ChipGroup>(R.id.groupeditpayment)

        // Create chips and select the one matching the category
        val list = ArrayList<Category>(EnumSet.allOf(Category::class.java))
        for (category in list) {
            val chip = Chip(this)
            chip.text = category.en_name
            chip.isClickable = true
            chip.isCheckable = true
            chipgroupedit.addView(chip)
            if (paymentitem.category == category.code) {
                chip.isSelected = true
                chipgroupedit.check(chip.id)
            }
        }

        //This is simply to clear the vendor in case the user clicks on this field
        vendorautocomplete.setOnClickListener {
            // Here it is where the text clearing takes place
            if (vendorautocomplete.text.toString().isNotEmpty()) {
                AlertDialog.Builder(this)
                    .setTitle(getString(R.string.clearvendor))
                    .setMessage(getString(R.string.clearvendor_warning)) // Specifying a listener allows you to take an action before dismissing the dialog.
                    // The dialog is automatically dismissed when a dialog button is clicked.
                    .setPositiveButton(android.R.string.yes
                    ) { _, _ ->
                        vendorautocomplete.setText("")
                        finish()
                    } // A null listener allows the button to dismiss the dialog and take no further action.
                    .setNegativeButton(android.R.string.no, null)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show()
            }
        }

        if (paymentitem.key != "") {
            paymentname.setText(paymentitem.name)
            paymentdate.setText(paymentitem.date)
            paymentamount.setText(paymentitem.amount)
        }

        savebuttonpayment.setOnClickListener {
            var inputvalflag = true
            paymentname.clearFocus()
            if (paymentname.text.toString().isEmpty()) {
                paymentname.error = getString(R.string.error_tasknameinput)
                inputvalflag = false
            } else {
                val validationmessage = TextValidate(paymentname).namefieldValidate()
                if (validationmessage != "") {
                    paymentname.error = "Error in Payment name: $validationmessage"
                    inputvalflag = false
                }
            }
            paymentdate.clearFocus()
            if (paymentdate.text.toString().isEmpty()) {
                paymentdate.error = getString(R.string.error_taskdateinput)
                inputvalflag = false
            }
            paymentamount.clearFocus()
            if (paymentamount.text.toString().isEmpty()) {
                paymentamount.error = getString(R.string.error_taskbudgetinput)
                inputvalflag = false
            }
            if (groupeditpayment.checkedChipId == -1) {
                Toast.makeText(this, "Category is required!", Toast.LENGTH_SHORT).show()
                inputvalflag = false
            }
            // Check that if the vendor is set, that vendor exists
            if (vendorautocomplete.text.toString().isNotEmpty()) {
                val vendordb = VendorDBHelper(this)
                val vendorid = vendordb.getVendorIdByName(vendorautocomplete.text.toString())
                if (vendorid == "") {
                    Toast.makeText(this, getString(R.string.vendornotfound), Toast.LENGTH_SHORT).show()
                    inputvalflag = false
                } else {
                    paymentitem.vendorid = vendorid
                }
            }
            if (inputvalflag) {
                savePayment()
            }
        }

        // Loading the Ad
        val adRequest = AdRequest.Builder().build()
        RewardedAd.load(this,"ca-app-pub-3940256099942544/5224354917", adRequest, object : RewardedAdLoadCallback() {
            override fun onAdFailedToLoad(adError: LoadAdError) {
                Log.d(TaskCreateEdit.TAG, adError.message)
                mRewardedAd = null
            }

            override fun onAdLoaded(rewardedAd: RewardedAd) {
                Log.d(TaskCreateEdit.TAG, "Ad was loaded.")
                mRewardedAd = rewardedAd
            }
        })

        // Ad listener in case I want to add additional behavior
        mRewardedAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdShowedFullScreenContent() {
                // Called when ad is shown.
                Log.d(TaskCreateEdit.TAG, "Ad was shown.")
            }

            override fun onAdFailedToShowFullScreenContent(adError: AdError?) {
                // Called when ad fails to show.
                Log.d(TaskCreateEdit.TAG, "Ad failed to show.")
            }

            override fun onAdDismissedFullScreenContent() {
                // Called when ad is dismissed.
                // Set the ad reference to null so you don't show the ad a second time.
                Log.d(TaskCreateEdit.TAG, "Ad was dismissed.")
                mRewardedAd = null
            }
        }
    }

    // Repeated from TaskCreateEdit
    private fun getCategory(): String {
        var mycategorycode = ""
        val categoryname = groupeditpayment.findViewById<Chip>(groupeditpayment.checkedChipId).text

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
                    paymentdate.setText(selectedDate)
                } else {
                    paymentdate.error = getString(R.string.error_invaliddate)
                }
            }))
        newFragment.show(supportFragmentManager, "datePicker")
    }

    private fun savePayment() {
        paymentitem.name = paymentname.text.toString()
        paymentitem.date = paymentdate.text.toString()
        paymentitem.amount = paymentamount.text.toString()
        paymentitem.category = getCategory()

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
            requestPermissions(permissions, TaskCreateEdit.PERMISSION_CODE)
        } else {
            if (paymentitem.key == "") {
                addPayment(this, paymentitem)
            } else if (paymentitem.key != "") {
                editPayment(this, paymentitem)
            }
        }
        if (mRewardedAd != null) {
            mRewardedAd?.show(this) {}
        } else {
            Log.d(TaskCreateEdit.TAG, "The rewarded ad wasn't ready yet.")
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    override fun onVAVendors(list: ArrayList<String>) {
        val adapteractv = ArrayAdapter<String>(this, android.R.layout.select_dialog_item, list)
        val actv = findViewById<AutoCompleteTextView>(R.id.vendorautocomplete)
        actv.threshold = 1
        actv.setAdapter(adapteractv)
        actv.setTextColor(Color.RED)
    }

    override fun onVAVendorsError(errcode: String) {
        //This absolutely needs to be handled as it has been generating very nasty exceptions
        val actv = findViewById<LinearLayout>(R.id.vendorpaymentsection)
        actv.visibility = View.GONE
    }

    companion object {
        const val TAG = "PaymentCreateEdit"
    }
}

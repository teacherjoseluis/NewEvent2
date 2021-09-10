package com.example.newevent2

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.DatePicker
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.newevent2.Functions.addPayment
import com.example.newevent2.Functions.deleteGuest
import com.example.newevent2.Functions.editPayment
import com.example.newevent2.Functions.validateOldDate
import com.example.newevent2.MVP.VendorPaymentPresenter
import com.example.newevent2.Model.*
import com.example.newevent2.ui.TextValidate
import com.example.newevent2.ui.dialog.DatePickerFragment
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import kotlinx.android.synthetic.main.payment_editdetail.*
import java.util.*

class PaymentCreateEdit() : AppCompatActivity(), VendorPaymentPresenter.VAVendors {

    private lateinit var paymentitem: Payment
    private lateinit var presentervendor: VendorPaymentPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.payment_editdetail)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.setHomeAsUpIndicator(R.drawable.icons8_left_24)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        //---------------------------------------------------------------------------------------
        presentervendor = VendorPaymentPresenter(this!!, this)
        //---------------------------------------------------------------------------------------


        val extras = intent.extras
        paymentitem = if (extras!!.containsKey("payment")) {
            intent.getParcelableExtra("payment")!!
        } else {
            Payment()
        }

//        val paymentid = paymentitem.key
//
//        if (paymentid != "") {
//            val paymentmodel = PaymentModel()
//            val user = com.example.newevent2.Functions.getUserSession(applicationContext!!)
//            paymentmodel.getPaymentdetail(user.key, user.eventid, paymentid, object :
//                PaymentModel.FirebaseSuccessPayment {
//                @RequiresApi(Build.VERSION_CODES.O)
//                override fun onPayment(payment: Payment) {
//                    paymentname.setText(payment.name)
//                    paymentdate.setText(payment.date)
//                    paymentamount.setText(payment.amount)
//
//                    paymentitem.key = payment.key
//                    paymentitem.amount = payment.amount
//                    paymentitem.category = payment.category
//                    paymentitem.date = payment.date
//                    paymentitem.name = payment.name
//                    paymentitem.createdatetime = payment.createdatetime
//                }
//            })
//        }

        paymentname.onFocusChangeListener = View.OnFocusChangeListener { _, p1 ->
            if (!p1) {
                val validationmessage = TextValidate(paymentname).namefieldValidate()
                if (validationmessage != "") {
                    paymentname.error = "Error in Payment name: $validationmessage"
                }
            }
        }

        paymentamount.setOnClickListener {
            paymentamount.error = null
        }

        paymentdate.setOnClickListener {
            paymentdate.error = null
            showDatePickerDialog()
        }

        val chipgroupedit = findViewById<ChipGroup>(R.id.groupeditpayment)
        //chipgroupedit.isSingleSelection = true

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

        vendorautocomplete.setOnClickListener {
            // Here it is where the text clearing takes place
            if (vendorautocomplete.text.toString().isNotEmpty()) {
                AlertDialog.Builder(this)
                    .setTitle("Clear vendor")
                    .setMessage("Are you sure you want to clear the vendor for this payment?") // Specifying a listener allows you to take an action before dismissing the dialog.
                    // The dialog is automatically dismissed when a dialog button is clicked.
                    .setPositiveButton(android.R.string.yes,
                        DialogInterface.OnClickListener { dialog, which ->
                            vendorautocomplete.setText("")
                            finish()
                        }) // A null listener allows the button to dismiss the dialog and take no further action.
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
                paymentname.error = "Payment name is required!"
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
                paymentdate.error = "Payment date is required!"
                inputvalflag = false
            }
            paymentamount.clearFocus()
            if (paymentamount.text.toString().isEmpty()) {
                paymentamount.error = "Payment amount is required!"
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
                    Toast.makeText(this, "Vendor was not found", Toast.LENGTH_SHORT).show()
                    inputvalflag = false
                } else {
                    paymentitem.vendorid = vendorid
                }
            }
            if (inputvalflag) {
                savePayment()
            }
        }
    }

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
            DatePickerFragment.newInstance((object : DatePickerDialog.OnDateSetListener {
                @RequiresApi(Build.VERSION_CODES.O)
                override fun onDateSet(p0: DatePicker?, p1: Int, p2: Int, p3: Int) {
                    if (validateOldDate(p1, p2 + 1, p3)) {
                        val selectedDate = p3.toString() + "/" + (p2 + 1) + "/" + p1
                        paymentdate.setText(selectedDate)
                    } else {
                        paymentdate.error = "Payment date is invalid!"
                    }
                }
            }))
        newFragment.show(supportFragmentManager, "datePicker")
    }

    private fun savePayment() {
        paymentitem.name = paymentname.text.toString()
        paymentitem.date = paymentdate.text.toString()
        paymentitem.amount = paymentamount.text.toString()
        paymentitem.category = getCategory()

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
                requestPermissions(permissions, TaskCreateEdit.PERMISSION_CODE)
            } else {
                if (paymentitem.key == "") {
                    addPayment(this, paymentitem)
                } else if (paymentitem.key != "") {
                    editPayment(this, paymentitem)
                }
//                val resultIntent = Intent()
//                setResult(Activity.RESULT_OK, resultIntent)
            }
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
        TODO("Not yet implemented")
    }

    companion object {
        const val TAG = "PaymentCreateEdit"
    }
}

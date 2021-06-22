package com.example.newevent2

import Application.Cache
import android.app.Activity
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.DatePicker
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.newevent2.Functions.validateOldDate
import com.example.newevent2.Model.Payment
import com.example.newevent2.Model.PaymentModel
import com.example.newevent2.Model.Task
import com.example.newevent2.Model.TaskModel
import com.example.newevent2.ui.dialog.DatePickerFragment
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import kotlinx.android.synthetic.main.eventform_layout.*
import kotlinx.android.synthetic.main.payment_editdetail.*
import kotlinx.android.synthetic.main.task_editdetail.*
import java.util.*

class PaymentCreateEdit() : AppCompatActivity() {

    private var userid = ""
    private var eventid = ""


    private lateinit var paymentitem: Payment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.payment_editdetail)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.setHomeAsUpIndicator(R.drawable.icons8_left_24)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        val extras = intent.extras
        paymentitem = if (extras!!.containsKey("payment")) {
            intent.getParcelableExtra("payment")!!
        } else {
            Payment()
        }

        userid = intent.getStringExtra("userid").toString()
        eventid = intent.getStringExtra("eventid").toString()
        val paymentid = paymentitem.key

        if (paymentid != "") {
            val paymentmodel = PaymentModel()
            paymentmodel.getPaymentdetail(userid, eventid, paymentid, object :
                PaymentModel.FirebaseSuccessPayment {
                @RequiresApi(Build.VERSION_CODES.O)
                override fun onPayment(payment: Payment) {
                    paymentname.setText(payment.name)
                    paymentdate.setText(payment.date)
                    paymentamount.setText(payment.amount)

                    paymentitem.key = payment.key
                    paymentitem.amount = payment.amount
                    paymentitem.category = payment.category
                    paymentitem.date = payment.date
                    paymentitem.name = payment.name
                    paymentitem.createdatetime = payment.createdatetime
                }
            })
        }

        paymentname.setOnClickListener {
            paymentname.error = null
        }

        paymentdate.setOnClickListener {
            paymentdate.error = null
            showDatePickerDialog()
        }

        paymentamount.setOnClickListener {
            paymentamount.error = null
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

        savebuttonpayment.setOnClickListener {
            var inputvalflag = true
            if (paymentname.text.toString().isEmpty()) {
                paymentname.error = "Payment name is required!"
                inputvalflag = false
            }
            if (paymentdate.text.toString().isEmpty()) {
                paymentdate.error = "Payment date is required!"
                inputvalflag = false
            }
            if (paymentamount.text.toString().isEmpty()) {
                paymentamount.error = "Payment amount is required!"
                inputvalflag = false
            }
            if (groupeditpayment.checkedChipId == -1) {
                Toast.makeText(this, "Category is required!", Toast.LENGTH_SHORT).show()
                inputvalflag = false
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
                        eventdate.setText(selectedDate)
                    } else {
                        eventdate.error = "Task date is invalid!"
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

        val paymentmodel = PaymentModel()
        if (paymentitem.key != "") {
            paymentmodel.editPayment(
                userid,
                eventid,
                paymentitem,
                object : PaymentModel.FirebaseAddEditPaymentSuccess {
                    override fun onPaymentAddedEdited(flag: Boolean) {
                        if (flag) {
                            //Deleting all instances of Payment from cache
                            Log.i(TAG, "Payment ${paymentitem.key} successfully edited")
                            Cache.deletefromStorage(PAYENTITY, applicationContext)
                        }
                    }
                })
        } else {
            val usersession =
                application.getSharedPreferences("USER_SESSION", Context.MODE_PRIVATE)
            val payments = usersession.getInt("payments", 0)
            val sessionEditor = usersession!!.edit()

            paymentmodel.addPayment(
                userid,
                eventid,
                paymentitem,
                payments,
                object : PaymentModel.FirebaseAddEditPaymentSuccess {
                    override fun onPaymentAddedEdited(flag: Boolean) {
                        if (flag) {
                            Log.i(TAG, "Payment successfully added")
                            sessionEditor.putInt("payments", payments + 1)
                            sessionEditor.apply()
                            //Deleting all instances of Payment from cache
                            Cache.deletefromStorage(PAYENTITY, applicationContext)
                        }
                    }
                })
        }
        val resultIntent = Intent()
        setResult(Activity.RESULT_OK, resultIntent)
        finish()
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    companion object {
        const val PAYENTITY = "Payment"
        const val TAG = "PaymentCreateEdit"
    }
}

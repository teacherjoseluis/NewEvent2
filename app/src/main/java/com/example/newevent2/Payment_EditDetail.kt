package com.example.newevent2

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.newevent2.Model.PaymentModel
import com.example.newevent2.Model.Task
import com.example.newevent2.Model.TaskModel
import com.example.newevent2.ui.dialog.DatePickerFragment
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.payment_editdetail.*
import kotlinx.android.synthetic.main.task_editdetail.*
import kotlinx.android.synthetic.main.task_editdetail.button2
import kotlinx.android.synthetic.main.task_editdetail.groupedit
import java.util.*
import kotlin.collections.ArrayList

class Payment_EditDetail : AppCompatActivity() {

    var payment = com.example.newevent2.Model.Payment()
    var userid = ""
    var eventid = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.payment_editdetail)

        payment = intent.getParcelableExtra("payment")!!
        userid = intent.getStringExtra("userid").toString()
        eventid = intent.getStringExtra("eventid").toString()

        val chipgroupedit = findViewById<ChipGroup>(R.id.groupedit)
        chipgroupedit.isSingleSelection = true

        // Create chips and select the one matching the category
        val list = ArrayList<Category>(EnumSet.allOf(Category::class.java))
        for (category in list) {
            val chip = Chip(this)
            chip.text = category.en_name
            chipgroupedit.addView(chip)
            if (payment.category == category.code) {
                chip.isSelected = true
                chipgroupedit.check(chip.id)
            }
        }
            val paymentname = findViewById<TextView>(R.id.pyname)
            paymentname.text = payment.name

            val paymentdate = findViewById<TextView>(R.id.pydate)
            paymentdate.text = payment.date

            val paymentamount = findViewById<TextView>(R.id.pyamount)
            paymentamount.text = payment.amount

        paymentname.setOnClickListener {
            paymentname.error = null
        }

        paymentdate.setOnClickListener {
            paymentdate.error = null
            paymentdate.text = com.example.newevent2.ui.Functions.showDatePickerDialog(
                supportFragmentManager
            )
        }

        paymentamount.setOnClickListener {
            paymentamount.error = null
        }

        //---------------------------------------------------------------------------------//
//        setSupportActionBar(findViewById(R.id.toolbar))
//        supportActionBar!!.setHomeAsUpIndicator(R.drawable.icons8_left_24)
//        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        val apptitle = findViewById<TextView>(R.id.appbartitle)
        apptitle.text = "Payment Detail"
        //-------------------------------------------------------------------------------//

        groupedit.isSingleSelection = true

        button2.setOnClickListener()
        {
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
            if (groupedit.checkedChipId == -1) {
                Toast.makeText(this, "Category is required!", Toast.LENGTH_SHORT).show()
                inputvalflag = false
            }
            if (inputvalflag) {
                savePayment()
                onBackPressed()
            }
        }

    }


    private fun savePayment() {
        payment.name = tkname.text.toString()
        payment.date = tkdate.text.toString()
        payment.amount = tkbudget.text.toString()

        val chipselected = groupedit.findViewById<Chip>(groupedit.checkedChipId)
        val chiptextvalue = chipselected.text.toString()

        val list = ArrayList<Category>(EnumSet.allOf(Category::class.java))
        for (category in list) {
            if(chiptextvalue == category.en_name){
                payment.category = category.code
            }
        }

        val paymentmodel = PaymentModel()
        paymentmodel.editPayment(userid, eventid, payment)
    }
}
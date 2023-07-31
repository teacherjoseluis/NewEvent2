package com.bridesandgrooms.event

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import kotlinx.android.synthetic.main.payment_editdetail.*
import java.util.*
import kotlin.collections.ArrayList

class Payment_EditDetail : AppCompatActivity() {

    var payment = com.bridesandgrooms.event.Model.Payment()
    var userid = ""
    var eventid = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.payment_editdetail)

        payment = intent.getParcelableExtra("payment")!!
        userid = intent.getStringExtra("userid").toString()
        eventid = intent.getStringExtra("eventid").toString()

        val chipgroupedit = findViewById<ChipGroup>(R.id.groupeditpayment)
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
            val paymentname = findViewById<TextView>(R.id.paymentname)
            paymentname.text = payment.name

            val paymentdate = findViewById<TextView>(R.id.paymentdate)
            paymentdate.text = payment.date

            val paymentamount = findViewById<TextView>(R.id.paymentamount)
            paymentamount.text = payment.amount

        paymentname.setOnClickListener {
            paymentname.error = null
        }

        paymentdate.setOnClickListener {
            paymentdate.error = null
            paymentdate.text = com.bridesandgrooms.event.ui.Functions.showDatePickerDialog(
                supportFragmentManager
            )
        }

        paymentamount.setOnClickListener {
            paymentamount.error = null
        }

        //---------------------------------------------------------------------------------//
        val apptitle = findViewById<TextView>(R.id.appbartitle)

        val extras = intent.extras
        if (extras!!.containsKey("payment")) {
            apptitle.text = getString(R.string.edit_payment)
        } else {
            apptitle.text = getString(R.string.new_payment)
        }
        //-------------------------------------------------------------------------------//

        groupeditpayment.isSingleSelection = true

        savebuttonpayment.setOnClickListener()
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
//            if (groupedit.checkedChipId == -1) {
//                Toast.makeText(this, "Category is required!", Toast.LENGTH_SHORT).show()
//                inputvalflag = false
//            }
            if (inputvalflag) {
                savePayment()
                onBackPressed()
            }
        }

    }

    private fun savePayment() {
        payment.name = paymentname.text.toString()
        payment.date = paymentdate.text.toString()
        payment.amount = paymentamount.text.toString()

//        val chipselected = groupedit.findViewById<Chip>(groupedit.checkedChipId)
//        val chiptextvalue = chipselected.text.toString()
//
//        val list = ArrayList<Category>(EnumSet.allOf(Category::class.java))
//        for (category in list) {
//            if(chiptextvalue == category.en_name){
//                payment.category = category.code
//            }
//        }
//        val paymentmodel = PaymentModel()
//        paymentmodel.editPayment(userid, eventid, payment, object: PaymentModel.FirebaseAddEditPaymentSuccess {
//            override fun onPaymentAddedEdited(flag: Boolean) {
//                TODO("Not yet implemented")
//            }
//        })
    }
}
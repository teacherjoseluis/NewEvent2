package com.bridesandgrooms.event

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.bridesandgrooms.event.Model.Category
import com.bridesandgrooms.event.databinding.PaymentEditdetailBinding
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
//import kotlinx.android.synthetic.main.payment_editdetail.*
import java.util.*
import kotlin.collections.ArrayList

class Payment_EditDetail : AppCompatActivity() {

    var payment = com.bridesandgrooms.event.Model.Payment()

    private lateinit var binding: PaymentEditdetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.payment_editdetail)

        payment = intent.getParcelableExtra("payment")!!
        //userid = intent.getStringExtra("userid").toString()
        //eventid = intent.getStringExtra("eventid").toString()

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
            binding.paymentname.setText(payment.name)
            binding.paymentdate.setText(payment.date)
            binding.paymentamount.setText(payment.amount)

        binding.paymentname.setOnClickListener {
            binding.paymentname.error = null
        }

        binding.paymentdate.setOnClickListener {
            binding.paymentdate.error = null
            binding.paymentdate.setText(com.bridesandgrooms.event.UI.Functions.showDatePickerDialog(
                supportFragmentManager
            ))
        }

        binding.paymentamount.setOnClickListener {
            binding.paymentamount.error = null
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

        binding.groupeditpayment.isSingleSelection = true

        binding.savebuttonpayment.setOnClickListener()
        {
            var inputvalflag = true
            if (binding.paymentname.text.toString().isEmpty()) {
                binding.paymentname.error = "Payment name is required!"
                inputvalflag = false
            }
            if (binding.paymentdate.text.toString().isEmpty()) {
                binding.paymentdate.error = "Payment date is required!"
                inputvalflag = false
            }
            if (binding.paymentamount.text.toString().isEmpty()) {
                binding.paymentamount.error = "Payment amount is required!"
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
        payment.name = binding.paymentname.text.toString()
        payment.date = binding.paymentdate.text.toString()
        payment.amount = binding.paymentamount.text.toString()

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
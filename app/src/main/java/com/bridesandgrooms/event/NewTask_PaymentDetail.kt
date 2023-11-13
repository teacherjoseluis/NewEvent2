package com.bridesandgrooms.event

import android.content.Context
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.bridesandgrooms.event.Model.Payment
import com.bridesandgrooms.event.databinding.PaymentEditdetailBinding

//import kotlinx.android.synthetic.main.payment_editdetail.*


class NewTask_PaymentDetail : AppCompatActivity(){
    var userid = ""
    var eventid = ""

    private lateinit var binding: PaymentEditdetailBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.payment_editdetail)

        userid = intent.getStringExtra("userid").toString()
        eventid = intent.getStringExtra("eventid").toString()

        // Toolbar
        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar!!.setHomeAsUpIndicator(R.drawable.icons8_left_24)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        val apptitle = findViewById<TextView>(R.id.appbartitle)
        apptitle.text = getString(R.string.new_payment)

        //groupedit.isSingleSelection = true

        binding.paymentname.setOnClickListener {
            binding.paymentname.error = null
        }

        binding.paymentdate.setOnClickListener {
            binding.paymentdate.error = null
            binding.paymentdate.setText(
                com.bridesandgrooms.event.UI.Functions.showDatePickerDialog(
                    supportFragmentManager
                ).replace(" ","")
            )
        }

        binding.paymentamount.setOnClickListener {
            binding.paymentamount.error = null
        }

        binding.savebuttonpayment.setOnClickListener {
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
                //onBackPressed()
            }
        }
    }

    private fun savePayment() {
        val payment = Payment()
        payment.name = binding.paymentname.text.toString()
        payment.date = binding.paymentdate.text.toString()
        payment.amount = binding.paymentamount.text.toString()

        val usersession =
            application.getSharedPreferences("USER_SESSION", Context.MODE_PRIVATE)
        val payments = usersession.getInt("payments", 0)
        val sessionEditor = usersession!!.edit()
        sessionEditor.putInt("payments", payments + 1)
        sessionEditor.apply()


//        val chipselected = groupedit.findViewById<Chip>(groupedit.checkedChipId)
//        val chiptextvalue = chipselected.text.toString()
//        val list = ArrayList<Category>(EnumSet.allOf(Category::class.java))
//        for (category in list) {
//            if (chiptextvalue == category.en_name) {
//                payment.category = category.code
//            }
//        }
//        val paymentmodel = PaymentModel()
//        paymentmodel.addPayment(userid, eventid, payment, payments, object: PaymentModel.FirebaseAddEditPaymentSuccess {
//            override fun onPaymentAddedEdited(flag: Boolean) {
//                TODO("Not yet implemented")
//            }
//
//        })
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }




}
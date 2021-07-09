package com.example.newevent2

import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.newevent2.Model.Payment
import com.example.newevent2.Model.PaymentModel
import com.example.newevent2.Model.TaskModel
import com.example.newevent2.ui.dialog.DatePickerFragment
import com.google.android.material.chip.Chip
import kotlinx.android.synthetic.main.payment_editdetail.*
import kotlinx.android.synthetic.main.task_editdetail.*
import java.util.*
import kotlin.collections.ArrayList


class NewTask_PaymentDetail : AppCompatActivity() {
    var userid = ""
    var eventid = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.payment_editdetail)
        userid = intent.getStringExtra("userid").toString()
        eventid = intent.getStringExtra("eventid").toString()

        // Toolbar
        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar!!.setHomeAsUpIndicator(R.drawable.icons8_left_24)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        val apptitle = findViewById<TextView>(R.id.appbartitle)
        apptitle.text = "New Payment"

        //groupedit.isSingleSelection = true

        paymentname.setOnClickListener {
            paymentname.error = null
        }

        paymentdate.setOnClickListener {
            paymentdate.error = null
            paymentdate.setText(
                com.example.newevent2.ui.Functions.showDatePickerDialog(
                    supportFragmentManager
                ).replace(" ","")
            )
        }

        paymentamount.setOnClickListener {
            paymentamount.error = null
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
        payment.name = paymentname.text.toString()
        payment.date = paymentdate.text.toString()
        payment.amount = paymentamount.text.toString()

        val usersession =
            application.getSharedPreferences("USER_SESSION", Context.MODE_PRIVATE)
        val payments = usersession.getInt("payments", 0)
        val sessionEditor = usersession!!.edit()
        sessionEditor.putInt("payments", payments + 1)
        sessionEditor.apply()


//        val chipselected = groupedit.findViewById<Chip>(groupedit.checkedChipId)
//        val chiptextvalue = chipselected.text.toString()
//
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
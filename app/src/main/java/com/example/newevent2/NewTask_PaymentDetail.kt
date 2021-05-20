package com.example.newevent2

import android.app.DatePickerDialog
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
import kotlinx.android.synthetic.main.payment_editdetail.button2
import kotlinx.android.synthetic.main.payment_editdetail.groupedit
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

        groupedit.isSingleSelection = true

        pyname.setOnClickListener {
            pyname.error = null
        }

        pydate.setOnClickListener {
            pydate.error = null
            pydate.setText(
                com.example.newevent2.ui.Functions.showDatePickerDialog(
                    supportFragmentManager
                ).replace(" ","")
            )
        }

        pyamount.setOnClickListener {
            pyamount.error = null
        }

        button2.setOnClickListener {
            var inputvalflag = true
            if (pyname.text.toString().isEmpty()) {
                pyname.error = "Payment name is required!"
                inputvalflag = false
            }
            if (pydate.text.toString().isEmpty()) {
                pydate.error = "Payment date is required!"
                inputvalflag = false
            }
            if (pyamount.text.toString().isEmpty()) {
                pyamount.error = "Payment amount is required!"
                inputvalflag = false
            }
            if (groupedit.checkedChipId == -1) {
                Toast.makeText(this, "Category is required!", Toast.LENGTH_SHORT).show()
                inputvalflag = false
            }
            if (inputvalflag) {
                savePayment()
                //onBackPressed()
            }
        }
    }


    private fun savePayment() {
        val payment = Payment()
        payment.name = pyname.text.toString()
        payment.date = pydate.text.toString()
        payment.amount = pyamount.text.toString()


        val chipselected = groupedit.findViewById<Chip>(groupedit.checkedChipId)
        val chiptextvalue = chipselected.text.toString()

        val list = ArrayList<Category>(EnumSet.allOf(Category::class.java))
        for (category in list) {
            if (chiptextvalue == category.en_name) {
                payment.category = category.code
            }
        }
        val paymentmodel = PaymentModel()
        paymentmodel.addPayment(userid, eventid, payment)
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}
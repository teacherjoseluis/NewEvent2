package com.example.newevent2

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.newevent2.ui.dialog.DatePickerFragment
import com.google.android.material.chip.Chip
import kotlinx.android.synthetic.main.payment_editdetail.*
import kotlinx.android.synthetic.main.payment_editdetail.button2


class NewTask_PaymentDetail : AppCompatActivity() {
    private var eventkey: String = ""
    private var paymentcategory: String = ""

    private var chiptextvalue: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.payment_editdetail)

        eventkey = intent.getStringExtra("eventkey").toString()

        groupedit.isSingleSelection = true

        pyname.setOnClickListener {
            pyname.error = null
        }

        pydate.setOnClickListener {
            pydate.error = null
            showDatePickerDialog()
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

    private fun showDatePickerDialog() {
        val newFragment =
            DatePickerFragment.newInstance(DatePickerDialog.OnDateSetListener { _, year, month, day ->
                // +1 because January is zero
                val selectedDate = day.toString() + "/" + (month + 1) + "/" + year
                pydate.setText(selectedDate)
            })

        newFragment.show(supportFragmentManager, "datePicker")
    }

    private fun savePayment() {
        val id = groupedit.checkedChipId
        val chipselected = groupedit.findViewById<Chip>(id)
        chiptextvalue = chipselected.text.toString()
        paymentcategory = when (chiptextvalue) {
            "Flowers & Deco" -> "flowers"
            "Venue" -> "venue"
            "Photo & Video" -> "photo"
            "Entertainment" -> "entertainment"
            "Transportation" -> "transport"
            "Ceremony" -> "ceremony"
            "Attire & Accessories" -> "accesories"
            "Health & Beauty" -> "beauty"
            "Food & Drink" -> "food"
            "Guests" -> "guests"
            else -> "none"
        }

        val paymententity = PaymentEntity().apply {
            name = pyname.text.toString()
            amount = pyamount.text.toString()
            date = pydate.text.toString()
            category = paymentcategory
            eventid = eventkey
        }
        paymententity.addPayment()
    }
}
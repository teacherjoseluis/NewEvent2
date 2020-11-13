package com.example.newevent2

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.newevent2.ui.dialog.DatePickerFragment
import com.google.android.material.chip.Chip
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.payment_editdetail.*
import kotlinx.android.synthetic.main.task_editdetail.*
import kotlinx.android.synthetic.main.task_editdetail.button2
import kotlinx.android.synthetic.main.task_editdetail.groupedit

class Payment_EditDetail: AppCompatActivity() {

    lateinit var paymentitem: Payment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.payment_editdetail)
        paymentitem = Payment()

        val intent = intent
        val paymentkey = intent.getStringExtra("paymentkey").toString()
        val eventid = intent.getStringExtra("eventid").toString()
        val paymentnameextra = intent.getStringExtra("name").toString()
        val paymentdateextra = intent.getStringExtra("date").toString()
        val paymentcategory = intent.getStringExtra("category").toString()
        val paymentamountextra = intent.getStringExtra("amount").toString()

        if (paymentkey != "" && eventid != "") {

            val paymentname = findViewById<TextView>(R.id.pyname)
            paymentname.text = paymentnameextra

            val paymentdate = findViewById<TextView>(R.id.pydate)
            paymentdate.text = paymentdateextra

            var selectedchip: TextView
            selectedchip = findViewById<TextView>(R.id.chip)

            if (paymentcategory == "flowers") {
                selectedchip = findViewById<TextView>(R.id.chip)
            }

            if (paymentcategory == "venue") {
                selectedchip = findViewById<TextView>(R.id.chip2)
            }

            if (paymentcategory == "photo") {
                selectedchip = findViewById<TextView>(R.id.chip3)
            }

            if (paymentcategory == "entertainment") {
                selectedchip = findViewById<TextView>(R.id.chip4)
            }

            if (paymentcategory == "transport") {
                selectedchip = findViewById<TextView>(R.id.chip5)
            }

            if (paymentcategory == "ceremony") {
                selectedchip = findViewById<TextView>(R.id.chip6)
            }

            if (paymentcategory == "accesories") {
                selectedchip = findViewById<TextView>(R.id.chip7)
            }

            if (paymentcategory == "beauty") {
                selectedchip = findViewById<TextView>(R.id.chip8)
            }

            if (paymentcategory == "food") {
                selectedchip = findViewById<TextView>(R.id.chip9)
            }

            if (paymentcategory == "guests") {
                selectedchip = findViewById<TextView>(R.id.chip10)
            }

            if (paymentcategory == "none") {
            }

            groupedit.check(selectedchip.id)
            selectedchip.isSelected = true

            val paymentamount = findViewById<TextView>(R.id.pyamount)
            paymentamount.text = paymentamountextra

        }

        //---------------------------------------------------------------------------------//
        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar!!.setHomeAsUpIndicator(R.drawable.icons8_left_24)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        val apptitle = findViewById<TextView>(R.id.appbartitle)
        apptitle.text = "Payment Detail"
        //-------------------------------------------------------------------------------//

        groupedit.isSingleSelection = true

        pydate.setOnClickListener()
        {
            showDatePickerDialog()
        }

        button2.setOnClickListener()
        {
            savePayment(paymentkey, eventid)
        }

    }

    private fun showDatePickerDialog() {
        val newFragment =
            DatePickerFragment.newInstance(DatePickerDialog.OnDateSetListener { _, year, month, day ->
                // +1 because January is zero
                val selectedDate = day.toString() + " / " + (month + 1) + " / " + year
                tkdate.setText(selectedDate)
            })

        newFragment.show(supportFragmentManager, "datePicker")
    }

    private fun savePayment(paymentkey: String, eventid: String) {
        val database = FirebaseDatabase.getInstance()
        val myRef = database.reference
        val postRef = myRef.child("User").child("Event").child(eventid).child("Payment").child(paymentkey)
        var category = ""

        if (groupedit.checkedChipId != null) {
            val id = groupedit.checkedChipId
            val chipselected = groupedit.findViewById<Chip>(id)
            val chiptextvalue = chipselected.text.toString()
            category = when (chiptextvalue) {
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
        }

        postRef.child("name").setValue(pyname.text.toString())
        postRef.child("amount").setValue(pyamount.text.toString())
        postRef.child("date").setValue(pydate.text.toString())
        postRef.child("category").setValue(category)
        this.onBackPressed()
    }

}
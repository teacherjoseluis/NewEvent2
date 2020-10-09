package com.example.newevent2

import android.app.DatePickerDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.newevent2.ui.dialog.DatePickerFragment
import com.google.android.material.chip.Chip
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.new_task_paymentdetail.*
import kotlinx.android.synthetic.main.new_task_paymentdetail.view.*
import kotlinx.android.synthetic.main.new_task_paymentdetail.view.pydate


class NewTask_PaymentDetail : Fragment() {

    lateinit var eventkey: String
    private var chiptextvalue: String? = null
    private var category: String = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        eventkey = this.arguments!!.get("eventkey").toString()

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        val inf = inflater.inflate(R.layout.new_task_paymentdetail, container, false)
        inf.group2.isSingleSelection = true

        inf.pydate.setOnClickListener {
            showDatePickerDialog()
        }

        inf.button3.setOnClickListener {
            savePayment(inf)
        }

        return inf
    }

    private fun showDatePickerDialog() {
        val newFragment =
            DatePickerFragment.newInstance(DatePickerDialog.OnDateSetListener { _, year, month, day ->
                // +1 because January is zero
                val selectedDate = day.toString() + " / " + (month + 1) + " / " + year
                pydate.setText(selectedDate)
            })

        newFragment.show(childFragmentManager, "datePicker")
    }

    private fun savePayment(inf: View) {
        val database = FirebaseDatabase.getInstance()
        val myRef = database.reference
        val postRef = myRef.child("User").child("Event").child(eventkey).child("Payment").push()

        if (inf.group2.checkedChipId != null) {
            val id = inf.group2.checkedChipId
            val chipselected = inf.group2.findViewById<Chip>(id)
            chiptextvalue = chipselected.text.toString()
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

        val tasks = hashMapOf(
            "name" to inf.paymentname.text.toString(),
            "amount" to pyamount.text.toString(),
            "date" to pydate.text.toString(),
            "category" to category,
            "eventid" to eventkey
        )

        postRef.setValue(tasks as Map<String, Any>)
            .addOnFailureListener {
                Snackbar.make(
                    activity!!.findViewById(android.R.id.content),
                    "Error while saving the Payment",
                    Snackbar.LENGTH_LONG
                ).show()
            }
            .addOnSuccessListener {
                Snackbar.make(
                    activity!!.findViewById(android.R.id.content),
                    "Payment Saved Successfully",
                    Snackbar.LENGTH_LONG
                ).show()
                activity!!.onBackPressed()
            }
    }

}
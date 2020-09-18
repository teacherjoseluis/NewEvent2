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
import kotlinx.android.synthetic.main.new_task_taskdetail.*
import kotlinx.android.synthetic.main.new_task_taskdetail.view.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [NewTask_PaymentDetail.newInstance] factory method to
 * create an instance of this fragment.
 */
class NewTask_PaymentDetail : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    lateinit var eventkey: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        eventkey = this.arguments!!.get("eventkey").toString()

        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        val inf = inflater.inflate(R.layout.new_task_paymentdetail, container, false)

        inf.pydate.setOnClickListener {
            showDatePickerDialog()
        }

        inf.button3.setOnClickListener {
            saveTask(inf)
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

    private fun saveTask(inf: View) {
        val database = FirebaseDatabase.getInstance()
        val myRef = database.reference
        val postRef = myRef.child("User").child("Event").child(eventkey).child("Payment").push()

        val tasks = hashMapOf(
            "amount" to pyamount.text.toString(),
            "date" to pydate.text.toString(),
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

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment NewTask_PaymentDetail.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            NewTask_PaymentDetail().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
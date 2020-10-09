package com.example.newevent2

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.newevent2.ui.dialog.DatePickerFragment
import com.google.android.material.chip.Chip
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.new_task_taskdetail.*
import kotlinx.android.synthetic.main.new_task_taskdetail.view.*


class NewTask_TaskDetail : Fragment() {
    lateinit var eventkey: String
    lateinit var storage: FirebaseStorage
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

        val inf = inflater.inflate(R.layout.new_task_taskdetail, container, false)
        inf.group.isSingleSelection = true

        inf.tkdate.setOnClickListener {
            showDatePickerDialog()
        }

        inf.button2.setOnClickListener {
            saveTask(inf)
        }
        return inf
    }

    private fun showDatePickerDialog() {
        val newFragment =
            DatePickerFragment.newInstance(DatePickerDialog.OnDateSetListener { _, year, month, day ->
                // +1 because January is zero
                val selectedDate = day.toString() + " / " + (month + 1) + " / " + year
                tkdate.setText(selectedDate)
            })

        newFragment.show(childFragmentManager, "datePicker")
    }

    private fun saveTask(inf: View) {
        val database = FirebaseDatabase.getInstance()
        val myRef = database.reference
        val postRef = myRef.child("User").child("Event").child(eventkey).child("Task").push()

        if (inf.group.checkedChipId != null) {
            val id = inf.group.checkedChipId
            val chipselected = inf.group.findViewById<Chip>(id)
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
            "name" to tkname.text.toString(),
            "budget" to tkbudget.text.toString(),
            "date" to tkdate.text.toString(),
            "category" to category,
            "status" to "A",
            "eventid" to eventkey
        )

        postRef.setValue(tasks as Map<String, Any>)
            .addOnFailureListener {
                Snackbar.make(
                    activity!!.findViewById(android.R.id.content),
                    "Error while saving the Task",
                    Snackbar.LENGTH_LONG
                ).show()
            }
            .addOnSuccessListener {
                Snackbar.make(
                    activity!!.findViewById(android.R.id.content),
                    "Task Saved Successfully",
                    Snackbar.LENGTH_LONG
                ).show()
                activity!!.onBackPressed()
            }
    }
}
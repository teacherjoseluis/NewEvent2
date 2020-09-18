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


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [NewTask_TaskDetail.newInstance] factory method to
 * create an instance of this fragment.
 */
class NewTask_TaskDetail : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    lateinit var eventkey: String
    lateinit var storage: FirebaseStorage
    private var chiptextvalue: String? = null

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
        }

        //val myRef = database.getReference("User/Event").push()
        var key = ""

        val tasks = hashMapOf(
            "name" to tkname.text.toString(),
            "budget" to tkbudget.text.toString(),
            "date" to tkdate.text.toString(),
            "category" to chiptextvalue,
            //"vendor" to
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

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment NewTask_TaskDetail.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            NewTask_TaskDetail().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
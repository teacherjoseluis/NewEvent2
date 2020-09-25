package com.example.newevent2

import android.graphics.Canvas
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SimpleAdapter
import android.widget.Toast
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.tasklist_tasks.view.recyclerView


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [EventDetail_Summary.newInstance] factory method to
 * create an instance of this fragment.
 */
class TaskList_Tasks : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    lateinit var eventkey: String
    lateinit var category: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        eventkey = this.arguments!!.get("eventkey").toString()
        category = this.arguments!!.get("category").toString()

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
        val inf = inflater.inflate(R.layout.tasklist_tasks, container, false)
        //inf.textView9.text = category

        val recyclerView = inf.recyclerView

        recyclerView.apply {
            layoutManager = LinearLayoutManager(inf.context).apply {
                stackFromEnd = true
                reverseLayout = true
            }
        }

        val database = FirebaseDatabase.getInstance()
        val myRef = database.reference
        //FirebaseUser user = mAuth.getCurrentUser();
        val postRef = myRef.child("User").child("Event").child(eventkey).child("Task")
        var tasklist = ArrayList<Task>()

        val taskListener = object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()) {

                    for (snapshot in p0.children) {
                        val taskitem = snapshot.getValue(Task::class.java)
                        if (taskitem!!.category == category) {
                            taskitem!!.key = snapshot.key.toString()
                            tasklist.add(taskitem!!)
                        }
                    }
                }

                //        pass the values to RvAdapter
                val rvAdapter = Rv_TaskAdapter(tasklist)
//        set the recyclerView to the adapter
                recyclerView.adapter = rvAdapter

                val swipeController = SwipeController(inf.context)
                val itemTouchHelper = ItemTouchHelper(swipeController)
                itemTouchHelper.attachToRecyclerView(recyclerView)

                /*
                val obs = recyclerView.viewTreeObserver
                obs.addOnGlobalLayoutListener {
                    recyclerView.requestLayout()
                    recyclerView.invalidate()
                }
                */
                //recyclerView.scrollToPosition(recyclerView.adapter!!.itemCount - 1)

            }

            override fun onCancelled(databaseError: DatabaseError) {
                println("loadPost:onCancelled ${databaseError.toException()}")
            }

        }
        postRef.addValueEventListener(taskListener)

        return inf
    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment EventDetail_Summary.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            EventDetail_Summary().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
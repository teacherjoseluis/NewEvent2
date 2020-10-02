package com.example.newevent2

import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.tasklist_payments.view.*
import kotlinx.android.synthetic.main.tasklist_tasks.view.*
import kotlinx.android.synthetic.main.tasklist_tasks.view.recyclerView
import java.text.DecimalFormat


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

    var countactive: Int = 0
    var countcompleted: Int = 0

    var sumbudget: Float = 0.0f

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
        val recyclerView2 = inf.recyclerView2

        recyclerView.apply {
            layoutManager = LinearLayoutManager(inf.context).apply {
                stackFromEnd = true
                reverseLayout = true
            }
        }
        recyclerView2.apply {
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
            @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
            override fun onDataChange(p0: DataSnapshot) {
                tasklist.clear()
                countactive = 0
                sumbudget = 0.0f
                if (p0.exists()) {
                    for (snapshot in p0.children) {
                        val taskitem = snapshot.getValue(Task::class.java)

                        if (taskitem!!.category == category && taskitem!!.status == "A") {
                            taskitem!!.key = snapshot.key.toString()
                            tasklist.add(taskitem!!)

                            val re = Regex("[^A-Za-z0-9 ]")
                            val budgetamount = re.replace(taskitem.budget, "").dropLast(2)
                            sumbudget += budgetamount.toFloat()
                            countactive += 1
                        }
                    }
                }

                //        pass the values to RvAdapter
                val rvAdapter = Rv_TaskAdapter(tasklist)
//        set the recyclerView to the adapter
                recyclerView.adapter = rvAdapter

                val swipeController = SwipeController(inf.context, rvAdapter, recyclerView)
                val itemTouchHelper = ItemTouchHelper(swipeController)
                itemTouchHelper.attachToRecyclerView(recyclerView)

//                if (rvAdapter.itemCount > 0) {
//                    recyclerView.addItemDecoration(
//                        DividerItemDecoration(
//                            context,
//                            LinearLayoutManager.VERTICAL
//                        )
//                    )
//                }

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

        //---------------------------------------------------------------------------------------

        var tasklist2 = ArrayList<Task>()

        val taskListener2 = object : ValueEventListener {
            @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
            override fun onDataChange(p0: DataSnapshot) {
                tasklist2.clear()
                countcompleted = 0
                if (p0.exists()) {
                    for (snapshot in p0.children) {
                        val taskitem = snapshot.getValue(Task::class.java)

                        if (taskitem!!.category == category && taskitem!!.status == "C") {
                            taskitem!!.key = snapshot.key.toString()
                            tasklist2.add(taskitem!!)
                            countcompleted += 1

                            val re = Regex("[^A-Za-z0-9 ]")
                            val budgetamount = re.replace(taskitem.budget, "").dropLast(2)
                            sumbudget += budgetamount.toFloat()
                        }
                    }
                }

                //        pass the values to RvAdapter
                val rvAdapter = Rv_TaskAdapter(tasklist2)
//        set the recyclerView to the adapter
                recyclerView2.adapter = rvAdapter

                val swipeController = SwipeController2(inf.context, rvAdapter, recyclerView2)
                val itemTouchHelper = ItemTouchHelper(swipeController)
                itemTouchHelper.attachToRecyclerView(recyclerView2)

//                if (rvAdapter.itemCount > 0) {
//                    recyclerView.addItemDecoration(
//                        DividerItemDecoration(
//                            context,
//                            LinearLayoutManager.VERTICAL
//                        )
//                    )
//                }

                /*
                val obs = recyclerView.viewTreeObserver
                obs.addOnGlobalLayoutListener {
                    recyclerView.requestLayout()
                    recyclerView.invalidate()
                }
                */
                //recyclerView.scrollToPosition(recyclerView.adapter!!.itemCount - 1)

                inf.tasktextView16.text = countactive.toString()
                inf.tasktextView17.text = countcompleted.toString()

                val formatter = DecimalFormat("$#,###.00")
                inf.tasktextView18.text = formatter.format(sumbudget)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                println("loadPost:onCancelled ${databaseError.toException()}")
            }
        }
        postRef.addValueEventListener(taskListener2)
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
package com.example.newevent2

import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.tasklist_tasks.view.*
import kotlinx.android.synthetic.main.tasklist_tasks.view.recyclerView
import java.text.DecimalFormat


class TaskList_Tasks : Fragment() {
    lateinit var eventkey: String
    lateinit var category: String

    var countactive: Int = 0
    var countcompleted: Int = 0

    var sumbudget: Float = 0.0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        eventkey = this.arguments!!.get("eventkey").toString()
        category = this.arguments!!.get("category").toString()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val inf = inflater.inflate(R.layout.tasklist_tasks, container, false)
        //inf.textView9.text = category

        val recyclerViewActive = inf.recyclerView
        val recyclerViewComplete = inf.recyclerView2

        recyclerViewActive.apply {
            layoutManager = LinearLayoutManager(inf.context).apply {
                stackFromEnd = true
                reverseLayout = true
            }
        }
        recyclerViewComplete.apply {
            layoutManager = LinearLayoutManager(inf.context).apply {
                stackFromEnd = true
                reverseLayout = true
            }
        }

        val database = FirebaseDatabase.getInstance()
        val myRef = database.reference
        //FirebaseUser user = mAuth.getCurrentUser();
        val postRef = myRef.child("User").child("Event").child(eventkey).child("Task")
        var tasklistActive = ArrayList<Task>()

        val taskListenerActive = object : ValueEventListener {
            @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
            override fun onDataChange(p0: DataSnapshot) {
                tasklistActive.clear()
                countactive = 0
                sumbudget = 0.0f

                if (p0.exists()) {
                    val re = Regex("[^A-Za-z0-9 ]")
                    for (snapshot in p0.children) {
                        val taskitem = snapshot.getValue(Task::class.java)

                        if (taskitem!!.category == category && taskitem!!.status == "A") {
                            taskitem!!.key = snapshot.key.toString()
                            tasklistActive.add(taskitem!!)

                            val budgetamount = re.replace(taskitem.budget, "").dropLast(2)
                            sumbudget += budgetamount.toFloat()
                            countactive += 1
                        }
                    }
                }

                //        pass the values to RvAdapter
                val rvAdapter = Rv_TaskAdapter(tasklistActive)
//        set the recyclerView to the adapter
                recyclerViewActive.adapter = rvAdapter

                val swipeController = SwipeControllerActiveTasks(inf.context, rvAdapter, recyclerViewActive)
                val itemTouchHelper = ItemTouchHelper(swipeController)
                itemTouchHelper.attachToRecyclerView(recyclerViewActive)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                println("loadPost:onCancelled ${databaseError.toException()}")
            }
        }
        postRef.addValueEventListener(taskListenerActive)

        //---------------------------------------------------------------------------------------

        var tasklistComplete = ArrayList<Task>()

        val taskListenerComplete = object : ValueEventListener {
            @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
            override fun onDataChange(p0: DataSnapshot) {
                tasklistComplete.clear()
                countcompleted = 0

                if (p0.exists()) {
                    val re = Regex("[^A-Za-z0-9 ]")

                    for (snapshot in p0.children) {
                        val taskitem = snapshot.getValue(Task::class.java)

                        if (taskitem!!.category == category && taskitem!!.status == "C") {
                            taskitem!!.key = snapshot.key.toString()
                            tasklistComplete.add(taskitem!!)
                            countcompleted += 1

                            val budgetamount = re.replace(taskitem.budget, "").dropLast(2)
                            sumbudget += budgetamount.toFloat()
                        }
                    }
                }

                //        pass the values to RvAdapter
                val rvAdapter = Rv_TaskAdapterComplete(tasklistComplete)
//        set the recyclerView to the adapter
                recyclerViewComplete.adapter = rvAdapter

                val swipeController = SwipeControllerCompleteTasks(inf.context, rvAdapter, recyclerViewComplete)
                val itemTouchHelper = ItemTouchHelper(swipeController)
                itemTouchHelper.attachToRecyclerView(recyclerViewComplete)

                inf.tasktextView16.text = countactive.toString()
                inf.tasktextView17.text = countcompleted.toString()

                val formatter = DecimalFormat("$#,###.00")
                inf.tasktextView18.text = formatter.format(sumbudget)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                println("loadPost:onCancelled ${databaseError.toException()}")
            }
        }
        postRef.addValueEventListener(taskListenerComplete)
        return inf
    }
}
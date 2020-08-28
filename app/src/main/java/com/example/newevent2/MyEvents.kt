package com.example.newevent2

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.my_events.*
import javax.xml.transform.Source


/*interface MyCallback {
    fun onCallback(value: Event?)
}
*/
class MyEvents : AppCompatActivity() {

    companion object {
        private val TAG = "ClassName"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.my_events)

//        initialize the recyclerView from the XML
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
//        Initializing the type of layout, here I have used LinearLayoutManager you can try GridLayoutManager
//        Based on your requirement to allow vertical or horizontal scroll , you can change it in  LinearLayout.VERTICAL
        //recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        //val layoutmanager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, true)
        //layoutmanager.stackFromEnd = true

        recyclerView.apply {
            layoutManager = LinearLayoutManager(this@MyEvents).apply {
                stackFromEnd = true
                reverseLayout = true
            }
        }

        //recyclerView.layoutManager = layoutmanager

//        Create an arraylist
//        val dataList = ArrayList<Model>()

        val database = FirebaseDatabase.getInstance()
        val myRef = database.reference
        //FirebaseUser user = mAuth.getCurrentUser();
        val postRef = myRef.child("User").child("Event")
        var eventlist= ArrayList<Event>()

        val eventListener = object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()) {

                    for (snapshot in p0.children) {
                        val eventitem = snapshot.getValue(Event::class.java)
                        eventitem!!.key=snapshot.key.toString()
                        eventlist.add(eventitem!!)
                    }
                }

                //        pass the values to RvAdapter
                val rvAdapter = RvAdapter(eventlist)
//        set the recyclerView to the adapter
                recyclerView.adapter = rvAdapter

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
        postRef.addValueEventListener(eventListener)

        /*
    dataList.add(Model("Phone", 1))
    dataList.add(Model("Watch", 2))
    dataList.add(Model("Note", 3))
    dataList.add(Model("Pin", 4))
     */

        floatingActionButton.setOnClickListener()
        {
            val newevent = Intent(this, MainActivity::class.java)
            startActivity(newevent)
        }
    }

/*    override fun onCallback(value: Event?) {
        val eventdb = Event()
        eventdb.name = value!!.name.toString()
        eventdb.date = value!!.date.toString()
        eventdb.time = value!!.time.toString()
        eventlist.add(eventdb)
    }
*/
}

